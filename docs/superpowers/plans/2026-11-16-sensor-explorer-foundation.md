# Sensor Explorer Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ein öffentliches, automatisiert getestetes Android-Projekt mit Clean Architecture, WLAN-Debugging, Firebase-Verteilung und einem vollständigen Beschleunigungssensor-Durchstich bereitstellen.

**Architecture:** Das App-Modul komponiert `core:domain`, `core:ui`, `data:sensor`, `feature:dashboard` und `feature:motion` über Hilt. Android-Sensorereignisse laufen als `Flow<AccelerationMeasurement>` durch Repository und Use Case in ein ViewModel; Compose rendert unveränderliche UI-Zustände. Weitere Sensorgruppen erhalten eigene Folgepläne und verändern diese Datenflussrichtung nicht.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Coroutines/Flow, Hilt, JUnit 4, kotlinx-coroutines-test, Compose UI Test, Gradle Version Catalog, Android Studio, ADB over Wi-Fi, GitHub CLI/Actions, Firebase App Distribution.

## Global Constraints

- Entwicklung und Hardwaretests ausschließlich auf dem Pixel 6 Pro; minimales SDK ist API 31.
- Anzeigesprache ist Deutsch; Produktname ist „Sensor Explorer“, Debug-Name ist „Sensor Explorer Dev“.
- Application ID ist `dev.sensorsp6pro.explorer`; Debug erhält zusätzlich `.debug`.
- Nur Live-Werte; keine Datenbank, Exportfunktion, Hintergrundmessung oder Firebase Analytics.
- Keine Beispielwerte als Fallback und keine Android-Imports in `core:domain`.
- Diagramme verwenden höchstens 100 flüchtige Messpunkte im Arbeitsspeicher.
- Beim Pausieren, Verlassen der Ansicht oder Wechsel in den Hintergrund werden Sensor-Listener abgemeldet.
- Vereinfachter Git Flow: `main`, `develop`, `feature/*`; jeder Sensor erhält später ein eigenes Issue.
- Feature-Branches und PRs erzeugen APK-Artefakte; nur `develop` und `main` verwenden Firebase App Distribution.
- Keystore-, Firebase- und lokale Secret-Dateien werden niemals committed.

## Plan Boundaries

Dieser Plan endet mit einer installierbaren App, deren Beschleunigungsübersicht und Detailansicht real auf dem Pixel funktionieren. Die folgenden eigenständigen Folgepläne setzen darauf auf und liefern jeweils erneut eine testbare App:

1. Übrige Bewegungssensoren und generische Ansicht für neu erkannte SensorManager-Einträge
2. Umgebungssensoren Licht, Luftdruck und Näherung
3. GNSS sowie Berechtigungs- und Fehlerzustände für Standort
4. Kamera und zugängliche Kamera-Hilfssensorinformationen
5. Audio, UWB, NFC und Akku/System
6. Fingerabdruck-Recherche als getrennter, nicht zur App gehörender Reparatur- und Kalibrierungsweg

---

### Task 1: Unabhängiges GitHub-Repository und Git-Flow-Grundlage

**Files:**
- Create: `.gitignore`
- Create: `LICENSE`
- Create: `README.md`
- Existing: `docs/superpowers/specs/2026-11-16-pixel-6-pro-sensor-explorer-design.md`
- Existing: `docs/superpowers/plans/2026-11-16-sensor-explorer-foundation.md`

**Interfaces:**
- Consumes: bestehende leere Arbeitsmappe `C:/dev/git/sensorsp6pro`
- Produces: öffentliches Repository `sensorsp6pro` mit `main`, `develop` und Remote `origin`

- [ ] **Step 1: Projektgrenze und GitHub-Anmeldung prüfen**

Run:

```bash
cd /c/dev/git/sensorsp6pro
pwd
git rev-parse --show-toplevel
gh auth status
```

Expected: `pwd` endet mit `/c/dev/git/sensorsp6pro`. Vor `git init` zeigt Git auf den übergeordneten Ordner `C:/dev/git`; dadurch wird bestätigt, dass ein eigenes Repository nötig ist. Falls `gh auth status` fehlschlägt, `gh auth login` mit GitHub.com, HTTPS und Browser-Anmeldung ausführen.

- [ ] **Step 2: Ignore-Regeln anlegen**

Create `.gitignore`:

```gitignore
# Android and Gradle
.gradle/
build/
**/build/
local.properties
.cxx/
.externalNativeBuild/

# Android Studio and OS
.idea/
*.iml
.DS_Store
Thumbs.db

# Signing and local secrets
*.keystore
*.jks
keystore.properties
google-services.json

# Planning companion
.superpowers/

# Captured device data
artifacts/device/
```

- [ ] **Step 3: MIT-Lizenz und Projektbeschreibung anlegen**

Create `LICENSE`:

```text
MIT License

Copyright (c) 2026 sensorsp6pro contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

Create `README.md`:

```markdown
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

