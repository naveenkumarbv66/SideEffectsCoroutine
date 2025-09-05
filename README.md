## SideEffects + Coroutine Demo (Jetpack Compose)

This sample demonstrates two ways to run and cancel coroutines from Compose:
- LaunchedEffect(key)
- rememberCoroutineScope()

### Screen
`SideEffectsDemo` in `app/src/main/java/com/naveen/sideeffectscoroutine/MainActivity.kt`.

Buttons:
- Increment (triggers LaunchedEffect)
  - Increments `tickCount` (the LaunchedEffect key) and starts an effect.
  - Previous effect is cancelled automatically on key change.
- Do async work (rememberCoroutineScope)
  - Starts a cancellable job via `rememberCoroutineScope()`.
  - If a job is already running, it cancels then starts a new one.
- Cancel async work
  - Cancels the current scope-launched job.
- Cancel LaunchedEffect
  - Cancels the current LaunchedEffect job manually.

### Code highlights
- LaunchedEffect with manual cancel handle:
```kotlin
var launchedJob by remember { mutableStateOf<Job?>(null) }
LaunchedEffect(tickCount) {
    launchedJob = coroutineContext.job
    try {
        delay(1000)
        message = "LaunchedEffect observed tick=$tickCount"
    } catch (e: CancellationException) {
        message = "LaunchedEffect cancelled"
        throw e
    } finally {
        launchedJob = null
    }
}
```

- Scope-launched job with cancel/restart:
```kotlin
var runningJob by remember { mutableStateOf<Job?>(null) }
val scope = rememberCoroutineScope()

runningJob?.cancel()
runningJob = scope.launch {
    try {
        message = "Working..."
        repeat(5) { delay(300) }
        message = "Completed via rememberCoroutineScope"
    } catch (e: CancellationException) {
        message = "Cancelled"
        throw e
    } finally {
        runningJob = null
    }
}
```

- Manual cancels:
```kotlin
launchedJob?.cancel()
runningJob?.cancel()
```

### When to use what
- Use `LaunchedEffect(key)` for work tied to composition and key changes.
- Use `rememberCoroutineScope()` for user-driven, on-demand tasks.

### Run
Open in Android Studio and run the `app` module on a device/emulator.

## POST demo (MVVM + Retrofit + WorkManager)

This project also includes a demo Activity showing a Retrofit POST request and a periodic background POST using WorkManager, organized with MVVM + Repository.

### How to try
- From the main screen, tap "Open POST demo".
- In Post Demo:
  - Tap "Send one-time POST" to POST to `https://jsonplaceholder.typicode.com/posts`.
  - Tap "Schedule periodic POST (15 min)" to enqueue periodic background work.

Note: WorkManager enforces a minimum 15-minute interval for periodic work and may batch execution. Exact timing is not guaranteed.

### Architecture
- Activity UI: `app/src/main/java/com/naveen/sideeffectscoroutine/PostDemoActivity.kt`
- ViewModel: `app/src/main/java/com/naveen/sideeffectscoroutine/ui/PostViewModel.kt`
- Repository: `app/src/main/java/com/naveen/sideeffectscoroutine/data/PostRepository.kt`
- Retrofit API + models: `app/src/main/java/com/naveen/sideeffectscoroutine/api/Api.kt`
- WorkManager CoroutineWorker: `app/src/main/java/com/naveen/sideeffectscoroutine/worker/PostCoroutineWorker.kt`
- Manifest entries (INTERNET + Activity): `app/src/main/AndroidManifest.xml`

### Recent Fixes
- **Fixed PostUiState.Success display issue**: Resolved problem where `PostUiState.Success(response.id)` was showing variable names instead of actual values
- **Added null safety**: Properly handle nullable ID values from API response with fallback to -1
- **Fixed smart cast issue**: Used local variable capture in `when` expressions to enable proper smart casting
- **Improved error handling**: Enhanced logging and error messages for better debugging

### Code highlights
- Proper null handling in ViewModel:
```kotlin
val postId = response.id ?: -1 // Handle null case
_state.value = PostUiState.Success(postId)
```

- Smart cast fix in Compose:
```kotlin
when (val currentState = uiState) {
    is PostUiState.Success -> {
        Text("ID: ${currentState.id}") // Smart cast works
    }
}
```

### Dependencies added
- Retrofit + Gson converter
- OkHttp + Logging Interceptor
- WorkManager (KTX)
- Lifecycle ViewModel + Runtime Compose

