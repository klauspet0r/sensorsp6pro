# Pixel-6-Pro-WLAN-Debugging

1. Pixel und Laptop befinden sich im selben WLAN.
2. Wireless Debugging ist in den Entwickleroptionen aktiviert.
3. `adb devices -l` muss das Gerät als `device` anzeigen.
4. Bei getrennter Verbindung `adb connect IP:CONNECT_PORT` verwenden.
5. Der Pairing-Port dient ausschließlich einer neuen Kopplung.
6. Run-Ziel in Android Studio: physisches Pixel 6 Pro, Konfiguration `app`.
7. Logcat nach der jeweiligen Build-Application-ID filtern.

## Abnahme Beschleunigung

- Dashboard und Detailroute öffnen
- X/Y/Z und Einheit m/s² prüfen
- Kippversuch durchführen
- Pause stoppt Werteänderungen
- Hintergrund stoppt Listener; Rückkehr startet erneut
- Keine FATAL-EXCEPTION-Meldung im Logcat
