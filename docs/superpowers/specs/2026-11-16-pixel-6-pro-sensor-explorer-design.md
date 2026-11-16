# Sensor Explorer für Pixel 6 Pro – Designspezifikation

## 1. Kontext und Ziel

Sensor Explorer ist eine native Android-Lern- und Experimentier-App für genau ein Pixel 6 Pro. Sie soll zeigen, welche physischen und virtuellen Sensoren sowie weiteren Hardware-Datenquellen das Gerät besitzt, deren Werte verständlich erklären und durch passende Live-Visualisierungen erfahrbar machen.

Die erste Version dient ausschließlich dem Entdecken und Lernen. Eine spätere praktische Anwendung auf Basis der gewonnenen Erkenntnisse ist ein separates Projekt und nicht Teil dieses Designs.

### Festgelegte Produktentscheidungen

- Kotlin und Jetpack Compose
- Material-You-Optik mit sichtbaren technischen Live-Werten
- Eigene Detailansicht für jeden unterstützten Sensor beziehungsweise jede Datenquelle
- Nur Live-Messungen; keine Speicherung, Verlaufsansicht oder Exportfunktion
- Entwicklung und Hardwaretests ausschließlich auf dem Pixel 6 Pro
- Entwicklung in Android Studio und Debugging über WLAN
- Mehrere Gradle-Module mit Clean Architecture
- Öffentliches GitHub-Repository `sensorsp6pro` unter MIT-Lizenz
- Vereinfachter Git Flow mit `main`, `develop` und `feature/*`
- GitHub Issues und ein einfaches GitHub-Projects-Board
- GitHub Actions für Tests und Builds; Firebase App Distribution für `develop` und `main`

## 2. Umfang der Sensor- und Datenquellen

### 2.1 Klassische Android-Sensoren

| Sensor | Messgröße | Visualisierung und Experiment |
| --- | --- | --- |
| Beschleunigung | X/Y/Z in m/s² einschließlich Schwerkraft | Drei Live-Diagramme, Achsenwerte, 3D-Modell und Kippversuch |
| Gyroskop | Drehgeschwindigkeit um X/Y/Z in rad/s | Rotierendes 3D-Modell, Achsendiagramme und Drehversuch |
| Magnetometer | Magnetfeld in µT | Feldstärke, Achsenwerte, Kompass und Störungstest |
| Barometer | Luftdruck in hPa | Live-Wert, kurzer sichtbarer Trend und relative Höhenänderung |
| Umgebungslicht | Beleuchtungsstärke in Lux | Lux-Anzeige, Live-Diagramm sowie Licht- und Abdeckversuch |
| Näherung | Abstand beziehungsweise Nah/Fern-Zustand | Zustandsanzeige, verfügbarer Abstandswert und Abdeckversuch |

Der kurze Drucktrend besteht nur aus flüchtigen Werten der aktuellen Ansicht. Er ist keine gespeicherte Messung. Eine Höhenanzeige wird als abgeleitete relative Schätzung gekennzeichnet, nicht als exakte absolute Höhe.

### 2.2 Virtuelle und zusammengesetzte Sensoren

Die App ermittelt über `SensorManager.getSensorList(Sensor.TYPE_ALL)` die tatsächlich vorhandenen Einträge. Dazu können Schwerkraft, lineare Beschleunigung, Rotationsvektoren, unkalibrierte Sensorvarianten, Schrittzähler, Schritterkennung, Significant Motion und Tilt Detection gehören.

Die Liste wird nicht als feste Pixel-6-Pro-Liste angenommen. Jeder verfügbare und öffentlich nutzbare Eintrag erhält eine eigene Ansicht mit Typ, Einheiten, Genauigkeit, Aktualisierungszeitpunkt und technischen Metadaten. Bekannte Sensoren erhalten ihre kuratierte Visualisierung; bislang unbekannte, aber messende Sensoren erhalten eine generische Rohwert- und Metadatenansicht. Reine Meta- oder Zusatzinformationseinträge werden als solche gekennzeichnet und nicht als Messwertquelle ausgegeben.

