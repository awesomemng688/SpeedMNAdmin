# Fix Compose Test Dependency Resolution Issue

The project is failing to resolve `androidx.compose.ui:ui-test-junit4` because the Compose BOM (Bill of Materials) is only applied to the `implementation` configuration, but not to `androidTestImplementation` and `debugImplementation` which use Compose dependencies without explicit versions.

## Proposed Changes

### [app] Component

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/app/build.gradle.kts)
- Add `androidTestImplementation(platform(libs.androidx.compose.bom))` to allow resolution of Compose test dependencies.
- Add `debugImplementation(platform(libs.androidx.compose.bom))` to allow resolution of Compose debug dependencies (like `ui-test-manifest`).

## Verification Plan

### Automated Tests
- Run Gradle sync to verify that all dependencies are resolved.
- Run a simple build to ensure no other dependency issues exist.
