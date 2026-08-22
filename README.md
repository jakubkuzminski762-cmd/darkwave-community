# Darkwave Community for Android

Native Android application for the Darkwave Interactive community at `veloryx.pl`.

## What is included

- native Jetpack Compose interface (not a WebView or PWA),
- English-first interface with Polish language switch,
- sign in, account registration, CAPTCHA and two-factor authentication,
- conversations, unread counters, friend search and invitations,
- forum feed and profile progression,
- Firebase Cloud Messaging receiver and Android notification bubbles,
- GitHub Actions build for test and signed production APKs.

The Firebase Android client configuration belongs in `app/google-services.json`. The committed file contains only the Android client configuration; it is not the private server service-account key.

## Release signing

Add these repository Actions secrets before distributing an APK:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

The workflow always compiles a short-lived debug artifact. It produces the distributable `darkwave-community.apk` only when all four release-signing secrets exist. Keep the original keystore and passwords backed up: every future update must be signed with the same key.

Production releases are built automatically from the protected signing secrets stored in GitHub Actions.

## Push delivery

The Android client is ready to receive Firebase messages. Background delivery also needs a private Firebase service-account JSON on the PHP server plus a `/api/mobile/push/register` implementation. Never commit the service-account file to this public repository.
