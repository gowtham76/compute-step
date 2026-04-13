package com.socgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class AssemblyExecutor {

    private static final Logger log = LoggerFactory.getLogger(AssemblyExecutor.class);

    public void execute(String type, String assemblyName, Runnable logic) {

        long startTime = System.currentTimeMillis();

        try {
            // ✅ Set MDC directly
            MDC.put("type", type);
            MDC.put("assembly", assemblyName);

            log.info("Assembly started");

            // Execute actual logic
            logic.run();

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "SUCCESS");

            log.info("Assembly completed");

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "FAILED");

            log.error("Assembly failed", e);

            throw e;

        } finally {
            // ✅ Clean only what this class added
            MDC.remove("assembly");
            MDC.remove("duration_ms");
            MDC.remove("status");
            // ❗ DO NOT remove runId/type if set at higher level unless needed
        }
    }
}