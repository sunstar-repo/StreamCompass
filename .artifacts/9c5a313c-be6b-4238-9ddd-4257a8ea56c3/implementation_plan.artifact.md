# Implementation Plan - Create Preview for StreamCompassApp

The goal is to create a `@Preview` Composable function for the `StreamCompassApp` in `StreamCompassApp.kt`.

## User Review Required

> [!IMPORTANT]
> `StreamCompassApp` depends on `DashboardScreen`, which uses Koin to inject a `ViewModel`. To make the preview work correctly and follow Jetpack Compose best practices (specifically the "View Models" rule), I need to refactor `DashboardScreen` to separate its UI logic from the `ViewModel`.

## Proposed Changes

### Presentation Layer

#### [MODIFY] [DashboardScreen.kt](file:///D:/workspace/StreamCompass/architecture/presentation/src/commonMain/kotlin/com/sunstar/streamcompass/presentation/dashboard/DashboardScreen.kt)
- **Refactor**: Extract a new `@Composable` method `DashboardContent` that takes `LazyPagingItems<Stream>` as a parameter.
- **Update**: Modify `DashboardScreen` to collect state from the `ViewModel` and call `DashboardContent`.
- **Preview**: Implement `DashboardScreenPreview` using sample data and `PagingData.from()`.

#### [MODIFY] [StreamCompassApp.kt](file:///D:/workspace/StreamCompass/architecture/presentation/src/commonMain/kotlin/com/sunstar/streamcompass/presentation/StreamCompassApp.kt)
- **New Preview**: Add `StreamCompassAppPreview` at the bottom of the file.
- **Dependency Handling**: Since `StreamCompassApp` is a top-level component that calls `DashboardScreen()` (which still defaults to `koinViewModel()`), I will wrap the preview in a `KoinApplication` with a mock module providing the `DashboardViewModel`. This ensures the preview renders exactly what `StreamCompassApp` would show in the app.

## Verification Plan

### Automated Tests
- Call `analyze_file` on `DashboardScreen.kt` and `StreamCompassApp.kt` to ensure no syntax errors.
- Run `gradle_build` to verify the project still compiles.

### Manual Verification
- Run `render_compose_preview` for `StreamCompassAppPreview` and `DashboardScreenPreview`.
- Verify the rendered images show the "Now Playing" section with sample data.
