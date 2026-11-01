package com.roni.library.devtools.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.roni.library.devtools.CrashReporter;
import com.roni.library.devtools.DebugDrawer;
import com.roni.library.devtools.DevToolsSDK;
import com.roni.library.devtools.FpsMonitor;
import com.roni.library.devtools.NativeDevToolsSDK;
import com.roni.library.devtools.NativeDevToolsSDKConfig;
import com.roni.library.devtools.NetworkInspector;
import com.roni.library.devtools.profiler.MemoryMetrics;
import com.roni.library.devtools.profiler.PerformanceSpan;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Activity demonstrating NativeDevToolsSDK usage and interactive inspection.
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTelemetryBadge;
    private TextView tvConsoleOutput;
    private NativeDevToolsSDK sdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTelemetryBadge = findViewById(R.id.tvTelemetryBadge);
        tvConsoleOutput = findViewById(R.id.tvConsoleOutput);

        Button btnToggleDrawer = findViewById(R.id.btnToggleDrawer);
        Button btnSimulateNetwork = findViewById(R.id.btnSimulateNetwork);
        Button btnExportHar = findViewById(R.id.btnExportHar);
        Button btnProfileSpan = findViewById(R.id.btnProfileSpan);
        Button btnDispatchCdp = findViewById(R.id.btnDispatchCdp);
        Button btnSimulateCrash = findViewById(R.id.btnSimulateCrash);

        sdk = NativeDevToolsSDK.getInstance();
        sdk.init(this, NativeDevToolsSDKConfig.builder()
                .debugEnabled(true)
                .tag("DevToolsDemoApp")
                .serverPort(9222)
                .build());

        log("NativeDevToolsSDK initialized on port " + sdk.getConfig().getServerPort());

        sdk.getDebugDrawer().addVisibilityListener(new DebugDrawer.DrawerVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean visible) {
                log("Debug Drawer visibility toggled: " + visible);
            }
        });

        btnToggleDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.getDebugDrawer().toggle();
            }
        });

        btnSimulateNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> reqHeaders = new HashMap<>();
                reqHeaders.put("Accept", "application/json");
                Map<String, String> resHeaders = new HashMap<>();
                resHeaders.put("Content-Type", "application/json");

                sdk.getNetworkInspector().record(
                        "https://api.domain.local/v1/telemetry",
                        "POST",
                        200,
                        reqHeaders,
                        resHeaders,
                        "{\"ping\":true}",
                        "{\"status\":\"pong\",\"latencyMs\":12}",
                        12
                );
                log("Network traffic captured: POST /v1/telemetry (Status: 200, Latency: 12ms)");
                updateTelemetry();
            }
        });

        btnExportHar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> har = sdk.getNetworkInspector().exportHar();
                log("HAR 1.2 Exported. Total entries: " + sdk.getNetworkInspector().getRecords().size());
            }
        });

        btnProfileSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformanceSpan span = sdk.getService().getPerformanceProfiler().startSpan("DatabaseSync", "storage");
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ignored) {}
                span.stop(System.nanoTime() / 1000L);
                sdk.getService().getPerformanceProfiler().recordSpan(span);
                log("Performance span recorded: " + span.getName() + " (Duration: " + span.getDurationUs() + "µs)");
                updateTelemetry();
            }
        });

        btnDispatchCdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> cdpRes = sdk.getService().getDevToolsServer().dispatchCdpMessage(101, "Runtime.evaluate", new HashMap<String, Object>());
                log("CDP message dispatched (id: 101, method: Runtime.evaluate):\n" + cdpRes);
            }
        });

        btnSimulateCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    throw new RuntimeException("Simulated user crash for inspection");
                } catch (Throwable t) {
                    sdk.getCrashReporter().recordCrash(t);
                    log("Crash intercepted & captured: " + t.getMessage());
                    log("Total crash logs: " + sdk.getCrashReporter().getCrashLogs().size());
                }
            }
        });

        updateTelemetry();
    }

    private void updateTelemetry() {
        MemoryMetrics mem = MemoryMetrics.capture();
        long usedMb = mem.getUsedHeapBytes() / (1024 * 1024);
        double fps = sdk.getFpsMonitor().getCurrentFps();
        int threads = sdk.getFpsMonitor().getThreadCount();

        tvTelemetryBadge.setText("Status: Active | Port: " + sdk.getConfig().getServerPort() +
                " | FPS: " + fps +
                " | Memory: " + usedMb + "MB | Threads: " + threads);
    }

    private void log(String message) {
        String current = tvConsoleOutput.getText().toString();
        tvConsoleOutput.setText(current + "\n> " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdk.release();
    }
}

