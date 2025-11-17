# JoshTalks Sample Task App
 
  ## Description: "KMM + Compose Multiplatform app for sample audio tasks, photo capture, and task history.


features:
  - "Noise Level Test with animated 0–60 dB gauge"
  - "Text Reading Task with API text, mic recording, validation, and playback"
  - "Image Description Task with HTTPS image loading + recording"
  - "Photo Capture Task with camera intent, description input, and audio recording"
  - "Custom Playback Preview Card with animation"
  - "Task History screen with total tasks & duration"
  - "Scrollable responsive UI design"
  - "Local JSON-based storage for tasks"

## technical_stack:
 - language: "Kotlin"
 - ui: "Compose Multiplatform (Android + Desktop supported)"
 - architecture: "State-driven navigation (NavController)"
  - networking: "DummyJSON API (HTTPS image fix applied)"
  - image_loading: "Coil 3"
  - audio_recording: "MediaRecorder"
  - local_storage: "Kotlinx Serialization"
  - permissions: "Record Audio + Camera"

## issue Fiexed & option feature added:
 - issue_fixed: "Original assignment images were HTTP (insecure). Coil requires HTTPS. Replaced with secure HTTPS."
 - playback_requirement: "Playback bar was optional. A custom playback animation card implemented."
 - all_requirements_completed: true

## Downlaod apk:
  link: https://github.com/saifikram969/JoshTalkAssignment/releases/download/v1.0/composeApp-debug.apk

## APP Overview
https://github.com/user-attachments/assets/a1186fad-3102-431b-8ff0-de964d110171


This is a Kotlin Multiplatform project targeting Android.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It cont


ains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
