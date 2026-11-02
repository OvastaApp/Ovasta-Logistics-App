# LogisticsApp → Kotlin Multiplatform (Android + iOS)

Updated 2026-11-02, replacing the 2026-07-16 draft. Modeled on Ovasta-Seller-App (`StudioProjects/Ovasta-Seller-App`).

## Context
The driver app (single `:app` module, ~202 Kotlin files, 100% Jetpack Compose, Koin, MVVM/MVI) has to ship on iOS. This plan refines the 2026-07-16 draft, which was modeled on Ovasta-Seller-App, and adds the two areas the draft skipped: **iOS location tracking** and the **custom new-order sound**.

Decisions made:
- iOS tracking matches Android. It runs while the driver is online and the app is alive, including in the background. It needs "When In Use" permission only, and there is no relaunch after a force-quit.
- The backend will send platform-specific FCM payloads, including the APNs sound.

Gaps found in the existing plan:
1. The seller app's iOS `FirebaseAuthProvider` and `FirestoreProvider` are **stubs** (`println` / TODO, and the Podfile has only Messaging and Crashlytics). Copying them "verbatim" would break Home and OrderDetails on iOS, because both run on Firestore snapshot listeners. Firestore therefore needs a real multiplatform solution.
2. The sdp/ssp count is 32 files, not 24. Some `java.*` APIs block `commonMain`:
   - `SimpleDateFormat` and `java.time` in `DateExt.kt` and `PartnerStatisticsSection.kt`
   - `NumberFormat` in `PriceExt.kt`
   - `String.format`
3. There is an existing Android bug that the migration should fix. `FcmService` picks the background channel whenever `isAtLeast(CREATED)` is true, which is effectively always. As a result, the custom sound is never used for messages handled in code.

## Target shape
Keep the existing plan's layout: `:shared` (commonMain/androidMain/iosMain + composeResources), a thin `:androidApp`, and `iosApp/` (xcodegen `project.yml`, Podfile and fastlane copied from the seller app). Framework name `sharedKit`, iOS deployment target 15.0.

