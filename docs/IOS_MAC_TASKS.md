# iOS work for the Mac session

The Windows side (Phases 1–4 and the shared part of 5) is done. Everything below needs a Mac
with Xcode. `docs/KMP_MIGRATION_PLAN.md` has the design behind each item.

The first Mac pass (2026-11-04) got `sharedKit` to compile and link for the simulator (task 1).
The Xcode app itself hasn't been built yet: it needs CocoaPods and `GoogleService-Info.plist`.

## 0. One-time setup
- [ ] Install Xcode, `brew install xcodegen cocoapods`, and JDK 17 (JDK 21 also runs the build).
- [ ] In the Firebase console, add the iOS app `com.ovasta.logisticsapp`:
  - put its `GoogleService-Info.plist` in `iosApp/iosApp/`; git ignores it
  - upload the APNs auth key
- [x] In `iosApp/Podfile`, pin the Firebase pods to the Firebase iOS SDK version that GitLive
      2.7.0 was built against: `~> 11.8.0` (`firebase-cocoapods` in GitLive's
      `libs.versions.toml`). Unpinned, CocoaPods would pick 12.x.
- [ ] Run `cd iosApp && xcodegen && pod install` and open `iosApp.xcworkspace`.
- [ ] Get a 1024px app icon into `Assets.xcassets/AppIcon.appiconset`. Signing is Automatic for
      now (team `H8X427WWQW`); fastlane/match comes in Phase 6.
- [ ] Simulators: Apple Silicon only. There is no `iosX64` target, because JetBrains
      navigation3 isn't published for it.

## 1. Compile the iOS code written without a compiler
✅ `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` builds. What it took:
- Kotlin 2.2.20 → 2.3.20. The iOS klibs of JetBrains navigation3 1.1.2 are built with 2.3.20,
  and Kotlin/Native 2.2.20 skips them ("incompatible ABI version"). Android builds with the older
  Kotlin anyway, so only an iOS build shows it. Kotlin 2.3 also makes `kotlinOptions { jvmTarget }`
  an error, so `:androidApp` sets it through `compilerOptions`.
- `DataTypeExt.ios.kt`: Kotlin/Native imports the rounding mode as `NSRoundingMode.NSRoundPlain`.
- `FirestoreExt.ios.kt`: GitLive's `ios` accessor needs `@OptIn(ExperimentalForeignApi::class)`.
- `sharedKit` is a static framework. Linked as a dynamic one it fails with
  `framework 'FirebaseCore' not found`, because the Firebase frameworks only exist in the app's
  pods. The seller app never hit this: it doesn't use Firestore or Auth on iOS.
- `gradlew` is executable in git (mode 755). It was committed as 644, so `./gradlew`, and with it
  the Xcode build phase that runs `embedAndSignAppleFrameworkForXcode`, failed with "permission
  denied".
- `DimensExtTest` (commonTest) uses `kotlin.test` instead of JUnit, which only exists on the JVM.
- `project.yml` sets `TARGETED_DEVICE_FAMILY: "1"` (iPhone only). xcodegen's default, iPhone and
  iPad, would mean iPad review and iPad screenshots.

These files were written on Windows, where iOS targets don't build. They compile now, but
none has run yet:

| File (`shared/src/iosMain/kotlin/com/ovasta/logisticsapp/`) | What it does |
|---|---|
| `base/PlatformAction.ios.kt` | tel:, Google/Apple Maps, wa.me, settings URL, idle timer, language-change notification |
| `base/ext/DataTypeExt.ios.kt` | `formatDecimal` with `NSDecimalNumber` (half-up) |
| `base/ext/NumberFormatExt.ios.kt` | `formatGrouped`, `formatMonthYear` with Foundation formatters |
| `base/ext/ToastEventHandler.ios.kt` | logs toasts (see task 5) |
| `base/platform/ImagePicker.ios.kt` | placeholder (see task 4) |
| `base/platform/PlatformUi.ios.kt` | uptime clock, smallest screen width, no dynamic color |
| `data/remote/FirestoreExt.ios.kt` | `received_at` presence (NSNull counts as absent) |
| `data/remote/IosHttpEngine.kt` | Ktor Darwin engine |
| `data/setting/data/datastore/FileSessionStore.kt` | session JSON in Application Support, protected until first unlock |
| `shared/MainViewController.kt`, `shared/Platform.ios.kt` | placeholder screen |

