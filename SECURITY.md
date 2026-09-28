# Security policy

## Supported release

The latest published APK is the version listed on the [Releases page](https://github.com/DiwasKhatri07/AksharAi-GenZ-Keyboards/releases). Please use the latest release when reporting a vulnerability.

## Important security notes

Akshar AI is an Android input method. A keyboard receives text that users type into other apps. The current code processes normal typing suggestions and transliteration on-device and only sends text to a cloud AI provider when an AI action is invoked and a provider key is available. Android-recognized password input types suppress keyboard suggestions and clipboard capture. These safeguards depend on correct editor metadata and are not a guarantee for every sensitive field. Numeric AI checks are heuristic and incomplete.

Clipboard entries, preferences and optional AI history are stored locally. Custom AI API keys are stored in app-private preferences but are not encrypted by the app. Use a test key with minimum provider permissions and revoke it if exposed. Do not publish personal text, API keys or signing material in issues or pull requests.

## Report a vulnerability privately

Please do not open a public issue containing an exploitable vulnerability or private user data. Contact maintainer [Diwas Khatri](https://github.com/DiwasKhatri07) via a GitHub private vulnerability report if enabled for this repository, or by email at `diwaskhatri935@gmail.com`. Include the affected release/commit, impact and a minimal reproduction. Allow time to investigate before public disclosure.