Die verbindlichen Entscheidungen stehen in
`docs/superpowers/specs/2026-11-16-pixel-6-pro-sensor-explorer-design.md`.
```

- [ ] **Step 4: Eigenes Repository initialisieren und ersten Stand prüfen**

Run:

```bash
git init -b main
git add .gitignore LICENSE README.md docs
git status --short
git diff --cached --check
```

Expected: Nur beabsichtigte Dateien sind staged; `.superpowers/` erscheint nicht. `git diff --cached --check` endet ohne Ausgabe und mit Exitcode 0.

- [ ] **Step 5: Initialen Stand committen und Repository veröffentlichen**

Run:

```bash
git commit -m "docs: define sensor explorer design"
gh repo create sensorsp6pro --public --source=. --remote=origin --description "Interaktiver Sensor-Explorer für das Pixel 6 Pro"
git push -u origin main
git checkout -b develop
git push -u origin develop
```

Expected: `main` und `develop` zeigen auf denselben initialen Commit. `gh repo view --json name,visibility,defaultBranchRef` zeigt `sensorsp6pro`, `PUBLIC` und `main`.

---

### Task 2: Issue-Vorlagen, Labels und Projects-Board

**Files:**
- Create: `.github/ISSUE_TEMPLATE/sensor-view.yml`
- Create: `.github/ISSUE_TEMPLATE/task.yml`
- Create: `.github/pull_request_template.md`

**Interfaces:**
- Consumes: Remote `origin` und Branch `develop` aus Task 1
- Produces: Issue-Workflow und Branch-Namensregel `feature/<issue-nummer>-<kurzname>`

- [ ] **Step 1: Issue für den Workflow anlegen und Feature-Branch starten**

Run:

```bash
ISSUE_URL=$(gh issue create --title "GitHub-Issue- und PR-Workflow einrichten" --body "Akzeptanzkriterien: Sensor- und Aufgabenvorlagen, PR-Vorlage, Labels und Projects-Board sind vorhanden.")
ISSUE_NUMBER=${ISSUE_URL##*/}
git checkout develop
git checkout -b "feature/${ISSUE_NUMBER}-github-workflow"
```

Expected: Der Branchname enthält die von GitHub vergebene Nummer, zum Beispiel `feature/1-github-workflow`.

- [ ] **Step 2: Sensor-Issue-Vorlage schreiben**

Create `.github/ISSUE_TEMPLATE/sensor-view.yml`:

```yaml
name: Sensoransicht
description: Eine neue Sensor- oder Datenquellenansicht
title: "[Sensor] "
labels: [feature]
body:
  - type: input
    id: source
    attributes:
      label: Datenquelle und Android-API
    validations:
      required: true
  - type: textarea
    id: visualization
    attributes:
      label: Visualisierung und Experiment
      value: |
        - Live-Werte und Einheiten:
        - Diagramm oder sonstige Visualisierung:
        - Verständliche Erklärung:
        - Experiment:
    validations:
      required: true
  - type: textarea
    id: permissions
    attributes:
      label: Berechtigungen und Hardwaregrenzen
    validations:
      required: true
  - type: textarea
    id: acceptance
    attributes:
      label: Akzeptanzkriterien
      value: |
        - [ ] Eigene Route und Material-You-Ansicht
        - [ ] Reale Werte ohne Beispiel-Fallback
        - [ ] Lade-, Pausen-, Fehler- und Verfügbarkeitszustände
        - [ ] Listener enden bei Pause und Hintergrund
        - [ ] Unit- und Compose-UI-Tests bestanden
        - [ ] Manuelle Pixel-Testcheckliste bestanden
    validations:
      required: true
```

- [ ] **Step 3: Aufgaben- und PR-Vorlagen schreiben**

Create `.github/ISSUE_TEMPLATE/task.yml`:

```yaml
name: Technische Aufgabe oder Recherche
description: Setup, Infrastruktur, Dokumentation, Fehler oder Recherche
title: "[Task] "
body:
  - type: textarea
    id: goal
    attributes:
      label: Ziel und fachlicher Nutzen
    validations:
      required: true
  - type: textarea
    id: acceptance
    attributes:
      label: Akzeptanzkriterien und Nachweis
    validations:
      required: true
```

Create `.github/pull_request_template.md`:

```markdown
## Ziel


## Nachweise

- [ ] Unit-Tests bestanden
- [ ] Compose-UI-Tests bestanden
- [ ] Lint bestanden
- [ ] Manuelle Pixel-Checkliste aus dem Issue bestanden oder nicht betroffen

## Issue

Closes #
```

Die leeren Abschnitte werden beim Erstellen jedes PR mit konkretem Ziel, ausgeführten Kommandos und Issue-Nummer ausgefüllt.

- [ ] **Step 4: Templates verifizieren und committen**

Run:

```bash
git add .github
git diff --cached --check
git commit -m "chore: add issue and pull request templates"
git push -u origin HEAD
```

Expected: Push und Commit sind erfolgreich. Anschließend einen PR nach `develop` öffnen, mit `Closes #${ISSUE_NUMBER}` verknüpfen und nach erfolgreichem Review mergen.

- [ ] **Step 5: Labels und Board anlegen und prüfen**

Run:

```bash
gh label create feature --color 0E8A16 --description "Neue Sensoransicht oder Funktion" --force
gh label create infrastructure --color 1D76DB --description "Build, CI, Setup und Tooling" --force
gh label create documentation --color 0075CA --description "Dokumentation" --force
gh label create bug --color D73A4A --description "Fehler" --force
gh label create research --color D4C5F9 --description "Recherche ohne Implementierungszusage" --force
gh project create --owner @me --title "Sensor Explorer"
```

Expected: `gh label list` enthält alle fünf Labels und `gh project list` das Projekt. Im GitHub-Projects-Webinterface genau vier Spalten `Backlog`, `In Arbeit`, `Review`, `Fertig` anlegen und den Workflow „Item closed“ auf `Fertig` aktivieren.

---

### Task 3: Android Studio, SDK und Modulgerüst

**Files:**
- Create: Android-Studio-Template-Dateien im Projektroot
- Modify: `settings.gradle.kts`
- Create: `core/domain/build.gradle.kts`
- Create: `core/ui/build.gradle.kts`
- Create: `data/sensor/build.gradle.kts`
- Create: `feature/dashboard/build.gradle.kts`
- Create: `feature/motion/build.gradle.kts`

**Interfaces:**
- Consumes: gemergter `develop`
- Produces: sechs baubare Module und Application ID `dev.sensorsp6pro.explorer`

- [ ] **Step 1: Fehlende Entwicklungsumgebung installieren**

Installiere die stabile Android-Studio-Version über den offiziellen Android-Developers-Download. Installiere darin Android SDK, aktuelle stabile SDK-Plattform, Android SDK Build-Tools, Platform-Tools und Emulator. Öffne **More Actions > SDK Manager** und bestätige jede Komponente.

Run nach Installation mit den tatsächlich von Android Studio angezeigten Pfaden:

```bash
"/c/Program Files/Android/Android Studio/jbr/bin/java.exe" -version
"/c/Users/klaus/AppData/Local/Android/Sdk/platform-tools/adb.exe" --version
```

Expected: Beide Kommandos geben Versionsinformationen aus. Falls Android Studio andere Installationspfade meldet, werden ausschließlich diese tatsächlichen Pfade in den übrigen Kommandos verwendet.

- [ ] **Step 2: Foundation-Issue und Branch erstellen**

Run:

```bash
git checkout develop
git pull origin develop
ISSUE_URL=$(gh issue create --title "Android-Grundgerüst und Beschleunigungssensor" --label feature --body "Vertikaler Durchstich: Module, SensorManager, Flow, ViewModel, Dashboard, Detailansicht, Tests und Pixel-Nachweis.")
ISSUE_NUMBER=${ISSUE_URL##*/}
git checkout -b "feature/${ISSUE_NUMBER}-android-foundation"
```

Expected: `git branch --show-current` endet mit `-android-foundation`.

- [ ] **Step 3: Compose-Projekt erzeugen**

In Android Studio **New Project > Empty Activity** wählen und exakt eintragen:

```text
Name: Sensor Explorer
Package name: dev.sensorsp6pro.explorer
Save location: C:\dev\git\sensorsp6pro
Minimum SDK: API 31 (Android 12)
Language: Kotlin
Build configuration language: Kotlin DSL
```

Nach dem Generieren `app/build.gradle.kts` prüfen: `applicationId` ist `dev.sensorsp6pro.explorer`, `versionName` ist `0.1.0`. Den CI-VersionCode direkt setzen:

```kotlin
defaultConfig {
    applicationId = "dev.sensorsp6pro.explorer"
    minSdk = 31
    versionCode = providers.environmentVariable("GITHUB_RUN_NUMBER")
        .orElse("1").map(String::toInt).get()
    versionName = "0.1.0"
}
```

- [ ] **Step 4: Module mit klaren Namensräumen erzeugen**

In Android Studio für `core:ui`, `data:sensor`, `feature:dashboard` und `feature:motion` jeweils **New > New Module > Android Library** verwenden. Für `core:domain` **Java or Kotlin Library** verwenden. Namespaces beziehungsweise Pakete:

```text
dev.sensorsp6pro.explorer.core.ui
dev.sensorsp6pro.explorer.data.sensor
dev.sensorsp6pro.explorer.feature.dashboard
dev.sensorsp6pro.explorer.feature.motion
dev.sensorsp6pro.explorer.core.domain
```

`settings.gradle.kts` muss exakt enthalten:

```kotlin
include(":app")
include(":core:domain")
include(":core:ui")
include(":data:sensor")
include(":feature:dashboard")
include(":feature:motion")
```

- [ ] **Step 5: Modulgrenzen und Hilt verdrahten**

Den im Template erzeugten Version-Catalog um diese Einträge ergänzen:

```toml
[libraries]
hilt-android = { module = "com.google.dagger:hilt-android", version = "2.57.2" }
hilt-compiler = { module = "com.google.dagger:hilt-android-compiler", version = "2.57.2" }
androidx-hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version = "1.3.0" }
androidx-lifecycle-runtime-compose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version = "2.10.0" }
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version = "2.9.6" }
javax-inject = { module = "javax.inject:javax.inject", version = "1" }
coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version = "1.10.2" }
turbine = { module = "app.cash.turbine:turbine", version = "1.2.1" }
junit = { module = "junit:junit", version = "4.13.2" }

[plugins]
hilt = { id = "com.google.dagger.hilt.android", version = "2.57.2" }
```

Im Root-`build.gradle.kts` `alias(libs.plugins.hilt) apply false` ergänzen. In `app`, `data:sensor`, `feature:dashboard` und `feature:motion` Hilt und `kapt` aktivieren:

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
```

Für `app` statt `android.library` das Template-Plugin `android.application` behalten. `core:domain` erhält `implementation(libs.javax.inject)`, `testImplementation(libs.coroutines.test)`, `testImplementation(libs.turbine)` und `testImplementation(libs.junit)`. Android-Feature- und Datenmodule erhalten `implementation(project(":core:domain"))`; `core:ui` und Feature-Module erhalten Compose über dieselben BOM- und Material-3-Katalogeinträge wie das erzeugte `app`-Modul. Feature-Module erhalten zusätzlich diese Projekt- und AndroidX-Abhängigkeiten:

```kotlin
dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

`app` erhält `implementation(libs.androidx.navigation.compose)` sowie die Projektabhängigkeiten auf `core:ui`, `feature:dashboard` und `feature:motion`; `data:sensor` wird ebenfalls in `app` eingebunden, damit Hilt dessen Modul findet.

- [ ] **Step 6: Build ausführen und Gerüst committen**

Run:

```bash
./gradlew.bat projects
./gradlew.bat :core:domain:test :data:sensor:testDebugUnitTest :feature:motion:testDebugUnitTest :app:assembleDebug
```

Expected: Alle sechs Module erscheinen bei `projects`; `BUILD SUCCESSFUL` und `app-debug.apk` wird erzeugt. Danach:

```bash
git add .
git diff --cached --check
git commit -m "build: scaffold clean architecture modules"
```

---

### Task 4: Domänenmodell und Use Case testgetrieben

**Files:**
- Create: `core/domain/src/main/kotlin/dev/sensorsp6pro/explorer/core/domain/AccelerationMeasurement.kt`
- Create: `core/domain/src/main/kotlin/dev/sensorsp6pro/explorer/core/domain/AccelerationRepository.kt`
- Create: `core/domain/src/main/kotlin/dev/sensorsp6pro/explorer/core/domain/ObserveAccelerationUseCase.kt`
- Test: `core/domain/src/test/kotlin/dev/sensorsp6pro/explorer/core/domain/ObserveAccelerationUseCaseTest.kt`

**Interfaces:**
- Produces: `ObserveAccelerationUseCase(rate: SamplingRate): Flow<AccelerationMeasurement>`
- Produces: `AccelerationMeasurement(vector: Vector3, timestampNanos: Long, accuracy: SensorAccuracy)`

- [ ] **Step 1: Fehlschlagenden Use-Case-Test schreiben**

Create `ObserveAccelerationUseCaseTest.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.domain

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveAccelerationUseCaseTest {
    @Test
    fun `forwards requested rate and measurements`() = runTest {
        val sample = AccelerationMeasurement(Vector3(0.1f, 0.2f, 9.81f), 42L, SensorAccuracy.HIGH)
        val repository = FakeAccelerationRepository(flowOf(sample))

        ObserveAccelerationUseCase(repository)(SamplingRate.GAME).test {
            assertEquals(sample, awaitItem())
            awaitComplete()
        }
        assertEquals(SamplingRate.GAME, repository.requestedRate)
    }
}
```

Die Test-Fake wird im selben Testpaket als `FakeAccelerationRepository.kt` angelegt:

```kotlin
package dev.sensorsp6pro.explorer.core.domain

import kotlinx.coroutines.flow.Flow

class FakeAccelerationRepository(
    private val measurements: Flow<AccelerationMeasurement>,
) : AccelerationRepository {
    var requestedRate: SamplingRate? = null

    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> {
        requestedRate = rate
        return measurements
    }
}
```

`core:domain` benötigt `testImplementation(libs.turbine)` und `testImplementation("junit:junit:4.13.2")`.

- [ ] **Step 2: Test ausführen und erwarteten Fehler sehen**

Run: `./gradlew.bat :core:domain:test --tests "*ObserveAccelerationUseCaseTest"`

Expected: Kompilierungsfehler, weil die Domänentypen noch nicht existieren.

- [ ] **Step 3: Minimale Domänentypen implementieren**

Create `AccelerationMeasurement.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.domain

data class Vector3(val x: Float, val y: Float, val z: Float)

enum class SensorAccuracy { UNRELIABLE, LOW, MEDIUM, HIGH, UNKNOWN }

data class AccelerationMeasurement(
    val vector: Vector3,
    val timestampNanos: Long,
    val accuracy: SensorAccuracy,
)

enum class SamplingRate { UI, GAME, FASTEST }

class SensorUnavailableException : IllegalStateException("Beschleunigungssensor nicht verfügbar")
class SensorStartException : IllegalStateException("Beschleunigungssensor konnte nicht gestartet werden")
```

Create `AccelerationRepository.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.domain

import kotlinx.coroutines.flow.Flow

interface AccelerationRepository {
    fun observe(rate: SamplingRate): Flow<AccelerationMeasurement>
}
```

Create `ObserveAccelerationUseCase.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAccelerationUseCase @Inject constructor(
    private val repository: AccelerationRepository,
) {
    operator fun invoke(rate: SamplingRate): Flow<AccelerationMeasurement> = repository.observe(rate)
}
```

- [ ] **Step 4: Test und Android-Freiheit prüfen**

Run:

```bash
./gradlew.bat :core:domain:test
! grep -R "^import android" core/domain/src/main
```

Expected: `BUILD SUCCESSFUL`; das negierte `grep` bestätigt, dass keine Android-Imports vorhanden sind.

- [ ] **Step 5: Domäne committen**

Run: `git add core/domain && git commit -m "feat: add acceleration domain flow"`

---

### Task 5: SensorManager-Adapter und Genauigkeitsmapping

**Files:**
- Create: `data/sensor/src/main/kotlin/dev/sensorsp6pro/explorer/data/sensor/SensorAccuracyMapper.kt`
- Create: `data/sensor/src/main/kotlin/dev/sensorsp6pro/explorer/data/sensor/SensorManagerAccelerationRepository.kt`
- Create: `data/sensor/src/main/kotlin/dev/sensorsp6pro/explorer/data/sensor/SensorDataModule.kt`
- Test: `data/sensor/src/test/kotlin/dev/sensorsp6pro/explorer/data/sensor/SensorAccuracyMapperTest.kt`

**Interfaces:**
- Consumes: `AccelerationRepository`, `SamplingRate`, `SensorAccuracy`
- Produces: Hilt-Binding von `AccelerationRepository` auf `SensorManagerAccelerationRepository`

- [ ] **Step 1: Mapping-Test schreiben und scheitern sehen**

Create `SensorAccuracyMapperTest.kt`:

```kotlin
package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import org.junit.Assert.assertEquals
import org.junit.Test

class SensorAccuracyMapperTest {
    @Test
    fun `maps every Android accuracy`() {
        assertEquals(SensorAccuracy.UNRELIABLE, SensorManager.SENSOR_STATUS_UNRELIABLE.toSensorAccuracy())
        assertEquals(SensorAccuracy.LOW, SensorManager.SENSOR_STATUS_ACCURACY_LOW.toSensorAccuracy())
        assertEquals(SensorAccuracy.MEDIUM, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM.toSensorAccuracy())
        assertEquals(SensorAccuracy.HIGH, SensorManager.SENSOR_STATUS_ACCURACY_HIGH.toSensorAccuracy())
        assertEquals(SensorAccuracy.UNKNOWN, 999.toSensorAccuracy())
    }
}
```

Run: `./gradlew.bat :data:sensor:testDebugUnitTest`

Expected: Kompilierungsfehler wegen fehlendem `toSensorAccuracy`.

- [ ] **Step 2: Mapping implementieren und Test bestehen lassen**

Create `SensorAccuracyMapper.kt`:

```kotlin
package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy

fun Int.toSensorAccuracy(): SensorAccuracy = when (this) {
    SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
    else -> SensorAccuracy.UNKNOWN
}
```

Run: `./gradlew.bat :data:sensor:testDebugUnitTest`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: SensorManager als abschaltbaren Flow kapseln**

Create `SensorManagerAccelerationRepository.kt`:

```kotlin
package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorStartException
import dev.sensorsp6pro.explorer.core.domain.SensorUnavailableException
import dev.sensorsp6pro.explorer.core.domain.Vector3
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorManagerAccelerationRepository @Inject constructor(
    private val sensorManager: SensorManager,
) : AccelerationRepository {
    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor == null) {
            close(SensorUnavailableException())
            return@callbackFlow
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(
                    AccelerationMeasurement(
                        vector = Vector3(event.values[0], event.values[1], event.values[2]),
                        timestampNanos = event.timestamp,
                        accuracy = event.accuracy.toSensorAccuracy(),
                    ),
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        val delay = when (rate) {
            SamplingRate.UI -> SensorManager.SENSOR_DELAY_UI
            SamplingRate.GAME -> SensorManager.SENSOR_DELAY_GAME
            SamplingRate.FASTEST -> SensorManager.SENSOR_DELAY_FASTEST
        }
        if (!sensorManager.registerListener(listener, sensor, delay)) {
            sensorManager.unregisterListener(listener)
            close(SensorStartException())
            return@callbackFlow
        }
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
```

Create `SensorDataModule.kt`:

```kotlin
package dev.sensorsp6pro.explorer.data.sensor

import android.content.Context
import android.hardware.SensorManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorDataModule {
    @Binds
    @Singleton
    abstract fun bindAccelerationRepository(
        implementation: SensorManagerAccelerationRepository,
    ): AccelerationRepository

    companion object {
        @Provides
        @Singleton
        fun provideSensorManager(@ApplicationContext context: Context): SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
```

- [ ] **Step 4: Tests und Build ausführen**

Run: `./gradlew.bat :core:domain:test :data:sensor:testDebugUnitTest :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`; der echte Listener wird erst in Task 9 auf dem Pixel geprüft.

- [ ] **Step 5: Adapter committen**

Run: `git add data/sensor && git commit -m "feat: stream acceleration from sensor manager"`

---

### Task 6: ViewModel-Zustände und Listener-Lebenszyklus

**Files:**
- Create: `feature/motion/src/main/kotlin/dev/sensorsp6pro/explorer/feature/motion/AccelerationUiState.kt`
- Create: `feature/motion/src/main/kotlin/dev/sensorsp6pro/explorer/feature/motion/AccelerationViewModel.kt`
- Test: `feature/motion/src/test/kotlin/dev/sensorsp6pro/explorer/feature/motion/AccelerationViewModelTest.kt`

**Interfaces:**
- Consumes: `ObserveAccelerationUseCase(rate)` und eine im Test enthaltene `FakeAccelerationRepository`
- Produces: `AccelerationViewModel.setActive(Boolean)` und `uiState: StateFlow<AccelerationUiState>`

- [ ] **Step 1: Fehlschlagende Zustandstests schreiben**

Create `AccelerationViewModelTest.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.motion

import app.cash.turbine.test
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import dev.sensorsp6pro.explorer.core.domain.ObserveAccelerationUseCase
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import dev.sensorsp6pro.explorer.core.domain.Vector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccelerationViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `active state keeps only latest one hundred samples and pause clears subscription`() = runTest {
        val source = MutableSharedFlow<AccelerationMeasurement>()
        val viewModel = AccelerationViewModel(
            ObserveAccelerationUseCase(FakeAccelerationRepository(source)),
        )
        viewModel.uiState.test {
            assertEquals(MeasurementStatus.PAUSED, awaitItem().status)
            viewModel.setActive(true)
            assertEquals(MeasurementStatus.STARTING, awaitItem().status)
            repeat(101) { index ->
                source.emit(AccelerationMeasurement(Vector3(index.toFloat(), 0f, 9.81f), index.toLong(), SensorAccuracy.HIGH))
            }
            val live = expectMostRecentItem()
            assertEquals(MeasurementStatus.LIVE, live.status)
            assertEquals(100, live.history.size)
            assertEquals(100f, live.history.last().vector.x)
            viewModel.setActive(false)
            assertEquals(MeasurementStatus.PAUSED, awaitItem().status)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeAccelerationRepository(
    private val measurements: Flow<AccelerationMeasurement>,
) : AccelerationRepository {
    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> = measurements
}
```

- [ ] **Step 2: Test ausführen und erwarteten Kompilierungsfehler prüfen**

Run: `./gradlew.bat :feature:motion:testDebugUnitTest`
Expected: Fehler wegen fehlendem `AccelerationViewModel` und `MeasurementStatus`.

- [ ] **Step 3: UI-Zustand und ViewModel implementieren**

Create `AccelerationUiState.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.motion

import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement

enum class MeasurementStatus { STARTING, LIVE, PAUSED, UNAVAILABLE, ERROR }

data class AccelerationUiState(
    val status: MeasurementStatus = MeasurementStatus.PAUSED,
    val history: List<AccelerationMeasurement> = emptyList(),
    val message: String? = null,
)
```

Create `AccelerationViewModel.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.motion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sensorsp6pro.explorer.core.domain.ObserveAccelerationUseCase
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorUnavailableException
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccelerationViewModel @Inject constructor(
    private val observeAcceleration: ObserveAccelerationUseCase,
) : ViewModel() {
    private val active = MutableStateFlow(false)

    val uiState = active.flatMapLatest { isActive ->
        if (!isActive) {
            flowOf(AccelerationUiState(status = MeasurementStatus.PAUSED))
        } else {
            observeAcceleration(SamplingRate.GAME)
                .runningFold(AccelerationUiState(status = MeasurementStatus.STARTING)) { state, sample ->
                    state.copy(
                        status = MeasurementStatus.LIVE,
                        history = (state.history + sample).takeLast(100),
                    )
                }
                .catch { error ->
                    emit(
                        AccelerationUiState(
                            status = if (error is SensorUnavailableException) MeasurementStatus.UNAVAILABLE else MeasurementStatus.ERROR,
                            message = error.message ?: "Unbekannter Sensorfehler",
                        ),
                    )
                }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AccelerationUiState())

    fun setActive(value: Boolean) {
        active.value = value
    }
}
```

- [ ] **Step 4: Zustandstest bestehen lassen**

Run: `./gradlew.bat :feature:motion:testDebugUnitTest`
Expected: `BUILD SUCCESSFUL`; der Test bestätigt 100 Messpunkte und den Wechsel von LIVE zu PAUSED.

- [ ] **Step 5: ViewModel committen**

Run: `git add feature/motion && git commit -m "feat: manage acceleration measurement state"`

---

### Task 7: Material-You-Diagramme und Beschleunigungsansicht

**Files:**
- Create: `core/ui/src/main/kotlin/dev/sensorsp6pro/explorer/core/ui/LiveLineChart.kt`
- Create: `core/ui/src/main/kotlin/dev/sensorsp6pro/explorer/core/ui/ExperimentCard.kt`
- Create: `feature/motion/src/main/kotlin/dev/sensorsp6pro/explorer/feature/motion/AccelerationScreen.kt`
- Test: `feature/motion/src/androidTest/kotlin/dev/sensorsp6pro/explorer/feature/motion/AccelerationScreenTest.kt`

**Interfaces:**
- Consumes: `AccelerationUiState`, `AccelerationViewModel.setActive(Boolean)`
- Produces: wiederverwendbares `LiveLineChart(history: List<Vector3>)` und `AccelerationScreen`

- [ ] **Step 1: UI-Test für Werte und Pause schreiben**

Create `AccelerationScreenTest.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.motion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import dev.sensorsp6pro.explorer.core.domain.Vector3
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AccelerationScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun `shows values explanation and pause action`() {
        var active = true
        val sample = AccelerationMeasurement(Vector3(0.12f, 0.08f, 9.81f), 1L, SensorAccuracy.HIGH)
        compose.setContent {
            AccelerationContent(
                state = AccelerationUiState(MeasurementStatus.LIVE, listOf(sample)),
                onActiveChange = { active = it },
            )
        }
        compose.onNodeWithText("9,81 m/s²", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Probier es aus").assertIsDisplayed()
        compose.onNodeWithText("Messung pausieren").performClick()
        assertFalse(active)
    }
}
```

Run: `./gradlew.bat :feature:motion:connectedDebugAndroidTest`
Expected: Test kann noch nicht kompiliert werden, weil `AccelerationContent` fehlt. Emulator wird in Task 10 automatisiert.

- [ ] **Step 2: Flüchtiges Drei-Achsen-Diagramm implementieren**

Create `LiveLineChart.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.sensorsp6pro.explorer.core.domain.Vector3

@Composable
fun LiveLineChart(history: List<Vector3>, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(160.dp)) {
        if (history.isEmpty()) return@Canvas
        val allValues = history.flatMap { listOf(it.x, it.y, it.z) }
        val minimum = allValues.minOrNull() ?: 0f
        val maximum = allValues.maxOrNull() ?: 1f
        val range = (maximum - minimum).takeIf { it > 0.001f } ?: 1f
        val colors = listOf(Color(0xFFB24D5A), Color(0xFF347CA5), Color(0xFF438365))
        val selectors = listOf(Vector3::x, Vector3::y, Vector3::z)
        selectors.forEachIndexed { index, selector ->
            val path = Path()
            history.forEachIndexed { pointIndex, point ->
                val x = if (history.size == 1) 0f else pointIndex.toFloat() / (history.lastIndex) * size.width
                val y = size.height - ((selector(point) - minimum) / range) * size.height
                if (pointIndex == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, colors[index], style = Stroke(width = 3f))
        }
    }
}
```

Create `ExperimentCard.kt`:

```kotlin
package dev.sensorsp6pro.explorer.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExperimentCard(text: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text("Probier es aus", style = MaterialTheme.typography.titleMedium)
            Text(text, Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

`core:ui` erhält `implementation(project(":core:domain"))`, weil das Diagramm `Vector3` konsumiert.

- [ ] **Step 3: Bildschirm mit Lifecycle-Aktivierung implementieren**

Create `AccelerationScreen.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sensorsp6pro.explorer.core.ui.ExperimentCard
import dev.sensorsp6pro.explorer.core.ui.LiveLineChart

@Composable
fun AccelerationScreen(viewModel: AccelerationViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.setActive(true)
        onPauseOrDispose { viewModel.setActive(false) }
    }
    AccelerationContent(state, viewModel::setActive)
}

