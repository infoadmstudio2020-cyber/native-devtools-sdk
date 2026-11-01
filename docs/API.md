# NativeDevToolsSDK API Reference

## NativeDevToolsSDK

Main entry point for the library.

### Methods

#### `static getInstance()`
Returns the singleton instance.

#### `init(Context context, NativeDevToolsSDKConfig config)`
Initializes the library. Must be called before any other method.

**Parameters:**
- `context` — application context
- `config` — configuration object

#### `isInitialized()`
Returns `true` if the library has been initialized.

#### `getConfig()`
Returns the current `NativeDevToolsSDKConfig` or null.

#### `release()`
Releases all resources. After calling this, `getInstance()` returns a fresh instance.

#### `getDebugDrawer()`
Returns the in-app debug drawer / bubble controller.

#### `getNetworkInspector()`
Returns the network traffic snooper and HAR exporter.

#### `getFpsMonitor()`
Returns the live FPS, frame render time, and memory monitor.

#### `getCrashReporter()`
Returns the uncaught exception interceptor and stack trace logger.

---

## Subsystem Services & Handlers

- `NativeDevToolsService`: Layer 2 domain service extending `BaseService` from `NativeServiceSDK`, registered in `ServiceRegistry`.
- `DevToolsCommandHandler`: Implements `IApiHandler` handling `devtools/*` routes (`getMetrics`, `getHealth`, `getBridgeMetrics`, `getManifest`, `exportHar`, `clearTraffic`, `startProfiling`, `stopProfiling`, `dispatchCdp`).
- `DevToolsServer`: Implements CDP target discovery (`/json/version`, `/json/list`) and message dispatching.


---

## NativeDevToolsSDKConfig.Builder

| Method | Default | Description |
|--------|---------|-------------|
| `debugEnabled(boolean)` | `false` | Enable debug logging |
| `tag(String)` | `"NativeDevToolsSDK"` | Log tag |
| `timeout(int)` | `30000` | Timeout in milliseconds |
| `build()` | — | Create the config |

---

## NativeDevToolsSDKResult<T>

| Method | Description |
|--------|-------------|
| `isSuccess()` | True if operation succeeded |
| `getData()` | Result data or null |
| `getErrorMessage()` | Error message or null |
| `getErrorCode()` | Numeric error code |
