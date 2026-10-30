# Walkthrough - Clean Up Unresolved Activity References in AndroidManifest.xml

Removed unresolved activity registrations (`GuruMainActivity` and `OperatorMainActivity`) from [AndroidManifest.xml](file:///C:/Users/ASUS/AndroidStudioProjects/absensi-kotlin/app/src/main/AndroidManifest.xml) to resolve app startup freezes and ensure clean compilation.

## Changes Implemented

### Manifest Cleanup
- **[AndroidManifest.xml](file:///C:/Users/ASUS/AndroidStudioProjects/absensi-kotlin/app/src/main/AndroidManifest.xml)**:
  - Removed `<activity android:name=".ui.guru.GuruMainActivity" />` and `<activity android:name=".ui.operator.OperatorMainActivity" />`.

## Verification
- Executed `./gradlew clean :app:assembleDebug` with a successful build result (`BUILD SUCCESSFUL`).
