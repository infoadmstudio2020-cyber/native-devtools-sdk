# NativeDevToolsSDK

In-app debugging toolkit, network inspector, FPS monitor, and crash logger.

[![JitPack](https://jitpack.io/v/infoadmstudio2020-cyber/native-devtools-sdk.svg)](https://jitpack.io/#infoadmstudio2020-cyber/native-devtools-sdk)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache-2.0-blue.svg)](LICENSE)

## Installation

### Step 1 — Add JitPack to your root build.gradle

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2 — Add the dependency

```gradle
dependencies {
    implementation 'com.github.infoadmstudio2020-cyber:native-devtools-sdk:1.0.0'
}
```

## Usage

```java
NativeDevToolsSDKConfig config = NativeDevToolsSDKConfig.builder()
    .debugEnabled(BuildConfig.DEBUG)
    .tag("AppDevTools")
    .serverPort(9222)
    .floatingBubbleEnabled(true)
    .shakeDetectionEnabled(true)
    .build();

NativeDevToolsSDK.getInstance().init(context, config);

// Toggle floating debug drawer / bubble
DevToolsSDK.getDebugDrawer().toggle();

// Record / Inspect Network Traffic
DevToolsSDK.getNetworkInspector().record(url, method, statusCode, reqHeaders, resHeaders, reqBody, resBody, latencyMs);

// Export HAR 1.2 Archive
Map<String, Object> har = DevToolsSDK.getNetworkInspector().exportHar();

// Monitor Performance & FPS
double currentFps = DevToolsSDK.getFpsMonitor().getCurrentFps();

// Capture / Export Crash Reports
String crashReport = DevToolsSDK.getCrashReporter().exportCrashReport();
```

## API

| Method / Component | Description |
|--------------------|-------------|
| `NativeDevToolsSDK.getInstance().init(context, config)` | Initialize NativeDevToolsSDK singleton |
| `DevToolsSDK.getDebugDrawer()` | Floating bubble / shake-gesture debug drawer controller |
| `DevToolsSDK.getNetworkInspector()` | Real-time HTTP traffic snooper and HAR exporter |
| `DevToolsSDK.getFpsMonitor()` | Live FPS, frame time, thread count, and heap memory monitor |
| `DevToolsSDK.getCrashReporter()` | Intercepts crashes, captures stack traces, and exports logs |
| `NativeDevToolsService` | Layer 2 domain service extending `BaseService` |
| `DevToolsCommandHandler` | Routes incoming `devtools/*` API requests |
| `DevToolsServer` | Chrome DevTools Protocol (CDP) server and target discovery |
| `DiagnosticsManager` | Bridge invocation metrics and platform manifest collector |
| `PerformanceProfiler` | Timed performance span recorder and CPU metrics tracker |


## Requirements

- Android API 21+
- Java 17+

## Changelog

See [CHANGELOG.md](CHANGELOG.md)

## License

```
Copyright 2026 infoadmstudio2020-cyber

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
