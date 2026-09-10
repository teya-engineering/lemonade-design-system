// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "Lemonade",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(
            name: "Lemonade",
            targets: ["Lemonade"]
        ),
    ],
    targets: [
        .target(
            name: "Lemonade",
            path: "Sources/Lemonade",
            exclude: ["Info.plist"],
            resources: [
                .process("Resources")
            ]
        ),
        .testTarget(
            name: "LemonadeTests",
            dependencies: ["Lemonade"]
        ),
    ]
)
