# Android build and release

- **App:** Akshar AI - GenZ Keyboards
- **Application ID:** `dev.aksharai.app`
- **Current release:** `v0.013` (version code 13)
- **Build:** Android Gradle Plugin, Kotlin, Jetpack Compose, Gradle wrapper

## Local builds

```bash
./gradlew testDebugUnitTest assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. A minified, resource-shrunk release build uses the signing inputs `KEYSTORE_PATH`, `STORE_PASSWORD` and `KEY_PASSWORD`:

```bash
export KEYSTORE_PATH=/secure/path/to/your-upload-key.jks
export STORE_PASSWORD='...'
export KEY_PASSWORD='...'
./gradlew assembleRelease
```

Do not commit signing keys or passwords. A new key signs a different app identity from the distributed v0.013 release; Android/Play update installation requires the matching private upload key.

## Automated releases

`.github/workflows/android-release.yml` can build a tagged release and attach APKs/checksums after a maintainer manually dispatches it with the selected existing version tag. Before doing so for a new release, add these repository Actions secrets using the same private signing key used for the existing release:

- `ANDROID_RELEASE_KEYSTORE_BASE64` — base64-encoded keystore file
- `ANDROID_STORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`

The keystore alias is `upload`. The workflow decodes it only into the temporary runner and never commits it. Keep an offline backup of the key. If the original matching private key is unavailable, do not overwrite/update the existing Android package with a newly signed APK; publish a separately identified build instead.

## Current artifacts

The v0.013 release attaches the optimized release APK, debug APK, and `SHA256SUMS.txt`. Debug APK is larger and intended for testing; the optimized release APK is the normal install option. Both variants share the application ID but have different signing keys, so they cannot normally be installed side-by-side.
