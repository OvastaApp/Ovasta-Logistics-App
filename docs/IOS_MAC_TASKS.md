# iOS work for the Mac session

The Windows side (Phases 1–4 and the shared part of 5) is done. Everything below needs a Mac
with Xcode. `docs/KMP_MIGRATION_PLAN.md` has the design behind each item.

## 0. One-time setup
- [ ] Install Xcode, `brew install xcodegen cocoapods`, and JDK 17.
- [ ] In the Firebase console, add the iOS app `com.ovasta.logisticsapp`:
  - put its `GoogleService-Info.plist` in `iosApp/iosApp/`; git ignores it
  - upload the APNs auth key
- [ ] In `iosApp/Podfile`, pin the Firebase pods to the Firebase iOS SDK version that GitLive
      2.7.0 was built against (see GitLive's release notes). Then run
      `cd iosApp && xcodegen && pod install` and open `iosApp.xcworkspace`.
- [ ] Get a 1024px app icon into `Assets.xcassets/AppIcon.appiconset`. Signing is Automatic for
      now (team `H8X427WWQW`); fastlane/match comes in Phase 6.
- [ ] Simulators: Apple Silicon only. There is no `iosX64` target, because JetBrains
      navigation3 isn't published for it.

## 1. Compile the iOS code written without a compiler
Run `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` and fix what breaks. These files
were written on Windows, where iOS targets don't build:

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

Also run the shared tests on iOS: `./gradlew :shared:iosSimulatorArm64Test`.

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
| `IHomeRepository` | see task 6: move the Android `HomeRepository` logic to common first |
| `OrderAlarm` | `AVAudioPlayer` looping `new_order_alert.caf` (`numberOfLoops = -1`), `AVAudioSession` category `.playback` |
| `DeviceLocation` | `CLLocationManager`: `requestLocation()` for the current fix, `locationServicesEnabled()`, authorization status for the permission |
| `SeenTaskStore` | `NSUserDefaults` key `seen_task_ids` |
| `LocationTrackingSwitch` | stops the tracker from task 6 |
| `AppLanguageStore` | `NSUserDefaults` plus `AppleLanguages` (the seller app's `LanguageApplier`) |

Then:
- [ ] Make `MainViewController()` call `App()` instead of the placeholder.
- [ ] Call `initKoin()` from `iOSApp.init()` in Swift (`MainViewControllerKt.doInitKoin()`, or wherever `initKoin` ends up).
- [ ] In `ContentView`, recreate `ComposeView` on `LANGUAGE_CHANGED_NOTIFICATION`, the way the seller app does.

## 3. Push and the custom sound
- [ ] Convert the sound and add it to the app bundle:
      `afconvert androidApp/src/main/res/raw/new_order_alert.mp3 iosApp/iosApp/new_order_alert.caf -d ima4 -f caff -v`.
      It must be ≤ 30 s; pad it to about 25 s for an alarm-like effect.
- [ ] Use the seller app's AppDelegate pattern:
  - [ ] permission and `registerForRemoteNotifications`, with the APNs token passed to `Messaging`
  - [ ] the FCM token passed into Kotlin for `PushTokenProvider` and `SettingsRepository.updateFcmToken`
  - [ ] `willPresent` returns `[.banner, .sound]`; just `[.banner]` while Home's in-app alarm is playing
  - [ ] `didReceive` passes `order_id` to navigation (`TaskDetails(id)`)
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

## 7. Test on a real device
These don't work fully on the simulator:
- [ ] Go online, lock the phone, and move for 15 minutes. Firestore `online_drivers` must keep
      updating, and the blue location pill must show.
- [ ] Send the push payload to the app while it's killed, in the background, in the foreground,
      with the silent switch on, and in a Focus mode. Check the sound and that tapping opens
      the order.
- [ ] Check the in-app alarm loops on Home, and run through login, orders, photos, wallet,
      language and logout.