@Composable
fun AccelerationContent(
    state: AccelerationUiState,
    onActiveChange: (Boolean) -> Unit,
) {
    val sample = state.history.lastOrNull()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Beschleunigung", style = MaterialTheme.typography.headlineMedium)
        Text("Status: ${state.status.name.lowercase()}")
        if (sample != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("X\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.x))
                Text("Y\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.y))
                Text("Z\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.z))
            }
            LiveLineChart(state.history.map { it.vector })
            Text("Genauigkeit: ${sample.accuracy.name.lowercase()}")
        }
        Card(Modifier.fillMaxWidth()) {
            Text(
                "Der Sensor misst Beschleunigung einschließlich der Erdanziehung. " +
                    "Deshalb zeigt er auch bei ruhendem Handy ungefähr 9,81 m/s².",
                Modifier.padding(16.dp),
            )
        }
        ExperimentCard("Lege dein Handy flach auf den Tisch. Kippe es danach langsam und beobachte die drei Achsen.")
        state.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { onActiveChange(state.status == MeasurementStatus.PAUSED) }) {
            Text(if (state.status == MeasurementStatus.PAUSED) "Messung fortsetzen" else "Messung pausieren")
        }
    }
}
```

Für Lifecycle und Hilt-ViewModel werden die Compose-BOM-kompatiblen Bibliotheken `androidx.lifecycle:lifecycle-runtime-compose` und `androidx.hilt:hilt-navigation-compose` im Version-Catalog ergänzt. Der UI-Test findet den deutsch formatierten Z-Wert innerhalb des Achsen-Textes.

- [ ] **Step 4: UI-Test bestehen lassen**

Run: `./gradlew.bat :feature:motion:connectedDebugAndroidTest`
Expected: `shows values explanation and pause action` besteht; Werte und deutscher Erklärungstext sind sichtbar.

- [ ] **Step 5: UI committen**

Run: `git add core/ui feature/motion && git commit -m "feat: visualize live acceleration"`

---

### Task 8: Dashboard, Navigation und App-Komposition

**Files:**
- Create: `feature/dashboard/src/main/kotlin/dev/sensorsp6pro/explorer/feature/dashboard/DashboardScreen.kt`
- Create: `app/src/main/kotlin/dev/sensorsp6pro/explorer/SensorExplorerApplication.kt`
- Create: `app/src/main/kotlin/dev/sensorsp6pro/explorer/SensorExplorerApp.kt`
- Modify: `app/src/main/kotlin/dev/sensorsp6pro/explorer/MainActivity.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- Consumes: `AccelerationScreen`, `AccelerationViewModel`, Hilt-Module
- Produces: Routen `dashboard` und `sensor/acceleration`

