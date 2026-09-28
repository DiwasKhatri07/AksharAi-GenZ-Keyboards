# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |

## Security Guarantees & Architecture

**Akshar Ai - GenZ Keyboards** is built with strict privacy and security standards:

1. **Zero Secret Logging**:
   - The keyboard NEVER runs background keyloggers.
   - Normal keystrokes and typing sequences remain completely on device memory and are never uploaded to remote servers.

2. **Password & OTP Shielding**:
   - In accordance with Android IME security standards, all password, PIN, and numeric credential fields (`TYPE_TEXT_VARIATION_PASSWORD`, `TYPE_TEXT_VARIATION_WEB_PASSWORD`, `TYPE_NUMBER_VARIATION_PASSWORD`) immediately disable text caching, suggestions, autocorrect, and AI writing prompts.

3. **Explicit AI Processing Only**:
   - Text is ONLY passed to LLM endpoints (Groq / Gemini) when the user explicitly triggers an AI action button in the AI toolbar.
   - User inputs are never shared with unauthorized third parties or used to train public models.

4. **Local SQLite Storage**:
   - Clipboard entries and saved personal words are stored exclusively on the device using an encrypted/isolated Room SQLite database (`aayo_keyboard.db`).

## Reporting a Vulnerability

If you discover a security vulnerability within Akshar Ai, please report it privately:

- **Maintainer**: Diwas Khatri
- **GitHub**: [@diwaskhatri07](https://github.com/diwaskhatri07)
- **Email**: diwaskhatri935@gmail.com

Please do not publicly disclose the issue until it has been reviewed and addressed.
