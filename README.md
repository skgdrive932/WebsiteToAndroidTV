# Website to Android TV APK Creator

Create an installable Android APK that opens your website on Android TV / Haier Smart TVs.
The project is designed to be built from a PC or phone browser using **GitHub Actions**.

## What it does
- Packages a website URL inside an Android WebView app.
- Full-screen landscape TV experience.
- Android TV launcher entry (`LEANBACK_LAUNCHER`) and regular Android launcher entry.
- Remote-control D-pad / OK / Back navigation support.
- Internet permission, JavaScript, DOM storage and cookies.
- APK build through GitHub Actions with editable app name and website URL.

## Important compatibility note
This works on Android-based Haier TVs that support installing Android APKs and include a compatible WebView provider. It will **not** work on Haier TVs running a non-Android operating system. Some websites also block embedded browsers, require unsupported DRM, or are not designed for TV remotes.

## Build an APK without a PC
1. Create a GitHub account and upload this repository (or use **Add file → Upload files**).
2. Open the repository's **Actions** tab and enable workflows if prompted.
3. Select **Build Website TV APK** → **Run workflow**.
4. Enter:
   - `website_url`: full address, e.g. `https://example.com`
   - `app_name`: name shown under the TV app icon
5. Run the workflow. When it finishes, open the run and download the `website-tv-apk` artifact.
6. Extract the artifact ZIP. Install the APK on your TV using USB or a trusted file-transfer app.

## Build locally (Android Studio)
- Android Studio with JDK 17
- Android SDK Platform 35
- Open this folder in Android Studio and build `app` → `assembleDebug`.

Set a default URL in `app/build.gradle.kts` by changing `defaultConfig.buildConfigField`.

## Customize app icon
Replace the launcher images in `app/src/main/res/mipmap-*` with your own PNG assets, keeping the same filenames. For best results use a square icon and a 320×180 TV banner at `drawable/tv_banner.png`.

## Security
Only package websites you own or are authorized to use. This app does not bypass subscriptions, DRM, authentication, or website restrictions. Use HTTPS URLs. The WebView blocks mixed content and cancels invalid SSL certificates.

## License
MIT License — Copyright (c) 2026 SK Kaushal
