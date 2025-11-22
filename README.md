# VoiceSense Sample Task

Simple Android prototype built with **Kotlin + Jetpack Compose** for an intern assignment.

## What this app does

- Start Screen with a “Start Sample Task” button.
- **Noise Test** screen:
  - Simulated ambient noise meter (0–60 dB) with a semicircular gauge.
  - Shows if the user can proceed (“Good to proceed” / “Please move to a quieter place”).
- **Task Selection** screen to choose:
  - Text Reading Task
  - Image Description Task
  - Photo Capture Task
  - Task History
- **Text Reading Task**:
  - Shows a sample passage.
  - Press‑and‑hold mic button to record (10–20 seconds).
  - Validates duration and lets user submit.
- **Image Description Task**:
  - Loads a sample image from the internet with Coil.
  - User records an audio description (10–20 seconds) and submits.
- **Photo Capture Task**:
  - Uses the camera to capture a preview image.
  - User types a description and can optionally record audio.
- **Task History**:
  - Lists all completed tasks in memory with type, duration and timestamp.

All tasks are stored **locally in memory** only (no backend, no login).

## Tech stack

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- ViewModel + StateFlow
- MediaRecorder (audio)
- Coil (image loading)

## How to run

1. Clone the project and open it in Android Studio.
2. Let Gradle sync.
3. Run on a device/emulator.
4. Grant **Camera** and **Microphone** permissions when asked.

## Notes

- This is a prototype for practice only.
- Closing the app clears the task history.