Library choices:
| Concern | Android today | Shared choice |
|---|---|---|
| HTTP | Retrofit + OkHttp + Gson | Ktor (OkHttp / Darwin engines) + kotlinx.serialization |
| Firestore, Auth | Firebase Android SDK | **GitLive `dev.gitlive:firebase-firestore` / `firebase-auth`** in commonMain (already in the seller app's catalog, v2.1.0+). iOS links Firebase pods `FirebaseFirestore` and `FirebaseAuth`. |
| Messaging, Crashlytics | Firebase Android | Native on each platform (Swift `AppDelegate` like the seller app), with token and user data bridged into Kotlin |
| Dates, numbers | `SimpleDateFormat`, `java.time`, `NumberFormat` | kotlinx-datetime plus a small `expect fun formatDecimal()` |
| Storage | DataStore + Keystore `Crypto.kt` + EncryptedSharedPreferences | DataStore-core (KMP) + seller app `SecureStorage` (Keychain on iOS) |
| Images | Coil 2 and 3 | Coil 3 + `coil-network-ktor` (drop Coil 2) |
| Navigation | Navigation3 | JetBrains' multiplatform Navigation3 (`org.jetbrains.androidx.navigation3`, stable 1.1.x on CMP 1.10), which keeps `AppNavHost` almost unchanged. |
| Strings | `res/values`, `values-ar` (about 305 strings) | `composeResources/values{,-ar}`, generated `Res` |
| Camera / gallery | `TakePicture` / `PickVisualMedia` + FileProvider | `expect` image picker that returns a `ByteArray`. iOS uses `PHPickerViewController` / `UIImagePickerController`. Multipart upload takes bytes instead of a `Uri`. |
| Geocoder | `android.location.Geocoder` | `expect` reverse geocoder, `CLGeocoder` on iOS |

## Phases
1. **Pre-work on Android, still in `:app`, shipped as normal releases.** This lowers risk before any module move.
   - ✅ Gson → kotlinx.serialization on all models (`apiJson` in `RemoteModule.kt`, covered by `ApiJsonTest`).
   - ✅ Dates moved to kotlinx-datetime. Still left: `NumberFormat`, `String.format` and the localized month name. They become an `expect` formatter in Phase 4.
   - ✅ Removed `LiveData` / `SingleLiveEvent`, Material 1, Coil 2 and `android.arch.lifecycle:extensions`.
   - ✅ sdp/ssp replaced by `Int.sdp` / `Int.ssp` in `base/ext/DimensExt.kt`, which use the same scaling and are covered by `DimensExtTest`. In Phase 4 only the smallest-width lookup becomes `expect`, since `LocalConfiguration` is Android-only; iOS uses the screen's smallest width in points.
   - ✅ Removed `@Parcelize` and the parcelize plugin. No model went through a Bundle or `rememberSaveable`.
   - ✅ Fixed the `com.maxab.basemodule...` package declarations (`Country`, `RemoteConfigModel`, `LogoutListener`).
2. **Skeleton.**
   - ✅ Renamed `:app` → `:androidApp` (`applicationId com.ovasta.logisticsapp` unchanged) and added `:shared`, with Android and iOS targets and the `sharedKit` framework.
   - ✅ Versions: Kotlin 2.2.20 and Compose Multiplatform 1.10.3, the release JetBrains Navigation3 1.1.x is built on, so `AppNavHost` keeps Navigation3. This lifts the Android app's androidx Compose from 1.9.5 to 1.10.5. AGP goes from 8.11.1 to 8.13.2, because with Kotlin 2.2.20 AGP 8.11 leaves the app classes off the unit-test compile classpath.
   - ✅ `DateExt` is in `shared/commonMain`, with its test in `androidUnitTest`. This proves the Android app consumes shared code. `App()` and `MainViewController()` are placeholders for the iOS app.
   - ✅ `iosApp/`: xcodegen `project.yml`, a Podfile with the Firebase pods, a SwiftUI entry and Info.plist. The Info.plist has location and camera texts in ar/en and the background modes location and remote-notification. The entitlements cover push and time-sensitive notifications. The framework is built by `embedAndSignAppleFrameworkForXcode`, which also copies Compose resources.
   - Ktor, Koin, GitLive, Coil 3 for KMP and DataStore-core enter the catalog in the phase that first uses them, so each version is checked by a real build.
   - Still needed before iOS builds:
     - a Mac (iOS targets don't build on Windows)
     - an iOS app `com.ovasta.logisticsapp` in the Firebase console, with its `GoogleService-Info.plist`
     - a 1024px app icon
     - fastlane/match signing, done in Phase 6
3. **Data layer to commonMain.**
   - Rewrite the Retrofit APIs as Ktor: Login, Home, Settings, OrderDetails, Wallet.
   - Port interceptors to Ktor plugins (headers, error mapping, cache).
   - Move the Firestore data sources (`HomeFirebaseRemoteDataSource`, `OrderDetailsRemoteDataSource`) to GitLive: `snapshots` Flow, `set(merge = true)`, `FieldValue.serverTimestamp`, `@Serializable` models instead of `@PropertyName` / `toObject`.
   - Firebase custom-token sign-in uses GitLive `signInWithCustomToken`.
4. **UI to commonMain.** Move theme, components, the 8 screens, about 12 sheets and dialogs, and the ViewModels (KMP `lifecycle-viewmodel` + `koin-compose-viewmodel`). Replace `R.*` with `Res.*`. Move `LocaleHelper` to the seller app's `LanguageApplier` (Arabic default, RTL).
5. **Platform features:** tracking and notifications, detailed below.
6. **Thin apps.**
   - Android: Application, MainActivity, `FcmService`, `LocationTrackerService`, manifest.
   - iOS: `AppDelegate`, `MainViewController`, capabilities.
7. **Verification:** see the end of this plan.

## Location tracking design
Move the logic into common code and keep only the "location source + keep-alive" part per platform.

**commonMain**
- `interface LocationSource { fun updates(): Flow<GeoFix>; suspend fun current(): GeoFix?; fun permissionState(): LocationPermission; fun servicesEnabled(): Boolean }`
- `LocationTracker` (common class, Koin singleton) takes over the logic in `LocationTrackerService.kt`:
  - the 10 m distance filter with `lastLoggedLocation` updated only on success
  - the Firestore write to `districts/{districtId}/online_drivers/{deliveryId}` (merge + serverTimestamp, 15 s timeout)
  - reporting to the monitor
- It exposes `start()` / `stop()`, which call into `expect class TrackingKeepAlive` (start or stop the platform mechanism).
- `LocationTrackingMonitor` moves to common as is: `PERMISSION_DENIED`, `LOCATION_DISABLED`, `TRACKING_OFF`, `LOGGING_NOT_WORKING`, 60 s stale check, 2 s recheck. Its permission and provider checks go through `LocationSource`. `ensureLocationTracking()` in `BaseViewModel` stays unchanged for every order action.
- `HomeViewModel` start/stop triggers (online toggle, consent dialog, restore on launch, logout) stay as they are.

**Android actual.** `LocationSource` wraps the existing `FusedLocationProviderClient` code: HIGH_ACCURACY, 10 s interval, 7 s fastest. `TrackingKeepAlive` starts the existing foreground service (`START_STICKY`, `foregroundServiceType=location`). The service then only hosts the coroutine that collects `LocationTracker`.

**iOS actual (`CLLocationManager` from Kotlin/Native, wrapped in `callbackFlow`)**
- Manager settings:
  - `desiredAccuracy = kCLLocationAccuracyBest`
  - `distanceFilter = 10.0`, the native equivalent of the Android 10 m filter, which saves battery
  - `activityType = CLActivityTypeAutomotiveNavigation`
  - `allowsBackgroundLocationUpdates = true`
  - `pausesLocationUpdatesAutomatically = false`, so iOS doesn't pause tracking while a driver waits at a pickup
  - `showsBackgroundLocationIndicator = true`, the blue pill and the iOS counterpart of the Android foreground notification
- **Start tracking only while the app is in the foreground**, from the online toggle or the restore on launch. iOS then keeps delivering fixes in the background with "When In Use" permission, the same rule Android enforces for foreground services.
- `TrackingKeepAlive` on iOS = `startUpdatingLocation` / `stopUpdatingLocation`. On iOS 17+, optionally hold a `CLBackgroundActivitySession` through a small Swift helper; skip that on iOS 15–16.
- Stationary drivers: iOS sends no fixes when nobody moves. The common monitor's 60 s stale rule would then wrongly block order actions. Fix: when the latest fix is older than 60 s but still accurate, the iOS source re-emits it (or calls `requestLocation()`). Alternatively, reset staleness on a heartbeat that `LocationTracker` sends every 30 s, a merge write of `timestamp` only. Apply the same behavior on both platforms so the backend sees one consistent signal.
- Permissions:
  - `requestWhenInUseAuthorization`, with `NSLocationWhenInUseUsageDescription` in Arabic and English (`ar.lproj` / `en.lproj` InfoPlist.strings)
  - Also declare `NSLocationAlwaysAndWhenInUseUsageDescription`, which App Store review expects
  - The "Open settings" button in `LocationBlockedDialog` → `UIApplicationOpenSettingsURLString`. iOS has no direct "location services" screen, so both buttons go to app settings on iOS.
- Info.plist `UIBackgroundModes`: `location`, `remote-notification`.
- App Store notes:
  - Guideline 2.5.4 requires a clear driver-tracking justification in the review notes, plus a demo account that can go online.
  - Firestore writes keep working in the background because location updates keep the process alive.
- Accepted limitation (as decided): if the driver force-quits, tracking stops until the next launch, and `HomeViewModel` restores it if the server says online. Show a one-time hint: "Don't close the app while online."

## Notifications and custom sound design
**The sound file**
- iOS cannot use MP3 for notification sounds. Convert once and commit:
  `afconvert new_order_alert.mp3 new_order_alert.caf -d ima4 -f caff -v`
  - The clip must be ≤ 30 s, or iOS plays the default sound instead. Since push sounds can't loop, consider padding or repeating the clip to about 25 s for an alarm-like effect.
- Put `new_order_alert.caf` in the iOS app bundle root (the `iosApp/iosApp/Resources/` sources in `project.yml`).
- Android keeps `res/raw/new_order_alert.mp3` in `:androidApp` or the `:shared` androidMain res.

**Backend payload (FCM HTTP v1) for new-order pushes**
```json
{
  "message": {
    "token": "...",
    "notification": { "title": "...", "body": "..." },
    "data": { "type": "new_order", "order_id": "123" },
    "android": { "priority": "HIGH", "notification": { "channel_id": "delivery_task_channel_v2", "sound": "new_order_alert" } },
    "apns": {
      "headers": { "apns-priority": "10", "apns-push-type": "alert" },
      "payload": { "aps": { "sound": "new_order_alert.caf", "interruption-level": "time-sensitive" } }
    }
  }
}
```
- `time-sensitive` breaks through Focus modes. It needs the **Time Sensitive Notifications** capability, which Apple does not have to approve. Do not plan on Critical Alerts: that entitlement is Apple-approved and normally refused for delivery apps.
- Also add `type` and `order_id` to `data` so a tap can deep-link to TaskDetails on both platforms (there is no deep link today).
- The token upload (`POST fcm-token`) stays the same. Optionally add `platform: "ios" | "android"` so the backend can target payloads.

**iOS app side (Swift `AppDelegate`, based on the seller app)**
- `requestAuthorization([.alert, .sound, .badge])` and APNs → `Messaging.apnsToken`.
- In `messaging(didReceiveRegistrationToken:)`, forward the token through a Kotlin bridge object, the way the seller app's `CrashlyticsBridge` works, so the shared `SettingsRepository.updateFcmToken` uploads it.
- `willPresent`: in the foreground, return `[.banner, .sound]` so the custom sound plays. If Home is visible, the in-app alarm takes over: return `[.banner]` and let `AlarmPlayer` loop.
- `didReceive` (tap): pass `order_id` to shared navigation.
- Xcode capabilities: Push Notifications, Background Modes (location, remote notifications), Time Sensitive Notifications. Upload the APNs auth key to the Firebase console.

**In-app looping alarm (foreground only, same as Android today)**
- `expect class AlarmPlayer { fun start(); fun stop() }`
  - Android: the existing `OrderAlarmSound` (MediaPlayer, `USAGE_ALARM`, looping).
  - iOS: `AVAudioPlayer` with `numberOfLoops = -1` on the bundled `.caf`, and `AVAudioSession` category `.playback` so it plays even with the silent switch on.
- `expect fun keepScreenOn(on: Boolean)`
  - Android: the current `ScreenFlashOverlay` flags.
  - iOS: `UIApplication.sharedApplication.idleTimerDisabled`. iOS cannot turn the screen on or show over the lock screen; the time-sensitive push covers that case.
- The new-task queue logic (Firestore listener, 30 s countdown, seen-IDs store) moves to common unchanged. Seen IDs move from SharedPreferences to DataStore.

**Android fix included in the migration**
- In `FcmService`, drop the broken `isAtLeast(CREATED)` branch and always post new-order messages to `delivery_task_channel_v2`.
- Read `type` and `order_id` and add them to the tap `PendingIntent`.
- Optionally wire the unused `OrderVibrator` into the in-app alarm.

## Critical files
- Tracking: `base/services/LocationTrackerService.kt`, `LocationManager.kt`, `LocationTrackingMonitor.kt`, `presentation/home/data/HomeRepository.kt`, `HomeFirebaseRemoteDataSource.kt`, `presentation/home/presentation/HomeViewModel.kt`, `components/LocationBlockedDialog.kt`
- Notifications: `base/services/FcmService.kt`, `app/LogisticsApp.kt` (channels), `base/ext/OrderAlarmSound.kt`, `base/ext/OrderVibrator.kt`, `HomeScreen.kt` (`ScreenFlashOverlay`), `res/raw/new_order_alert.mp3`
- Blockers: `base/ext/DateExt.kt`, `base/ext/PriceExt.kt`, `PartnerStatisticsSection.kt`, `base/encryption/Crypto.kt`, `base/ext/ImageExt.kt`, `base/di/RemoteModule.kt`, `firebaseModule.kt`
- Reuse from the seller app (`Ovasta-Seller-App/shared/src/{android,ios}Main/.../data/platform/`): `DataStoreProvider`, `SecureStorage`, `PermissionManager` (extend with location), `HapticFeedback`, `HttpClientEngine`, `LanguageApplier`, `CrashlyticsLogger` + `CrashlyticsBridge`, `iosApp/project.yml`, `AppDelegate.swift`, Podfile (add `FirebaseFirestore` and `FirebaseAuth`)

## Verification
1. **After each phase:** build Android and run `./gradlew :androidApp:assembleDebug test`. Regression-test login → go online → accept task → order details (pick, edit, deliver, photo upload) → wallet → go offline → logout. Behavior must match production before any iOS work.
2. **Android tracking:** with the app backgrounded and the screen off for 10 minutes, confirm the `online_drivers` doc `timestamp` keeps advancing while driving and the heartbeat fires while stationary. Kill location services and confirm `LocationBlockedDialog` appears.
3. **iOS simulator:** `xcodegen && pod install`, build, then run login plus the Firestore Home and OrderDetails streams. Use Simulator ▸ Features ▸ Location ▸ "Freeway Drive" for tracking.
4. **iOS real device (required for push and background location):**
   - Go online, lock the phone, drive or walk for 15 minutes, and check that Firestore updates continue and the blue pill shows.
   - Send the FCM v1 payload above with the app killed, in the background, in the foreground, with the silent switch on, and in a Focus mode. Confirm the custom sound plays and a tap opens the order.
   - Confirm the in-app alarm loops on Home.
5. **TestFlight beta** with a few drivers before App Store submission. Include review notes about background location.