Bekannte, auf dem Gerät fehlende Sensoren werden als nicht verfügbar erklärt. Es erscheinen keine Beispieldaten als Ersatz. Alle Diagramme verwenden ausschließlich ein flüchtiges, begrenztes Zeitfenster im Arbeitsspeicher und keine gespeicherte Messhistorie.

### 2.3 Erweiterte Hardware-Datenquellen

- **GNSS:** Position, Genauigkeit, Satelliten und Signalstärke, soweit Android diese Informationen bereitstellt
- **Kameras:** verfügbare Objektive, Vorschau und zugängliche Camera2-Metadaten
- **LDAF sowie Spektral-/Flicker-Sensor:** eigene Informations- und Kameraansicht nur mit tatsächlich freigegebenen Informationen; keine Behauptung direkter Rohdaten
- **Mikrofone:** verfügbare Audioquellen, Pegel, Wellenform und Frequenzspektrum; unabhängige Rohdaten aller drei Mikrofone werden nicht vorausgesetzt
- **UWB:** Verfügbarkeit und Fähigkeiten; Entfernungsmessung nur mit kompatiblem zweitem Gerät oder Tag
- **NFC:** Status und technische Informationen zu erkannten Tags
- **Akku und System:** Akkutemperatur, Spannung, Ladestatus und Geräteinformationen; Akkutemperatur wird nicht als Raumtemperatur bezeichnet

Das Pixel 6 Pro besitzt keine frei nutzbaren Umgebungstemperatur-, Luftfeuchtigkeits- oder Herzfrequenzsensoren. Diese werden nicht als verfügbare Messquellen dargestellt.

### 2.4 Fingerabdruck

Die App erhält keine Fingerabdruck-Sensoransicht. Der defekte beziehungsweise nicht erkannte Sensor wird in einem separaten GitHub-Research-Issue behandelt. Darin werden Hardwareerkennung und der offizielle Google-Reparatur-/Kalibrierungsweg untersucht. Die App selbst verspricht weder Reparatur noch Kalibrierung und erhält keinen Zugriff auf biometrische Rohdaten.

## 3. Informationsarchitektur und visuelles Design

### 3.1 Navigation

Die Startansicht gruppiert Sensorkarten nach Bewegung, Umgebung, Standort, Kamera, Audio, Funk und System. Jede Karte öffnet über Compose Navigation eine eigene Detailroute. Die Karten entstehen aus den tatsächlich erkannten Datenquellen.

Die Übersicht zeigt Live-Werte nur für sichtbare, unbedenkliche Sensorkarten. Kamera, Mikrofon und GNSS starten erst in ihrer jeweiligen Detailansicht nach der erforderlichen Berechtigung.

### 3.2 Gestaltung

Die freigegebene Gestaltung kombiniert Variante B „Material You Explorer“ mit der Informationsdichte von Variante A „Dark Sensor Lab“:

- helle Material-You-Oberfläche mit großen, farblich differenzierten Karten
- freundliche, verständliche deutsche Texte
- konkrete Messwerte und Einheiten direkt auf den Sensorkarten
- farbcodierte X/Y/Z-Werte und Live-Diagramme in passenden Detailansichten
- Abtastrate, Genauigkeit und Aktualisierungsstatus als kompakte Statusangaben
- Experiment- und Erklärungskarte auf jeder geeigneten Sensoransicht
- aufklappbare technische Metadaten, damit die Standardansicht übersichtlich bleibt
- Schaltfläche zum Pausieren und Fortsetzen der aktiven Messung

Beispielzahlen aus den Design-Mockups sind keine Produktivdaten und dürfen nicht als Fallback erscheinen.

## 4. Technische Architektur

### 4.1 Module

