# LogisticsApp → Compose Multiplatform (Android + iOS) Migration Plan

Modeled on the Ovasta-Seller-App migration (`C:\Users\m.farahat_maxab\StudioProjects\Ovasta-Seller-App`).
Written 2026-07-16.

## Current state

Single Android `:app` module (~167 Kotlin files, 45 composable files, 8 ViewModels):

- **Networking:** Retrofit + Gson + OkHttp interceptors (`HeadersInterceptor`, `ErrorMappingInterceptor`, `CacheHandlerInterceptor`)
- **DI:** Koin (android)
- **Navigation:** Navigation3 (`NavDisplay`, `AppNavHost.kt`)
- **Storage:** DataStore preferences, `security-crypto` encrypted prefs
- **Firebase:** Auth, Firestore (Home/OrderDetails realtime data), Messaging (`FcmService`), Crashlytics, Analytics
- **Location:** foreground `LocationTrackerService` + `play-services-location`
- **UI:** Compose M3, Coil (OkHttp), sdp/ssp in ~24 files, 2 LiveData usages
- **Features:** auth (splash/login), home, orderDetails, settings

## Target shape (same as seller app)

```
LogisticsApp/
├── shared/            # KMP module: androidTarget + iosX64/iosArm64/iosSimulatorArm64
│   └── src/
│       ├── commonMain/    # data, domain, di, ui (screens/components/theme), platform expects
│       │   └── composeResources/  # drawable, font, values, values-ar, values-en
│       ├── androidMain/   # actuals
│       └── iosMain/       # actuals
├── androidApp/        # thin: Application, MainActivity, FcmService, LocationTrackerService
└── iosApp/            # Xcode project: xcodegen project.yml, Podfile, fastlane, Swift entry
```

Shared framework: `sharedKit`, bundleId `com.ovasta.logistics.shared`.

## Phase 1 — Project skeleton

1. Create `:shared`, rename `:app` → `:androidApp`; update `settings.gradle.kts` (copy seller app's).
2. Version catalog: add kotlin-multiplatform, compose-multiplatform, ktor (core/content-negotiation/kotlinx-json/logging/okhttp/darwin), koin-core/koin-compose/koin-compose-viewmodel, coil3 + coil-network-ktor, datastore-preferences-core (KMP), lifecycle-viewmodel-compose (KMP). Copy versions from seller app catalog.
3. `shared/build.gradle.kts`: clone from seller app (iOS framework targets, compose.resources config with `publicResClass`).
4. Copy `iosApp/` scaffold from seller app (AppDelegate.swift, iOSApp.swift, ContentView, project.yml, Podfile, fastlane, ar.lproj/en.lproj); rebrand to Logistics; add this app's `GoogleService-Info.plist`.

## Phase 2 — Platform abstraction (expect/actual)

Copy from seller app nearly verbatim:

| Provider | Notes |
|---|---|
| DataStoreProvider | |
| DeviceInfoProvider | |
| SecureStorage | replaces `security-crypto`; Keychain on iOS |
| FirebaseAuthProvider | |
| FirestoreProvider | verify it covers Home's query/listener shapes |
| FirebaseMessagingProvider | |
| PermissionManager | add location-always permission for this app |
| HapticFeedback | |
| HttpClientEngine | OkHttp / Darwin |
| AppConfig, CrashlyticsLogger, LanguageApplier | |
| PlatformActions, PlatformBackHandler, PlatformModule | |

**New for this app:**

- `LocationTracker` (expect): `startTracking()`, `stopTracking()`, location `Flow`.
  - Android actual: existing foreground `LocationTrackerService` + play-services-location (service stays in `:androidApp`).
  - iOS actual: `CLLocationManager`, `allowsBackgroundLocationUpdates`, Info.plist `UIBackgroundModes: location` + `NSLocationAlwaysAndWhenInUseUsageDescription` / `NSLocationWhenInUseUsageDescription`.
- Push: FCM on Android as-is; iOS APNs → FCM through FirebaseMessagingProvider.

## Phase 3 — Data layer (Retrofit → Ktor)

1. Convert Retrofit interfaces to Ktor calls: `LoginApi`, `HomeApi`, `SettingsApi`, `OrderDetailsApi`, remote data sources.
2. Gson → kotlinx.serialization on all models: `@SerializedName` → `@SerialName`, add `@Serializable`. Firestore document mapping must not rely on Gson reflection — map explicitly or via serializers.
3. Interceptors → Ktor `defaultRequest` / response validation / logging plugins (seller app pattern).
4. Move repositories, `ApiResponse`, exception types to `commonMain` (already Flow/coroutine based).

## Phase 4 — UI to commonMain

1. Move theme, shared composables, screens (Splash, Login, Home, OrderDetails + bottom sheets) to `shared/commonMain/.../ui/screens`.
2. **Navigation3 → seller-app navigator:** Nav3 is Android-only; replace `AppNavHost`/`NavDisplay` with seller app's `NavHost.kt` + `AppScreens.kt` (data object/class routes, custom back stack).
3. **Remove sdp/ssp (~24 files):** replace with plain `dp`/`sp` (or seller app's scaling helper). Visual-regression risk — compare screens before/after.
4. LiveData (2 usages) → StateFlow / `collectAsState`. ViewModels → KMP `lifecycle-viewmodel-compose` + `koin-compose-viewmodel`.
5. Resources: `values/strings.xml` + `values-ar/strings.xml` → `commonMain/composeResources/values{,-ar,-en}`; `R.string` → generated Res class; `generateLocaleConfig` → `LanguageApplier`.

## Phase 5 — Thin platform apps

- `:androidApp`: Application + Koin startup, MainActivity hosting shared `App()`, FcmService, LocationTrackerService, manifest, google-services + crashlytics plugins.
- iOS: Koin init in `Main.ios.kt` / MainViewController, Firebase pods, APNs capability, background-location capability, fastlane from seller app.

## Phase 6 — Verification

1. Android first: build `:androidApp`; regression-test login → home → order details (picking/receiving) → background location. Must match pre-migration behavior before iOS work.
2. iOS: `xcodegen` + `pod install`, simulator build; then real-device tests for Firebase auth/Firestore streams, push, and background location (neither push nor background location fully works on simulator).

## Risk items

1. **iOS background location** — real-device testing; App Store review needs justification for the background mode.
2. **Firestore listeners on iOS** — confirm seller app's FirestoreProvider supports the snapshot-listener shapes used by Home/OrderDetails.
3. **sdp/ssp removal** — 24 UI files touched; screenshot-compare.
4. **Gson→kotlinx.serialization** — watch for models depending on Gson leniency (missing fields, type coercion).

## Pre-work

Finish and merge the in-flight `order_details` branch changes (OrderDetailsScreen, EditProductBottomSheet, ReceiveAmountBottomSheet) before Phase 4 moves those files.
