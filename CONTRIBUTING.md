# Contributing to Akshar Ai - GenZ Keyboards 🇳🇵✨

Thank you for your interest in contributing to **Akshar Ai - GenZ Keyboards**! We welcome community contributions to expand Nepali dictionaries, improve transliteration accuracy, enhance UI themes, optimize battery and typing latency, and build intelligent on-device features.

## 🛠️ Development Setup

1. **Prerequisites**:
   - Android Studio Ladybug or newer / Hedgehog+
   - JDK 17 or JDK 21
   - Android SDK 35 (Android 15) with Build Tools 35.0.0
   - Kotlin 2.0+

2. **Clone & Open**:
   ```bash
   git clone https://github.com/diwaskhatri07/akshar-ai-keyboard.git
   cd akshar-ai-keyboard
   ```
   Open the root directory in Android Studio.

3. **Environment Setup**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Add your optional Groq or Gemini API keys to test cloud AI modes (offline fallback rules are active by default).

4. **Build & Run**:
   ```bash
   gradle :app:assembleDebug
   gradle :app:testDebugUnitTest
   ```

---

## 📐 Architecture Guidelines

- **Zero Unnecessary Recompositions**: All keys and layouts must use `remember` and avoid memory allocations in touch critical paths.
- **Privacy First**: Sensitive password and OTP fields must NEVER send keystrokes to predictive caches or cloud services.
- **On-Device First**: Language transliteration, Nepinglish normalization, swipe typing, and sentiment emojis must run entirely on-device with sub-millisecond execution.
- **Clean Architecture & Room Persistence**: User configurations and clipboard entries are persisted locally in SQLite Room (`AppDatabase`).

---

## 🚀 How to Submit a Pull Request

1. Fork the repo and create your feature branch:
   ```bash
   git checkout -b feature/awesome-nepali-feature
   ```
2. Write clean Kotlin code following Material 3 guidelines and project conventions.
3. Add unit tests for your feature in `app/src/test/`.
4. Ensure all unit tests pass:
   ```bash
   gradle :app:testDebugUnitTest
   ```
5. Commit your changes with clear, descriptive commit messages.
6. Push to your branch and open a Pull Request.

---

## 💬 Community & Questions

Developed with ❤️ by **Diwas Khatri** ([@diwaskhatri07](https://github.com/diwaskhatri07)).
For bugs or feature proposals, please submit an issue on GitHub!
