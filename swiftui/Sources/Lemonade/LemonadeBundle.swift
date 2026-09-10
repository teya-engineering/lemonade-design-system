import Foundation

/// Resolved exactly once, on first access.
///
/// This must not be recomputed per call: every generated colour token passes
/// `bundle: .lemonade`, so this accessor sits on the hot path of essentially
/// every view body. In framework builds the resolution below costs an ObjC
/// class-to-bundle lookup plus an `NSBundle` resource lookup that misses
/// (resources are compiled directly into the framework bundle, not into a
/// nested `Lemonade.bundle`), which is far too expensive to repeat.
private let lemonadeBundle: Bundle = {
    #if SWIFT_PACKAGE
    return .module
    #else
    return frameworkResourceBundle()
    #endif
}()

#if !SWIFT_PACKAGE
/// The bundle carrying Lemonade's resources in a framework build: a nested `Lemonade.bundle` when
/// one was produced, and the framework bundle itself otherwise.
private func frameworkResourceBundle() -> Bundle {
    let bundle = Bundle(for: BundleFinder.self)
    guard let resourceBundleURL = bundle.url(forResource: "Lemonade", withExtension: "bundle"),
          let resourceBundle = Bundle(url: resourceBundleURL) else {
        return bundle
    }
    return resourceBundle
}
#endif

/// Bundle accessor that works for both SPM and XcodeGen builds.
/// SPM generates a `Bundle.module` accessor, while framework builds need to find the bundle differently.
public extension Bundle {
    /// The Lemonade framework bundle
    static var lemonade: Bundle { lemonadeBundle }
}

private class BundleFinder {}
