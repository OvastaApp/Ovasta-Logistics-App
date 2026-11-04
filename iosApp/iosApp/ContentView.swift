import SwiftUI
import UIKit
import sharedKit

struct ContentView: View {
    // A new id makes SwiftUI build a new ComposeView, which reads the saved language again
    @State private var composeViewId = UUID()

    var body: some View {
        ComposeView()
            .id(composeViewId)
            .ignoresSafeArea(.all)
            .onReceive(
                NotificationCenter.default.publisher(
                    for: Notification.Name(PlatformAction_iosKt.LANGUAGE_CHANGED_NOTIFICATION)
                )
            ) { _ in
                composeViewId = UUID()
            }
    }
}

/// Hosts the shared Compose UI from :shared (MainViewController.kt).
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
