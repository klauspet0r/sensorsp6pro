# Sensor Explorer – Pixel 6 Pro

Eine deutsche Lern- und Experimentier-App für die Sensoren des Pixel 6 Pro.
Die App verwendet Kotlin, Jetpack Compose, Clean Architecture und ausschließlich
flüchtige Live-Messungen. Sie speichert oder überträgt keine Sensordaten.

## Entwicklung

- Zielgerät: Pixel 6 Pro, Android 12 oder neuer
- IDE: Android Studio
- Branches: `main`, `develop`, `feature/*`
- CI: GitHub Actions
- Build-Verteilung: Firebase App Distribution für `develop` und `main`

## Verifizieren

- Lokal: `./gradlew.bat :core:domain:test testDebugUnitTest lintDebug assembleDebug`
- CI: `./gradlew :core:domain:test testDebugUnitTest lintDebug connectedDebugAndroidTest`
- WLAN-Setup: `docs/device-setup.md`
- Firebase: `docs/firebase-setup.md`
- Foundation-Nachweis: `docs/foundation-verification.md`

Die verbindlichen Entscheidungen stehen in
`docs/superpowers/specs/2026-11-16-pixel-6-pro-sensor-explorer-design.md`.
