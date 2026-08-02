# Fix Compose Test Dependency Resolution Error

The user is experiencing a sync error: `Failed to resolve: androidx.compose.ui:ui-test-junit4`. This occurs because the library is declared without a version in the version catalog, intending to use the Compose BOM (Bill of Materials), but the BOM is only applied to the `implementation` configuration. In Gradle, version constraints from a BOM must be explicitly applied to each configuration that needs them.

## Proposed Changes

### [app](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/app)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/app/build.gradle.kts)

- Add `androidTestImplementation(platform(libs.androidx.compose.bom))` to provide versions for `ui-test-junit4` and other compose libraries used in instrumentation tests.
- Add `debugImplementation(platform(libs.androidx.compose.bom))` to provide versions for `ui-tooling` and `ui-test-manifest`.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify that the dependencies are resolved correctly.
- Run `./gradlew :app:assembleDebugAndroidTest` to ensure test dependencies are correctly linked.
