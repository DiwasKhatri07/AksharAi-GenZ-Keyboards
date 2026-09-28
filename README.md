# Akshar Ai - GenZ Keyboards 🇳🇵✨

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_8.0+_(API_26+)-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/diwaskhatri07)

> **Akshar Ai - GenZ Keyboards** is a lightning-fast, production-ready Android Input Method Editor (IME) tailored for **Nepali Devanagari**, **Nepinglish (Roman Nepali)**, and **English** typing. Packed with gesture swipe typing, contextual sentiment emoji recommendations, on-device transliteration, dual-engine AI writing (Groq LLaMA 3.1 & Google Gemini), iOS quick editing tools, tactile haptics, and privacy-first local storage.

Developed with ❤️ by **Diwas Khatri** ([@diwaskhatri07](https://github.com/diwaskhatri07)).

---

## 🌟 Highlights & Features

### 1. Real Android System Input Method Editor (IME)
- Conforms to Android `InputMethodService` specifications for seamless system-wide input.
- Works across all apps: WhatsApp, Instagram, TikTok, Chrome, Telegram, YouTube, and Notes.
- Automatic **Password & OTP Shielding**: When focused on password or numeric PIN fields, autocorrect, predictive caching, and AI toolbars are immediately turned off for user privacy.

### 2. Gesture-Based Swipe Typing Recognition
- Maps user drag trajectories across keys using real-time spatial path sampling and edit distance algorithms.
- **Dual-Script Prediction**: Swiping English letters (e.g. `n-a-m-a-s-t-e` or `d-a-m-i`) automatically outputs both Nepinglish (`namaste`, `dami`) and native Devanagari (`नमस्ते`, `दामी`).
- Vivid glowing neon gesture trail with smooth quadratic Bézier curves that fade smoothly upon release.

### 3. Smart Nepali & Nepinglish Engine
- **Devanagari Layout**: Full consonants, vowels, halanta (्), nukta, matras, and native numbers (०..९).
- **Intelligent Transliteration**: Sub-millisecond rule-based engine converting Roman input (`kasto`, `sanchai`, `hajur`, `mero`, `khana`, `nepal`) into clean Devanagari.
- **Nepinglish Autocorrect & Normalization**: Automatically repairs slang abbreviations (`xa` → `cha`, `tmi` → `timi`, `garxu` → `garchu`, `xaina` → `chaina`).
- **Gen-Z Slang Dictionary**: Built-in support for popular youth vernacular (`babaal`, `khatra`, `sahii`, `vibe`, `chill`, `sigma`, `based`, `solti`, `momo`, `chiya`).

### 4. Lightweight On-Device Sentiment Emoji Strip
- Real-time sentence sentiment classifier running locally without network overhead.
- Identifies emotions (Celebration/Fire 🔥, Love ❤️, Laughter 😂, Cool/Sigma 🗿, Respect 🙏, Hangout ☕) and surfaces instant one-tap reaction emojis in a dedicated strip.

### 5. Multi-Provider AI Writing Assistant
- **12 Writing Modes**: Rewrite, Expand, Shorten, Grammar Correction, Translate (Nepali ↔ English), Emojify, Gen-Z, Funny, Professional, Casual, Sigma/Chad, and AI Reply Generator.
- **Dual Provider Support**:
  - **Groq Cloud**: Ultra-low latency `llama-3.1-8b-instant`.
  - **Google Gemini**: High quality `gemini-2.5-flash`.
- **Fail-Safe Offline Mode**: If offline or if API keys are omitted, an on-device rule engine takes over seamlessly so the app **never crashes**.

### 6. Privacy-First Local Clipboard & Utilities
- Stores up to 10 recent clips locally in an isolated SQLite Room database.
- Pin favorite messages, single-tap paste, and clear unpinned clips.
- iOS-style floating blue action bar for instant Select All, Copy, Cut, and Paste.
- Spacebar cursor drag navigation for precision editing.
- Built-in Voice Typing (`SpeechRecognizer`) and Nepali/English Text-to-Speech (TTS).

---

## 🏗️ Architecture Overview

```
akshar-ai-keyboard/
├── app/src/main/java/com/example/
│   ├── ai/                      # Multi-provider AI (Groq, Gemini, Local Rule Engine)
│   ├── data/
│   │   ├── local/               # Room Database (ClipboardEntity, ClipboardDao)
│   │   └── preferences/         # User settings (Themes, Haptics, Sound, Languages)
│   ├── engine/
│   │   ├── emoji/               # SentimentEmojiEngine & EmojiDictionary
│   │   ├── nepali/              # DevanagariLayouts & NepaliTransliterationEngine
│   │   ├── nepinglish/          # NepinglishEngine & GenZSlangDictionary
│   │   ├── prediction/          # Next-word bigram & SuggestionItem pipeline
│   │   └── swipe/               # SwipeTypingEngine & trajectory matching
│   ├── ime/                     # NepaliInputMethodService (Android IME lifecycle)
│   └── ui/
│       ├── keyboard/            # KeyboardRootView, KeyView, SuggestionBar, Panels
│       ├── screens/             # HomeScreen, ThemesScreen, Privacy, Settings
│       └── theme/               # Material 3 Color Schemes & 7 Curated Presets
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or JDK 21
- Android SDK 35 (Android 15)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/diwaskhatri07/akshar-ai-keyboard.git
   cd akshar-ai-keyboard
   ```
2. Configure optional API keys:
   ```bash
   cp .env.example .env
   # Edit .env and insert GROQ_API_KEY or GEMINI_API_KEY if desired
   ```
3. Run test suites:
   ```bash
   gradle :app:testDebugUnitTest
   ```
4. Assemble Debug APK:
   ```bash
   gradle :app:assembleDebug
   ```

### Enabling the Keyboard on Android
1. Open **Android Settings** → **System** → **Languages & input** → **On-screen keyboard**.
2. Tap **Manage on-screen keyboards** and toggle **Akshar Ai Keyboard** to **ON**.
3. Tap **Change keyboard** (or keyboard icon on navigation bar) and select **Akshar Ai**.

---

## 🔒 Privacy Commitment

- **No Secret Logging**: Keystrokes are never transmitted or monetized.
- **Password Protection**: Learning, suggestions, and AI are strictly disabled on password and PIN inputs.
- **Local Storage**: All clipboard and settings data reside exclusively inside your device's isolated SQLite database.
- Read our full [Privacy Policy](PRIVACY.md).

---

## 🗺️ Feature Roadmap

- [x] Android InputMethodService baseline
- [x] Romanized Nepali to Devanagari transliteration
- [x] Gen-Z Nepali slang and phrase auto-expansion
- [x] Gesture Swipe Typing with dual-script prediction
- [x] Dynamic sentiment emoji suggestion strip
- [x] Cloud AI (Groq + Gemini) with offline fallback
- [x] Room Database Clipboard Manager with pin support
- [ ] Multilingual voice typing with offline Vosk/Whisper model
- [ ] User custom personal dictionary editor in settings
- [ ] Cloud-free sticker and GIF picker integration

---

## 👨‍💻 Author & Credits

- **Creator & Lead Engineer**: [Diwas Khatri](https://github.com/diwaskhatri07)
- **GitHub**: [@diwaskhatri07](https://github.com/diwaskhatri07)
- **Email**: diwaskhatri935@gmail.com

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
