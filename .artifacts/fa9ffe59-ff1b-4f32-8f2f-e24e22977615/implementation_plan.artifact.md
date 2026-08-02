# Implementation Plan - Fix Obsolete 'testVariants' API Usage

The project is using Android Gradle Plugin (AGP) 9.3.1, which has deprecated and moved away from the legacy Variant API (`testVariants`, `applicationVariants`, etc.). The project currently has `android.newDsl=false` in `gradle.properties`, which enables the legacy API but triggers a warning/error about its obsolescence in AGP 9.0+ and planned removal in 10.0.

Since the project build scripts do not directly use these APIs, the issue is likely caused by outdated third-party plugins (Hilt and KSP) that depend on the legacy Variant API.

## Proposed Changes

### [Component Name] Gradle Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/gradle/libs.versions.toml)
- Update `hilt` from `2.51.1` to `2.60.1`.
- Update `ksp` from `2.2.10-2.0.2` to `2.3.10`.

#### [MODIFY] [gradle.properties](file:///C:/Users/Awesome/AndroidStudioProjects/SpeedMNAdmin/gradle.properties)
- Remove `android.newDsl=false` to adopt the new AGP DSL and Variant API.
- Update `android.sync.suppressAgpWarnings` to `LIBRARY_CONSTRAINTS_SHOULD_BE_DISABLED` to address the library constraints performance warning.
- Optionally enable `android.dependency.excludeLibraryComponentsFromConstraints=true` for better import performance.

## Verification Plan

### Automated Tests
- Run `gradle_sync` to verify the "obsolete API" warning/error is gone.
- Run `gradle_build app:assembleDebug` to ensure the project compiles successfully with updated dependencies.

### Manual Verification
- None required as this is a build-time configuration change.
