# Privacy policy — Akshar AI - GenZ Keyboards

**Effective:** September 28, 2026
**Application ID:** `dev.aksharai.app`
**Maintainer:** Diwas Khatri

Akshar AI is an Android keyboard and companion app. A keyboard can receive text that you type in other apps. This policy explains the app's on-device handling, optional network features, and controls. The application source is available in this repository.

## Information and processing

- **Text entered with the keyboard:** Android sends typed characters to the focused app through the input-method interface. The app's transliteration, candidate suggestions and autocorrect operate on the device. The app does not need an account on its own server.
- **Password editors:** For Android input fields that explicitly identify themselves as password fields, the keyboard suppresses its suggestions and clipboard capture and hides sensitive actions. This safeguard depends on the field correctly reporting its input type; it cannot detect every private or sensitive text field.
- **Optional AI transformation:** If you invoke an AI mode, text selected for transformation may be sent over HTTPS to Groq or Google Gemini when a provider is configured. The app may try the available cloud providers and fall back to an on-device rules engine if a cloud request is unavailable. The provider processes the request under its own policies and terms. Do not send confidential or sensitive information to an AI provider.
- **Custom AI key:** A key you enter is kept in the app's private Android preferences and attached to applicable provider requests. The app does not itself encrypt this preference. Keep your device protected and do not share your key.
- **Clipboard:** When the keyboard is active in a non-password field, it can read the Android primary clipboard and save clipboard items in local app storage for the Clips panel. Use the in-app retention choices and delete clips you do not want retained. Android may limit clipboard reads depending on OS version and app focus.
- **AI history and settings:** Keyboard settings are stored locally. AI history is optional and disabled by default; when enabled, original text and AI output are saved locally until deleted or app data is cleared.
- **Voice typing:** If you tap the microphone/voice action and grant permission, the Android speech-recognition service selected on your device receives audio for transcription. That service may use network processing; its handling is governed by the provider/device settings, not this app.
- **Diagnostics and metrics:** The app code in this release has no app-operated account or analytics backend. GitHub traffic metrics mentioned in this repository concern the public source repository, not keyboard users.

## Storage, retention, and backups

App preferences, clipboard entries, saved words/phrases, themes and optional AI history are stored on the device. Clipboard retention can be configured in the app; items otherwise remain until removed, app data is cleared or the app is uninstalled. AI history is opt-in. Android backup/restore may apply according to Android version, device configuration and the user's backup settings; this app does not promise that local records are excluded from every backup path.

## Permissions

The app declares Internet access for optional AI requests, microphone access for voice typing, and vibration for haptic feedback. Microphone use is initiated by the user; Android may separately show the permission prompt.

## Sharing and third parties

The app's optional cloud-AI feature shares only the text submitted for that action and the request information required by the configured provider. It does not control provider retention, regional processing, model-use or deletion rules. Review the applicable provider policy before enabling cloud AI. Android speech recognition is a separate system/provider service. This project does not sell keyboard text or provide a user-tracking service.

## Your choices

You can use the local keyboard features without configuring an AI API key. Avoid the AI and voice actions for sensitive text. Turn off/limit clipboard capture and AI history using available in-app controls, delete stored clips/history, clear the app's data, or uninstall the app. Android settings let you disable the keyboard or revoke microphone permission.

## Children and changes

This app is not directed to children and does not knowingly collect information from children through a developer-operated service. This policy may change as app features change; a revised copy will be published with a new effective date.

## Contact

For privacy questions, contact the maintainer through [GitHub](https://github.com/DiwasKhatri07) or use the repository's issue/discussion channels. Do not post API keys, private text or other sensitive material in a public issue.