- [ ] **Step 1: Dashboard mit derselben Live-Datenquelle schreiben**

Create `DashboardScreen.kt`:

```kotlin
package dev.sensorsp6pro.explorer.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sensorsp6pro.explorer.feature.motion.AccelerationViewModel

@Composable
fun DashboardScreen(
    onOpenAcceleration: () -> Unit,
    viewModel: AccelerationViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.setActive(true)
        onPauseOrDispose { viewModel.setActive(false) }
    }
    Column(Modifier.padding(16.dp)) {
        Text("Sensor Explorer", style = MaterialTheme.typography.headlineMedium)
        Card(onClick = onOpenAcceleration, modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Beschleunigung", style = MaterialTheme.typography.titleMedium)
                val sample = state.history.lastOrNull()
                Text(sample?.let { "Z: %.2f m/s²".format(it.vector.z) } ?: "Sensor wird gestartet")
            }
        }
    }
}
```

`feature:dashboard` erhält `implementation(project(":feature:motion"))`. Diese Kopplung wird im Folgeplan für übrige Bewegungssensoren durch einen gemeinsamen Dashboard-Vertrag ersetzt, bevor weitere Karten hinzukommen.

- [ ] **Step 2: Hilt-Anwendung und Navigation schreiben**

Create `SensorExplorerApplication.kt`:

