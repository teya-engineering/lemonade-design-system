import SwiftUI
import CoreText

/// Handles font registration for the Lemonade Design System.
/// Call `LemonadeFonts.registerFonts()` at app startup to ensure fonts are available.
public enum LemonadeFonts {
    private static let _registerFontsOnce: Void = {
        let fontNames = [
            "Figtree-Regular",
            "Figtree-Medium",
            "Figtree-SemiBold"
        ]

        for fontName in fontNames {
            registerFont(named: fontName)
        }
    }()

    /// Registers all Lemonade custom fonts from the bundle.
    /// This should be called once at app startup, typically in the App's init.
    ///
    /// ## Usage
    /// ```swift
    /// @main
    /// struct MyApp: App {
    ///     init() {
    ///         LemonadeFonts.registerFonts()
    ///     }
    ///     // ...
    /// }
    /// ```
    public static func registerFonts() {
        _ = _registerFontsOnce
    }

    /// The font file's URL: under `Fonts/` in an SPM build, at the bundle root in an Xcode
    /// project build.
    private static func fontURL(named fontName: String) -> URL? {
        Bundle.lemonade.url(forResource: fontName, withExtension: "ttf", subdirectory: "Fonts")
            ?? Bundle.lemonade.url(forResource: fontName, withExtension: "ttf")
    }

    private static func registerFont(named fontName: String) {
        guard let url = fontURL(named: fontName) else {
            #if DEBUG
            print("Lemonade: Could not find font file: \(fontName).ttf")
            #endif
            return
        }

        guard let fontDataProvider = CGDataProvider(url: url as CFURL) else {
            #if DEBUG
            print("Lemonade: Could not create data provider for font: \(fontName)")
            #endif
            return
        }

        guard let font = CGFont(fontDataProvider) else {
            #if DEBUG
            print("Lemonade: Could not create CGFont for: \(fontName)")
            #endif
            return
        }

        var error: Unmanaged<CFError>?
        if !CTFontManagerRegisterGraphicsFont(font, &error) {
            if let error = error?.takeRetainedValue() {
                let errorDescription = CFErrorCopyDescription(error)
                if let desc = errorDescription as String?, !desc.contains("already registered") {
                    #if DEBUG
                    print("Lemonade: Error registering font \(fontName): \(desc)")
                    #endif
                }
            }
        }
    }
}
