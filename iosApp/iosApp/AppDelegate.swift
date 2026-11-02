import UIKit
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // GoogleService-Info.plist comes from the Firebase console (iOS app com.ovasta.logisticsapp)
        // and is not committed, like androidApp/google-services.json
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
        } else {
            print("GoogleService-Info.plist is missing, so Firebase is not configured")
        }
        return true
    }
}
