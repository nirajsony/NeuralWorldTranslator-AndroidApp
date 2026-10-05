# 📱 NeuralWorld Translator — Android App

> Native Android companion app for the NeuralWorld Live Speech Translator.

[![Live App](https://img.shields.io/badge/Live%20App-neuralworld.space-00f2fe?style=for-the-badge)](https://neuralworld.space/)
[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![WebView](https://img.shields.io/badge/Android%20WebView-Integrated-4285F4?style=for-the-badge&logo=googlechrome)](https://developer.android.com/reference/android/webkit/WebView)

## 🌐 Live Application

### 👉 https://neuralworld.space/

NeuralWorld Translator is a Kotlin-based Android application built as the mobile companion for the **NeuralWorld Live Speech Translator**.

The Android app uses a lightweight native shell around the NeuralWorld web experience and adds Android-specific capabilities including microphone permission handling and native Text-to-Speech.

---

## ✨ Features

- 🎙️ Android microphone permission handling
- 🌐 NeuralWorld web application inside Android WebView
- 🔊 Native Android Text-to-Speech
- 🌉 JavaScript → Android TTS bridge
- 🤖 Synchronization with the web application's speaking robot animation
- 📱 Mobile-friendly experience
- 🔐 WebView microphone access restricted to `neuralworld.space`
- 🧩 Lightweight Kotlin implementation
- 🔄 Uses the same NeuralWorld translator experience as the browser version

---

## 🧠 Architecture

The app intentionally keeps the Android layer small.

```text
┌─────────────────────────────┐
│       Android App           │
│       Kotlin / Android      │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          WebView            │
│                             │
│  https://neuralworld.space/ │
└──────────────┬──────────────┘
               │
       JavaScript Bridge
               │
               ▼
┌─────────────────────────────┐
│       AndroidTTS            │
│   Native Text-to-Speech     │
└─────────────────────────────┘
```

### Runtime flow

```text
Microphone
    ↓
WebView / Speech Recognition
    ↓
NeuralWorld Translation UI
    ↓
Browser-side AI translation
    ↓
Translated Text
    ↓
AndroidTTS JavaScript Bridge
    ↓
Android Text-to-Speech
    ↓
Speaker
```

The broader translation pipeline is implemented by the main NeuralWorld translator project; this repository focuses on the native Android integration.

---

## 🔊 Native Android TTS

The Kotlin app exposes a JavaScript interface named:

```text
AndroidTTS
```

The web application can call:

```javascript
window.AndroidTTS.speak(text, language)
```

The Android layer receives the text and selects an appropriate locale.

### Supported language mapping

| Code | Android locale |
|---|---|
| `en` | `en-US` |
| `hi` | `hi-IN` |
| `es` | `es-ES` |
| `ja` | `ja-JP` |
| `zh` | Simplified Chinese |

Android `TextToSpeech` is used for playback, with an `UtteranceProgressListener` tracking speech completion.

When speech finishes, the Android layer removes the `speaking` state from the NeuralWorld robot so the UI animation ends with the audio.

---

## 🎙️ Microphone Permissions

The Android manifest requests:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

When the WebView requests microphone access, the native layer:

1. checks that the request originates from `neuralworld.space`
2. checks Android's microphone permission
3. requests the permission when necessary
4. grants audio capture to the WebView after approval

Microphone requests from other origins are denied.

---

## 🏗️ Project Structure

```text
NeuralWorldTranslator-AndroidApp/
│
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/
│       │   │   └── space/neuralworld/translator/
│       │   │       └── MainActivity.kt
│       │   └── res/
│       │       ├── drawable/
│       │       ├── layout/
│       │       ├── mipmap-*/
│       │       ├── values/
│       │       ├── values-night/
│       │       └── xml/
│       ├── androidTest/
│       └── test/
│
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

## 🧩 MainActivity

The main Android activity is:

```text
space.neuralworld.translator.MainActivity
```

It is responsible for:

- creating and configuring the WebView
- enabling JavaScript
- enabling DOM storage
- loading `https://neuralworld.space/`
- handling WebView microphone requests
- requesting Android microphone permission
- initializing Android Text-to-Speech
- exposing the `AndroidTTS` JavaScript interface
- synchronizing TTS completion with the web UI
- releasing TTS and WebView resources during destruction

---

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| Platform | Android |
| Web host | Android WebView |
| Native speech | Android TextToSpeech |
| JavaScript bridge | Android JavascriptInterface |
| Build system | Gradle Kotlin DSL |
| Android Gradle Plugin | 9.4.1 |
| Compile SDK | 37 |
| Target SDK | 37 |
| Minimum SDK | 24 |
| Java compatibility | Java 11 |
| UI | Material 3 / AppCompat |
| Application ID | `space.neuralworld.translator` |

---

## ▶️ Run Locally

### Prerequisites

Install:

- Android Studio
- Android SDK Platform 37
- JDK with Java 11 compatibility
- Android device or emulator
- Internet access

### Clone the repository

```bash
git clone https://github.com/nirajsony/NeuralWorldTranslator-AndroidApp.git
cd NeuralWorldTranslator-AndroidApp
```

Open the project in Android Studio, allow Gradle to sync, and run the `app` configuration on a device or emulator.

---

## 📲 Permissions

### Internet

Required to load:

```text
https://neuralworld.space/
```

### Microphone

Required for speech input through the WebView.

Android will request the permission at runtime when microphone access is needed.

---

## 🔐 Privacy & Security

The Android application does not contain a separate cloud AI API key.

The app acts primarily as a native Android host for the NeuralWorld web application.

For microphone access, the WebView permission handler restricts audio capture to the `neuralworld.space` origin.

The actual translation, browser AI model loading, caching, and speech-recognition behavior are provided by the NeuralWorld web application and by the capabilities of the Android/WebView environment.

---

## 🌐 Related NeuralWorld Project

This Android application is the companion project for:

### NeuralWorld Live Speech Translator

**Live application:**

https://neuralworld.space/

**Main translator source:**

https://github.com/nirajsony/NeuralWorld.Space.LiveSpeech.Translator

The main translator repository contains the web application and broader translation pipeline, while this repository contains the Android-specific Kotlin layer.

---

## 🚧 Project Status

The Android application is part of the ongoing NeuralWorld project.

Future improvements may include:

- deeper native Android integration
- better Android speech-recognition support
- richer native controls
- improved offline capabilities
- improved background audio handling
- performance tuning
- signed release builds

---

## 👨‍💻 Author

**Niraj Soni**

Built as part of the **NeuralWorld** project.

### 🌐 NeuralWorld

https://neuralworld.space/

---

## ⭐ Support

If you find the project useful or interesting, consider giving the repository a ⭐ on GitHub.

**Android source:**

https://github.com/nirajsony/NeuralWorldTranslator-AndroidApp

**Live application:**

https://neuralworld.space/