| Modul | Verantwortung |
| --- | --- |
| `app` | MainActivity, Compose Navigation, Hilt-Komposition und Theme |
| `feature:dashboard` | Sensorübersicht und verfügbare Datenquellen |
| `feature:motion` | Physische und virtuelle Bewegungs-/Positionssensoren |
| `feature:environment` | Licht, Luftdruck und Näherung |
| `feature:location` | GNSS |
| `feature:camera` | Kameras und zugängliche Kamera-Hilfssensorinformationen |
| `feature:audio` | Mikrofon-Live-Analyse |
| `feature:connectivity` | UWB und NFC |
| `feature:system` | Akku-Telemetrie und Geräteinformationen |
| `core:domain` | Reine Kotlin-Modelle, Repository-Schnittstellen und Use Cases |
| `core:ui` | Material-You-Komponenten, Diagramme, Werte-, Experiment- und Zustandskarten |
| `data:sensor` | SensorManager und Laufzeit-Sensorerkennung |
| `data:gnss` | Standort- und Satelliten-APIs |
| `data:camera` | Camera2 |
| `data:audio` | AudioRecord und flüchtige Live-Analyse |
| `data:connectivity` | UWB- und NFC-Systemdienste |
| `data:system` | BatteryManager und Geräteeigenschaften |

Ein Sensor erhält kein eigenes Gradle-Modul; die Module folgen fachlichen Gruppen. Innerhalb dieser Gruppen bleibt jede Sensoransicht eigenständig.

### 4.2 Abhängigkeiten und Datenfluss

- Feature-Module hängen von `core:domain` und `core:ui` ab.
- Datenmodule implementieren Schnittstellen aus `core:domain` und kapseln Android-APIs.
- `core:domain` enthält keine Android-Imports.
- `app` verbindet Domänen-Schnittstellen über Hilt mit ihren Datenimplementierungen.
- Live-Messungen verwenden Kotlin Coroutines und `Flow<Measurement>`.
- ViewModels wandeln Messströme in unveränderliche UI-Zustände mit `StateFlow` um.
- Compose rendert ausschließlich diese UI-Zustände.

Datenfluss:

`Android-API → Repository → Flow<Measurement> → Use Case → ViewModel/StateFlow → Compose`

Das minimale Android-SDK ist API 31 (Android 12), die ursprüngliche Android-Version des Pixel 6 Pro. Compile- und Target-SDK verwenden die beim Projektsetup in Android Studio verfügbare stabile Android-Version. Es besteht kein Anspruch auf Tests oder gleichwertige Funktion auf anderen Geräten.

## 5. Laufzeitverhalten, Berechtigungen und Datenschutz

- Die Übersicht aktualisiert sichtbare Werte reduziert; Detailansichten können höhere unterstützte Raten verwenden.
- Angezeigte Abtastraten werden aus tatsächlichen Ereignis-Zeitstempeln berechnet. Eine angeforderte Rate wird nicht als garantiert dargestellt.
- Messungen starten beim Öffnen der jeweiligen Ansicht und enden beim Verlassen, Pausieren oder Wechsel der App in den Hintergrund.
- Es gibt keinen Hintergrunddienst und keine Hintergrundstandortberechtigung.
- Kamera-, Mikrofon- und genaue Standortberechtigungen werden erst nach einer kurzen fachlichen Erklärung in der jeweiligen Ansicht angefragt.
- Für Schrittsensoren wird die erforderliche Aktivitätserkennungsberechtigung nur bei Bedarf angefragt.
- Verweigerte Berechtigungen blockieren nicht die übrigen Sensoransichten. Die betroffene Ansicht erklärt den Zustand und bietet bei dauerhaft verweigerter Berechtigung den Weg zu den Systemeinstellungen.
- Kamera-, Audio- und Standortdaten bleiben flüchtig im Arbeitsspeicher und werden nicht gespeichert oder übertragen.
- Firebase dient nur der APK-Verteilung. Die App erhält keine Firebase-Analytics- oder Telemetriefunktion.

## 6. Zustände und Fehlerbehandlung

Jede Messansicht unterscheidet mindestens:

- wird gestartet
- live
- pausiert
- Berechtigung erforderlich oder verweigert
- Sensor beziehungsweise Funktion nicht verfügbar
- Datenquelle vorübergehend belegt
- technischer Fehler

Die UI zeigt den letzten Aktualisierungszeitpunkt und kennzeichnet ausbleibende Werte. Fehler führen nicht zu erfundenen Messdaten oder zum Absturz anderer Ansichten.

UWB ohne kompatibles Gegenüber zeigt nur Verfügbarkeit und Voraussetzungen. LDAF und Spektral-/Flicker-Sensor zeigen nur nachweisbar zugängliche Kamera-Informationen. Belegte Kamera- oder Mikrofonquellen erhalten eine verständliche Meldung und eine Wiederholungsmöglichkeit.

## 7. WLAN-Debugging

Die vorhandene Wireless-Debugging-Kopplung wird zuerst mit `adb devices` geprüft. Falls keine Verbindung besteht, wird das Pixel über seine aktuelle WLAN-IP und den auf dem Gerät angezeigten Connect-Port verbunden. Der Pairing-Port wird nur für eine neue Kopplung verwendet und nicht mit dem Connect-Port verwechselt.

Android Studio erhält das Pixel als Run-/Debug-Ziel. Das Setup umfasst Logcat, Paketfilterung und eine wiederverwendbare Run-Konfiguration für das `app`-Modul. Beim initialen Gerätecheck werden Android-Version, SDK-Level und tatsächlich gemeldete Sensoren erfasst.

ADB-Schlüssel, persönliche Zugangsdaten und lokale Konfigurationsdateien werden nicht versioniert.

## 8. GitHub- und Issue-Workflow

Das neue öffentliche Repository heißt `sensorsp6pro` und erhält die MIT-Lizenz. Der vereinfachte Git Flow verwendet:

- `main` für signierte Release-Kandidaten
- `develop` für integrierte und auf dem Pixel testbare Entwicklungsstände
- `feature/*` für einzelne Issues

Release- und Hotfix-Branches sind nicht Bestandteil des initialen Workflows.

Es gibt ein Issue pro Sensoransicht sowie separate Issues für Projektsetup, Repository, WLAN-Debugging, CI/CD und Fingerabdruck-Recherche. Die Issue-Vorlage enthält Datenquelle, Visualisierung, Berechtigungen, Akzeptanzkriterien und Testcheckliste. Labels unterscheiden mindestens Funktion, Infrastruktur, Dokumentation, Fehler und Recherche.

Branches folgen dem Muster `feature/<issue-nummer>-<kurzname>`. Pull Requests verknüpfen ihr Issue mit `Closes #<nummer>`. Das Projects-Board enthält Backlog, In Arbeit, Review und Fertig.

## 9. Build-, Test- und Deployment-Pipeline

| Auslöser | Prüfungen | Ergebnis |
| --- | --- | --- |
| Push auf `feature/*` | Unit-Tests, Lint, Debug-Build | APK als GitHub-Actions-Artefakt |
| Pull Request nach `develop` | Unit-Tests, Compose-UI-Tests auf Emulator, Lint, Debug-Build | APK-Artefakt und PR-Status |
| Push oder Merge auf `develop` | Alle Unit- und UI-Tests, Lint, Debug-Build | Debug-APK über Firebase App Distribution |
| Push oder Merge auf `main` | Alle Unit- und UI-Tests, Lint, Release-Build | Signierte Release-APK über Firebase App Distribution |

Der Debug-Build erhält ein Application-ID-Suffix `.debug` und den sichtbaren Namen „Sensor Explorer Dev“. Dadurch können Entwicklungs- und Release-Build nebeneinander installiert werden. Firebase enthält zwei Android-App-Registrierungen im selben Projekt, eine für jede Application ID.

Jeder Firebase-Build enthält Branch, Commit und Workflow-Link in den Release Notes. Release-Keystore, zugehörige Passwörter und Firebase-Zugangsdaten werden als GitHub Secrets gespeichert. Lokale Keystore- und Secret-Dateien sind von der Versionskontrolle ausgeschlossen. Der Release-Keystore wird zusätzlich sicher außerhalb des Repositorys gesichert.