- [ ] Run the shared tests on iOS: `./gradlew :shared:iosSimulatorArm64Test`. The tests
      compile, but the test executable fails to link with `framework 'FirebaseCore' not found`.
      Unlike the framework, it can't leave Firebase to Xcode. It needs the Firebase frameworks on
      its linker path (for example `linkerOpts("-F<dir>")` on the test binary, pointing at
      `iosApp/Pods` after `pod install`), or CI skips this task and builds the app instead.

## 2. Koin on iOS, then show the real app
Add `fun initKoin()` in `iosMain`. It starts `networkModule`, `sessionModule`, `firebaseModule`,
`trackingModule` and `viewModelModule`, plus iOS bindings for every type in
`KoinModulesTest.platformBindings`:

| Binding | iOS implementation |
|---|---|
| `HttpClientEngine` | `createIosHttpEngine()` |
| `NetworkErrorMessages` | the `timeout_error_message` / `generic_unknown_error` Res strings, read once at start (`getString` is suspend) |
| `SessionStore` | `FileSessionStore()` |
| `PushTokenProvider` | the FCM token the AppDelegate hands over (task 3) |
| `TrackingKeepAlive` | the `CLLocationManager` tracker from task 6 |
| `Logger` | optional: Ktor request logging, debug builds only, as on Android |
| `OrderAlarm` | `AVAudioPlayer` looping `new_order_alert.caf` (`numberOfLoops = -1`), `AVAudioSession` category `.playback` |
| `DeviceLocation` | `CLLocationManager`: `requestLocation()` for the current fix, `locationServicesEnabled()`, authorization status for the permission |
| `SeenTaskStore` | `NSUserDefaults` key `seen_task_ids` |
| `LocationTrackingSwitch` | stops the tracker from task 6 |
| `AppLanguageStore` | `NSUserDefaults` plus `AppleLanguages` (the seller app's `LanguageApplier`) |

Then:
- [ ] After starting Koin, `initKoin()` calls `SessionHeaderCache.initialize(sessionStore)`, as
      `LogisticsApp.onCreate` does on Android. `AccessTokenProvider` reads the token from that
      cache. Without it, every request after a relaunch goes out without a token, gets a 401,
      and the 401 handler logs the driver out.
- [ ] Make `MainViewController()` call `App()` instead of the placeholder.
- [ ] Call `initKoin()` in `AppDelegate.application(_:didFinishLaunchingWithOptions:)`, right after
      `FirebaseApp.configure()` (`MainViewControllerKt.doInitKoin()`, or wherever `initKoin` ends
      up). Not in `iOSApp.init()`: SwiftUI runs that before the AppDelegate, so Firebase isn't
      configured yet. It would only work while nothing resolves Firestore or Auth at startup.
- [ ] In `ContentView`, recreate `ComposeView` on `LANGUAGE_CHANGED_NOTIFICATION`, the way the seller app does.

## 3. Push and the custom sound
- [ ] Convert the sound and add it to the app bundle:
      `afconvert androidApp/src/main/res/raw/new_order_alert.mp3 iosApp/iosApp/new_order_alert.caf -d ima4 -f caff -v`.
      It must be ≤ 30 s; pad it to about 25 s for an alarm-like effect.
- [ ] Use the seller app's AppDelegate pattern:
  - [ ] permission and `registerForRemoteNotifications`, with the APNs token passed to `Messaging`
  - [ ] the FCM token passed into Kotlin for `PushTokenProvider` and `SettingsRepository.updateFcmToken`
  - [ ] `willPresent` returns `[.banner, .sound]`; just `[.banner]` while Home's in-app alarm is playing
  - [ ] `didReceive` calls `PendingOrderLink.shared.onNotificationTapped(type:orderId:)` with the
        push's `data["type"]` and `data["order_id"]`. `AppNavHost` opens the order; a `new_order`
        only opens the app.
  - [ ] At launch, when a user is logged in and no token is cached, fetch the FCM token and
        upload it. This matches `checkAndSendFcmToken()` in the Android `LogisticsApp`; the
        refresh callback alone misses drivers whose token never changes.
- [ ] Xcode capabilities: Push Notifications, Background Modes (location, remote notifications),
      Time Sensitive Notifications. `iosApp.entitlements` already declares these.
- [ ] Backend: new-order pushes send `apns.payload.aps.sound = "new_order_alert.caf"` and
      `interruption-level: "time-sensitive"`, plus `data.type` and `data.order_id`. The full
      payload is in the plan doc.

## 4. Image picker
Replace the placeholder in `ImagePicker.ios.kt`:
- [ ] `PHPickerViewController` for the gallery and `UIImagePickerController` for the camera,
      presented from the key window's root view controller.
- [ ] Encode with `UIImageJPEGRepresentation`, lowering the quality and then the size until the
      result is under `maxBytes`, as `compressImageToJpeg` does on Android.

## 5. In-app toast
iOS has no system toast. Show `ToastMsg` in an overlay (for example a `CompositionLocal` host
in `App()`) from `rememberPlatformToaster()`. Then show the "not available" messages in
`PlatformAction.ios.kt` too.

## 6. Location tracking
The shared half is done (task 6 on Windows):
- `LocationTracker.track(fixes)` does the 10 m filter, the Firestore write with its 15 s
  timeout, and the reports to `LocationTrackingMonitor` (covered by `LocationTrackerTest`).
- `HomeRepository` is in `trackingModule`. Its start and stop go to the platform's
  `TrackingKeepAlive`, which on Android starts and stops `LocationTrackerService`.

On iOS:
- [ ] Bind `TrackingKeepAlive` to a tracker that runs `CLLocationManager` and passes its fixes
      to `LocationTracker.track(...)` in a coroutine. `stop()` cancels that coroutine and calls
      `LocationTrackingMonitor.onTrackingStopped()`.
- [ ] `CLLocationManager` settings:
  - [ ] `desiredAccuracy = kCLLocationAccuracyBest`, `distanceFilter = 10.0`, `activityType = automotiveNavigation`
  - [ ] `allowsBackgroundLocationUpdates = true`, `pausesLocationUpdatesAutomatically = false`, `showsBackgroundLocationIndicator = true`
  - [ ] "When In Use" permission only; start tracking while the app is in the foreground
- [ ] Stationary drivers: with `distanceFilter = 10`, iOS sends no fixes while the phone isn't
      moving, and the monitor blocks order actions after 60 s without a fix. Call
      `requestLocation()` every 30 s and pass that fix on too. Android needs nothing here: its
      fused fixes keep arriving every 10 s while stationary.
- [ ] Show a one-time hint: "Don't close the app while online". Force-quitting stops tracking
      until the next launch.

## 7. Permission requests
Android asks for location (fine and coarse) and notifications in `MainActivity` at every
launch. iOS needs the same, so nothing works on first launch before the driver goes online:
- [ ] At launch: `CLLocationManager.requestWhenInUseAuthorization()`, plus the notification
      request from task 3.
- [ ] Camera: ask when the camera opens (task 4). The usage string is in `Info.plist` already.
      The gallery needs no permission: `PHPickerViewController` runs outside the app and only
      returns the photos the driver picks.
- [ ] If location is denied, `LocationBlockedDialog` already offers the Settings button through
      `PlatformAction.OpenAppSettings`.

## 8. Crashlytics and the privacy manifest
- [ ] Crashlytics user info: Android sets the user id, name and district from
      `CrashlyticsUserInfoUseCase`. Add the same on iOS through a Kotlin → Swift bridge, the
      seller app's `CrashlyticsBridge` pattern.
- [ ] Add the Crashlytics `run` script build phase to `project.yml` so dSYMs are uploaded;
      without it, iOS crash reports aren't symbolicated.
- [ ] Add `iosApp/iosApp/PrivacyInfo.xcprivacy`, which App Store Connect requires. Declare the
      required-reason APIs:
  - `NSPrivacyAccessedAPICategoryUserDefaults` (CA92.1): language, seen orders
  - `NSPrivacyAccessedAPICategorySystemBootTime` (35F9.1): `elapsedRealtimeMillis` uses the uptime
  - `NSPrivacyAccessedAPICategoryFileTimestamp` (C617.1), if Ktor or the session file reads it

  Also list the collected data types: precise location, user ID, phone number, crash data.
  The Firebase pods ship their own manifests.

## 9. App Store submission
- [ ] Review notes: drivers share their location only while online, for dispatch and delivery
      tracking (Guideline 2.5.4). Add a demo driver account that can go online.
- [ ] TestFlight with a few drivers first.

## 10. Test on a real device
These don't work fully on the simulator:
- [ ] Go online, lock the phone, and move for 15 minutes. Firestore `online_drivers` must keep
      updating, and the blue location pill must show.
- [ ] Send the push payload to the app while it's killed, in the background, in the foreground,
      with the silent switch on, and in a Focus mode. Check the sound and that tapping opens
      the order.
- [ ] Check the in-app alarm loops on Home, and run through login, orders, photos, wallet,
      language and logout.