```kotlin
package dev.sensorsp6pro.explorer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SensorExplorerApplication : Application()
```

Create `SensorExplorerApp.kt`:

```kotlin
package dev.sensorsp6pro.explorer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sensorsp6pro.explorer.feature.dashboard.DashboardScreen
import dev.sensorsp6pro.explorer.feature.motion.AccelerationScreen

@Composable
fun SensorExplorerApp() {
    val controller = rememberNavController()
    NavHost(controller, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(onOpenAcceleration = { controller.navigate("sensor/acceleration") })
        }
        composable("sensor/acceleration") {
            AccelerationScreen()
        }
    }
}
```

`MainActivity` erhält `@AndroidEntryPoint` und `setContent { SensorExplorerTheme { SensorExplorerApp() } }`. Das Manifest registriert `android:name=".SensorExplorerApplication"` und deklariert:

```xml
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="false" />
```

- [ ] **Step 3: Alle lokalen Prüfungen ausführen**

Run:

```bash
./gradlew.bat :core:domain:test testDebugUnitTest lintDebug assembleDebug
```

Expected: `BUILD SUCCESSFUL`, keine Lint-Fehler und eine APK unter `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 4: Vertikalen Durchstich committen und PR vorbereiten**

Run:

```bash
git add app core data feature
git commit -m "feat: connect dashboard to acceleration details"
git push -u origin HEAD
gh pr create --base develop --title "Android-Grundgerüst und Beschleunigungssensor" --body "Closes #${ISSUE_NUMBER}"$'\n\n'"Nachweise: Unit-Tests, Lint, Debug-Build und Compose-UI-Test."
```

Expected: Der Foundation-PR verknüpft automatisch das Issue aus Task 3 und enthält die bisherigen Testergebnisse.

---

### Task 9: ADB-WLAN und reale Pixel-Abnahme

**Files:**
- Create: `docs/device-setup.md`

**Interfaces:**
- Consumes: `app-debug.apk`, Android Studio Platform-Tools
- Produces: nachvollziehbare WLAN-Verbindung und dokumentierte reale Sensorabnahme

- [ ] **Step 1: Vorhandene Kopplung prüfen**

Run mit dem tatsächlichen Platform-Tools-Pfad:

```bash
ADB="/c/Users/klaus/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" start-server
"$ADB" devices -l
```

Expected: Das Pixel erscheint als `device`, nicht `offline` oder `unauthorized`. Ohne Eintrag am Pixel **Einstellungen > Entwickleroptionen > Kabelloses Debugging** öffnen und die aktuelle IP sowie den Connect-Port interaktiv einlesen:

```bash
read -p "Pixel WLAN-IP: " PHONE_IP
read -p "Connect-Port: " CONNECT_PORT
"$ADB" connect "$PHONE_IP:$CONNECT_PORT"
"$ADB" devices -l
```

Nur bei fehlender bestehender Kopplung **Mit Kopplungscode koppeln** öffnen, Pairing-IP und separaten Pairing-Port interaktiv einlesen und anschließend `"$ADB" pair "$PAIR_IP:$PAIR_PORT"` ausführen.

- [ ] **Step 2: Android- und Sensorinformationen ohne Geheimnisse erfassen**

Run:

```bash
"$ADB" shell getprop ro.build.version.release
"$ADB" shell getprop ro.build.version.sdk
"$ADB" shell dumpsys sensorservice
```

Expected: Pixel 6 Pro und SDK 31 oder höher; `dumpsys` zeigt den Beschleunigungssensor. Persönliche Standort-, Konto- oder Netzwerkdaten werden nicht in die Dokumentation kopiert.

- [ ] **Step 3: App installieren und reale Messung ausführen**

Run:

```bash
./gradlew.bat :app:installDebug
"$ADB" shell monkey -p dev.sensorsp6pro.explorer.debug -c android.intent.category.LAUNCHER 1
"$ADB" logcat -s AndroidRuntime:E
```

Expected: App öffnet sich ohne `FATAL EXCEPTION`. Dashboard und Detailansicht zeigen veränderliche reale Werte. Flach aufgelegt ist der Betrag der Z-Achse ungefähr 9,81 m/s²; beim Kippen ändern sich die Achsen. Nach Hometaste, Bildschirmsperre und Rückkehr erscheinen keine Abstürze oder dauerhaft veralteten LIVE-Zustände.

- [ ] **Step 4: Setup und Checkliste dokumentieren**

Create `docs/device-setup.md`:

```markdown
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
```

Run: `git add docs/device-setup.md && git commit -m "docs: document wireless pixel verification"`

- [ ] **Step 5: Dokumentation pushen und Foundation mergen**

Run:

```bash
git push origin HEAD
gh pr view --web
```

Expected: Der PR enthält zusätzlich die WLAN-Dokumentation und den bestandenen manuellen Pixel-Test. Nach Review `gh pr merge --squash --delete-branch` ausführen. `git checkout develop && git pull origin develop` zeigt anschließend den vollständigen Foundation-Stand.

---

### Task 10: GitHub Actions für Feature-Branches und Pull Requests

**Files:**
- Create: `.github/workflows/android-ci.yml`
- Modify: `feature/motion/build.gradle.kts` und übrige Testmodul-Builddateien für konsistente UI-Testabhängigkeiten

**Interfaces:**
- Consumes: Gradle Wrapper und Tests aus Tasks 4–8
- Produces: PR-Status und Artefakt `sensor-explorer-feature-debug`

- [ ] **Step 1: CI-Issue und Branch erstellen**

Run:

```bash
git checkout develop
git pull origin develop
ISSUE_URL=$(gh issue create --title "Android-CI für Features und Pull Requests" --label infrastructure --body "Unit-Tests, Emulator-UI-Tests, Lint und Debug-APK-Artefakte ohne Firebase-Verteilung.")
ISSUE_NUMBER=${ISSUE_URL##*/}
git checkout -b "feature/${ISSUE_NUMBER}-android-ci"
```

Expected: Branch endet mit `-android-ci`.

- [ ] **Step 2: Workflow schreiben**

Create `.github/workflows/android-ci.yml`:

```yaml
name: Android CI

on:
  push:
    branches: ["feature/**"]
  pull_request:
    branches: [develop]

permissions:
  contents: read

concurrency:
  group: android-ci-${{ github.ref }}
  cancel-in-progress: true

jobs:
  verify:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
      - uses: gradle/actions/setup-gradle@v4
      - name: Unit tests and lint
        run: ./gradlew :core:domain:test testDebugUnitTest lintDebug
      - name: Build debug APK
        run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: sensor-explorer-feature-debug
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 14

  ui-test:
    if: github.event_name == 'pull_request'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
      - uses: gradle/actions/setup-gradle@v4
      - uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 35
          target: google_apis
          arch: x86_64
          script: ./gradlew connectedDebugAndroidTest
```

- [ ] **Step 3: Workflow lokal und remote verifizieren**

Run:

```bash
git add .github/workflows/android-ci.yml feature
git diff --cached --check
git commit -m "ci: test and build android feature branches"
git push -u origin HEAD
gh run list --branch "$(git branch --show-current)" --limit 1
```

Expected: Der Push löst `Android CI` aus; `verify` ist grün und lädt eine APK hoch. Der PR nach `develop` startet zusätzlich `ui-test` auf dem Emulator. Erst nach zwei grünen Jobs mergen.

---

### Task 11: Signierte Firebase-Builds für develop und main

**Files:**
- Modify: `app/build.gradle.kts`
- Create: `.github/workflows/firebase-distribution.yml`
- Create: `docs/firebase-setup.md`

**Interfaces:**
- Consumes: grüne CI, Application IDs `dev.sensorsp6pro.explorer[.debug]`
- Produces: Firebase-Verteilung an Testergruppe `pixel-testers`

- [ ] **Step 1: Firebase-Issue, Branch und Projekt einrichten**

Run:

```bash
git checkout develop
git pull origin develop
ISSUE_URL=$(gh issue create --title "Firebase-Verteilung und Release-Signing" --label infrastructure --body "Debug- und Release-APKs nach Tests an pixel-testers verteilen; Debug und Release nebeneinander installieren.")
ISSUE_NUMBER=${ISSUE_URL##*/}
git checkout -b "feature/${ISSUE_NUMBER}-firebase-distribution"
```

In Firebase Console ein Projekt ohne Google Analytics erstellen. Zwei Android-Apps registrieren:

```text
dev.sensorsp6pro.explorer.debug
dev.sensorsp6pro.explorer
```

Beide Firebase-App-IDs notieren und als GitHub Actions Secrets `FIREBASE_APP_ID_DEBUG` und `FIREBASE_APP_ID_RELEASE` hinterlegen. Testergruppe `pixel-testers` mit der eigenen E-Mail-Adresse erstellen. Ein dediziertes Google-Cloud-Service-Konto mit Rolle **Firebase App Distribution Admin** erzeugen und dessen JSON-Schlüssel als Secret `FIREBASE_SERVICE_ACCOUNT_JSON` speichern; die lokale JSON-Datei danach sicher außerhalb des Repositorys aufbewahren.

- [ ] **Step 2: Release-Signing erzeugen und sichern**

Mit dem Android-Studio-JBR einen Upload-Keystore erzeugen:

```bash
KEYTOOL="/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe"
"$KEYTOOL" -genkeypair -v -keystore release-upload.keystore -alias sensor-explorer -keyalg RSA -keysize 2048 -validity 10000
base64 -w 0 release-upload.keystore > release-upload.keystore.base64
```

Den Keystore und die Base64-Datei offline sichern und niemals committen. Den Inhalt der Base64-Datei sowie Keystore-Passwort, Alias und Key-Passwort als `SIGNING_KEYSTORE_BASE64`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD` in GitHub Secrets hinterlegen. Beide lokalen Dateien nach der erfolgreichen Secrets-Einrichtung in den gesicherten Offline-Speicher verschieben; sie bleiben durch `*.keystore` beziehungsweise die neu zu ergänzende Regel `*.keystore.base64` von Git ausgeschlossen.

`app/build.gradle.kts` erhält:

```kotlin
signingConfigs {
    create("release") {
        storeFile = providers.gradleProperty("RELEASE_STORE_FILE").orNull?.let(::file)
        storePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD").orNull
        keyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS").orNull
        keyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD").orNull
    }
}

buildTypes {
    debug {
        applicationIdSuffix = ".debug"
        resValue("string", "app_name", "Sensor Explorer Dev")
    }
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        signingConfig = signingConfigs.getByName("release")
    }
}
```

- [ ] **Step 3: Deployment-Workflow schreiben**

Create `.github/workflows/firebase-distribution.yml`:

```yaml
name: Firebase Distribution

on:
  push:
    branches: [develop, main]

permissions:
  contents: read

jobs:
  distribute:
    runs-on: ubuntu-latest
    environment: firebase-app-distribution
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
      - uses: gradle/actions/setup-gradle@v4
      - name: Unit tests and lint
        run: ./gradlew :core:domain:test testDebugUnitTest lintDebug
      - uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 35
          target: google_apis
          arch: x86_64
          script: ./gradlew connectedDebugAndroidTest
      - id: auth
        uses: google-github-actions/auth@v2
        with:
          credentials_json: ${{ secrets.FIREBASE_SERVICE_ACCOUNT_JSON }}
      - uses: actions/setup-node@v4
        with:
          node-version: "22"
      - run: npm install --global firebase-tools
      - name: Build and distribute develop debug APK
        if: github.ref_name == 'develop'
        env:
          FIREBASE_APP: ${{ secrets.FIREBASE_APP_ID_DEBUG }}
        run: |
          ./gradlew assembleDebug
          printf 'Branch: %s\nCommit: %s\nWorkflow: %s\n' "$GITHUB_REF_NAME" "$GITHUB_SHA" "$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID" > release-notes.txt
          firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk --app "$FIREBASE_APP" --groups pixel-testers --release-notes-file release-notes.txt
      - name: Decode release keystore
        if: github.ref_name == 'main'
        run: printf '%s' '${{ secrets.SIGNING_KEYSTORE_BASE64 }}' | base64 --decode > "$RUNNER_TEMP/release-upload.keystore"
      - name: Build and distribute main release APK
        if: github.ref_name == 'main'
        env:
          FIREBASE_APP: ${{ secrets.FIREBASE_APP_ID_RELEASE }}
        run: |
          ./gradlew assembleRelease \
            -PRELEASE_STORE_FILE="$RUNNER_TEMP/release-upload.keystore" \
            -PRELEASE_STORE_PASSWORD="${{ secrets.SIGNING_STORE_PASSWORD }}" \
            -PRELEASE_KEY_ALIAS="${{ secrets.SIGNING_KEY_ALIAS }}" \
            -PRELEASE_KEY_PASSWORD="${{ secrets.SIGNING_KEY_PASSWORD }}"
          printf 'Branch: %s\nCommit: %s\nWorkflow: %s\n' "$GITHUB_REF_NAME" "$GITHUB_SHA" "$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID" > release-notes.txt
          firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk --app "$FIREBASE_APP" --groups pixel-testers --release-notes-file release-notes.txt
```

- [ ] **Step 4: Setup dokumentieren und Workflow prüfen**

Create `docs/firebase-setup.md` mit den Application IDs als Namen, aber ohne numerische Firebase-IDs oder Zugangsdaten:

```markdown
# Firebase App Distribution

- Projekt ohne Analytics; Testergruppe `pixel-testers`
- Debug-Application-ID: `dev.sensorsp6pro.explorer.debug`
- Release-Application-ID: `dev.sensorsp6pro.explorer`
- `develop`: Debug-APK, `main`: signierte Release-APK
- Secrets: Firebase-App-IDs, Service-Account-JSON und Release-Signing
- Keystore und Service-Account-Datei niemals committen
- Installationseinladung an die eigene Tester-E-Mail-Adresse
```

Run:

```bash
git add app/build.gradle.kts .github/workflows/firebase-distribution.yml docs/firebase-setup.md
git diff --cached --check
git commit -m "ci: distribute develop and main builds with firebase"
git push -u origin HEAD
```

Expected: Feature-Push erzeugt keinen Firebase-Lauf. Nach PR-Merge nach `develop` erscheint genau ein grüner Firebase-Lauf und eine E-Mail beziehungsweise App-Distribution-Einladung für die Debug-APK.

- [ ] **Step 5: Debug- und Release-Installation auf dem Pixel abnehmen**

Debug über Firebase installieren und Start/Kippversuch wiederholen. Anschließend den geprüften `develop` über PR nach `main` mergen. Die Release-Einladung installieren und bestätigen:

```bash
"$ADB" shell pm list packages | grep dev.sensorsp6pro.explorer
```

Expected: Beide Pakete `dev.sensorsp6pro.explorer` und `dev.sensorsp6pro.explorer.debug` sind gleichzeitig installiert. Beide Apps starten und zeigen reale Werte; die Release-App heißt „Sensor Explorer“, die Debug-App „Sensor Explorer Dev“.

---

### Task 12: Gesamtnachweis und Foundation-Abschluss

**Files:**
- Modify: `README.md`
- Create: `docs/foundation-verification.md`

**Interfaces:**
- Consumes: alle vorherigen Tasks
- Produces: dokumentierter, reproduzierbarer Ausgangspunkt für die sechs Folgepläne

- [ ] **Step 1: Abschluss-Issue, Branch und lokale Qualitätsprüfung**

Run:

```bash
git checkout develop
git pull origin develop
ISSUE_URL=$(gh issue create --title "Foundation-Gesamtnachweis dokumentieren" --label documentation --body "Lokale, CI-, Firebase- und Pixel-Nachweise bündeln und reproduzierbare Prüfkommandos dokumentieren.")
ISSUE_NUMBER=${ISSUE_URL##*/}
git checkout -b "feature/${ISSUE_NUMBER}-foundation-verification"
./gradlew.bat clean :core:domain:test testDebugUnitTest lintDebug assembleDebug
```

Expected: `BUILD SUCCESSFUL` ohne Lint- oder Testfehler. Anschließend `git status --short` prüfen; Buildausgaben dürfen nicht als neue Dateien erscheinen.

- [ ] **Step 2: Remote-Nachweise sammeln**

Run:

```bash
gh run list --limit 10
gh issue list
gh project list
```

Expected: Feature-CI, PR-UI-Tests sowie Firebase-Läufe für `develop` und `main` sind grün. Alle bisherigen Foundation-Issues sind geschlossen und im Board unter `Fertig`; nur das in Task 12 neu angelegte Abschluss-Issue ist noch offen.

- [ ] **Step 3: Nachweisdokument und README ergänzen**

Create `docs/foundation-verification.md`:

```markdown
# Foundation-Abnahme

- Repository: öffentlich, MIT, `main` und `develop`
- Module: app, core:domain, core:ui, data:sensor, feature:dashboard, feature:motion
- Unit-Tests: Domäne, Genauigkeitsmapping und ViewModel
- UI-Test: Werte, Erklärung und Pause auf Emulator
- Pixel: Dashboard, Detailansicht, Kippversuch, Pause, Hintergrund und Rückkehr
- WLAN: ADB-Verbindung und Android-Studio-Run-Ziel
- Feature: APK-Artefakt; PR: APK plus Emulator-UI-Test
- develop: Firebase-Debug; main: Firebase-Release
- Debug und Release gleichzeitig installiert und gestartet
- Keine Speicherung oder Übertragung von Messdaten
```

`README.md` erhält unter „Entwicklung“:

```markdown

## Verifizieren

- Lokal: `./gradlew.bat :core:domain:test testDebugUnitTest lintDebug assembleDebug`
- CI: `./gradlew :core:domain:test testDebugUnitTest lintDebug connectedDebugAndroidTest`
- WLAN-Setup: `docs/device-setup.md`
- Firebase: `docs/firebase-setup.md`
- Foundation-Nachweis: `docs/foundation-verification.md`
```

- [ ] **Step 4: Dokumentation committen und develop abschließen**

Run:

```bash
git add README.md docs/foundation-verification.md
git diff --cached --check
git commit -m "docs: record foundation verification"
git push -u origin HEAD
gh pr create --base develop --title "Foundation-Gesamtnachweis" --body "Closes #${ISSUE_NUMBER}"
gh pr merge --squash --delete-branch
git checkout develop
git pull origin develop
git status --short --branch
```

Expected: Arbeitsbaum auf `develop` ist sauber. Der dokumentierte Stand entspricht exakt den lokalen und remote ausgeführten Nachweisen. Erst danach beginnt der Folgeplan für die übrigen Bewegungssensoren.
