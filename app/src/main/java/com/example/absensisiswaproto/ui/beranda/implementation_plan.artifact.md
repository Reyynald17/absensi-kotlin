# Implementation Plan - Clean Up Unresolved Activity References in AndroidManifest.xml

Remove or comment out activity registrations in `AndroidManifest.xml` whose corresponding Kotlin source files (`GuruMainActivity.kt`, `OperatorMainActivity.kt`) do not exist, ensuring clean build compilation and successful app launch into the Login screen.

## User Review Required

> [!IMPORTANT]
> This plan removes or comments out the entries for `GuruMainActivity` and `OperatorMainActivity` from `AndroidManifest.xml` as requested.

## Proposed Changes

### Manifest Cleanup

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/ASUS/AndroidStudioProjects/absensi-kotlin/app/src/main/AndroidManifest.xml)
- Comment out or remove `<activity android:name=".ui.guru.GuruMainActivity" />` and `<activity android:name=".ui.operator.OperatorMainActivity" />`.

---

## Verification Plan

### Automated Tests / Build
- Run `./gradlew clean :app:assembleDebug` to verify clean compilation.

### Manual Verification
- Deploy app and confirm it successfully launches past `SplashActivity` into `LoginActivity`.