## 10. Teststrategie

- **Domänen-Unit-Tests:** Umrechnungen, Modelle, Use Cases, Verfügbarkeit und Fehlerzustände
- **ViewModel-Tests:** Flow-Verarbeitung, Pausieren, Lebenszyklus und UI-Zustände mit nachgebildeten Repositories
- **Compose-UI-Tests:** Navigation, Karten, Diagrammzustände, Berechtigungs- und Fehlerdarstellung mit nachgebildeten Datenquellen
- **Datenmodultests:** Android-APIs hinter schmalen Adaptern kapseln und Adaptergrenzen gezielt prüfen
- **Manuelle Pixel-Tests:** jedes Issue enthält eine sensorspezifische Checkliste für reale Hardware, Berechtigungen, Pausieren, Hintergrundwechsel und Rückkehr
- **Zusatzfälle:** Kamera/Mikrofon belegt, Standort deaktiviert, dauerhaft verweigerte Berechtigung, NFC-Tag vorhanden/fehlend und UWB-Gegenüber vorhanden/fehlend

Emulator-UI-Tests verwenden nachgebildete Datenquellen und gelten nicht als Nachweis realer Sensorwerte. Reale Hardwarefunktionen werden ausschließlich auf dem Pixel 6 Pro abgenommen.

## 11. Umsetzungsreihenfolge

1. Öffentliches Repository, MIT-Lizenz, Git-Flow-Grundlage und Issue-Vorlagen einrichten.
2. Android-Projekt, Modulgrenzen, Theme, Navigation und Hilt-Grundgerüst erstellen.
3. WLAN-Verbindung und Geräteeigenschaften prüfen.
4. CI für Feature-Branches und Pull Requests einrichten.
5. Firebase-Projekt sowie Debug- und Release-Verteilung für `develop` und `main` konfigurieren.
6. Einen vertikalen Durchstich mit dem Beschleunigungssensor umsetzen: Datenquelle, Use Case, ViewModel, Karte, Detailansicht und Tests.
7. Die übrigen SensorManager-Einträge und erweiterten Datenquellen issue-weise ergänzen.
8. Jede Ansicht auf dem Pixel anhand ihrer Checkliste abnehmen.

## 12. Abnahmekriterien für Version 1

- Die App wird über Android Studio und WLAN-Debugging auf dem Pixel installiert und gestartet.
- Die Übersicht basiert auf tatsächlich erkannten Datenquellen und enthält keine vorgetäuschten Live-Werte.
- Jede unterstützte Datenquelle besitzt eine eigene freigegebene Material-You-Ansicht mit verständlichen Erklärungen und technischen Werten.
- Berechtigungsablehnungen, fehlende Hardware und belegte Datenquellen werden ohne Absturz behandelt.
- Beim Pausieren oder Verlassen der App enden alle aktiven Hardwarezugriffe.
- Es werden keine Messdaten dauerhaft gespeichert oder an Firebase übertragen.
- Feature- und PR-Builds erzeugen prüfbare APK-Artefakte; `develop` und `main` verteilen die festgelegten Builds über Firebase.
- Alle automatisierten Tests und die manuelle Pixel-Testcheckliste für die enthaltenen Ansichten bestanden.

## 13. Ausdrücklich ausgenommen

- Messungen speichern oder exportieren
- Mehrere Sensoren als dauerhafte gemeinsame Messreihe aufzeichnen
- Hintergrundmessungen
- Google-Play-Veröffentlichung
- Kompatibilitätszusage für andere Geräte
- Fingerabdruck-Reparatur oder -Kalibrierung innerhalb der App
- Direkter Zugriff auf biometrische, LDAF- oder Spektral-/Flicker-Rohdaten
- Firebase Analytics oder sonstige Übertragung von Sensordaten
- Die spätere praktische Folgeanwendung
