# Contributing to Akshar AI - GenZ Keyboards

Thanks for helping improve Nepali, Nepinglish and English typing. Contributions are welcome in transliteration, dictionaries, accessibility, keyboard layout, privacy, tests, documentation and bug fixes.

## Development setup

- Android Studio with Android SDK Platform 36 / 36.1
- JDK 21 (the checked-in Gradle wrapper downloads the required Gradle distribution)
- Git and an Android device or emulator for manual keyboard testing

```bash
git clone https://github.com/DiwasKhatri07/AksharAi-GenZ-Keyboards.git
cd AksharAi-GenZ-Keyboards
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Install `app/build/outputs/apk/debug/app-debug.apk` on a test device. Enable the IME in Android Settings and select it from the keyboard picker. Keyboard behavior varies across apps and Android versions, so test both the companion UI and an actual text field where possible.

## Optional AI setup

Cloud AI keys are optional. Copy `.env.example` to `.env` for a local development environment and add your own provider key if needed. Keep `.env`, API keys, signing files and passwords out of commits, screenshots, logs, and issue reports. The local rule-based fallback can be tested without a key.

## Pull requests

1. Open or link an issue when the change is substantial; describe the problem and expected behavior.
2. Make a focused branch and keep changes small enough to review.
3. Add/update tests for behavior changes. Include a short device/Android version note for keyboard-specific changes.
4. Run `./gradlew testDebugUnitTest assembleDebug` and report any unrun checks.
5. Update user-facing documentation when features, permissions or privacy behavior changes.
6. Submit a pull request with a clear summary and screenshots only when useful. Never include real user text, private browser pages or credentials in showcase assets.

## Design and privacy principles

- Prefer on-device processing for typing features.
- Treat text received through the IME as sensitive. Never add telemetry or cloud transmission without clear disclosure and an explicit user action.
- Preserve Android password-field safeguards and do not cache password/OTP content.
- Do not claim perfect OTP/card detection; heuristic checks are incomplete.
- Keep UI readable, accessible and usable on different screen sizes.
- Add tests for dictionary, transliteration and sensitive-field behavior where practical.

## Code style

Follow the existing Kotlin, Compose, Room and coroutine patterns in the codebase. Avoid expensive allocations and blocking I/O on the UI thread. Use descriptive names and comments for non-obvious language rules.

## Community

Be respectful and constructive. The [Code of Conduct](CODE_OF_CONDUCT.md) applies to issues, reviews and pull requests. For security issues, see [SECURITY.md](SECURITY.md) instead of posting details publicly.
