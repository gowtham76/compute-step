package com.socgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class AssemblyExecutor {

    private static final Logger log =
            LoggerFactory.getLogger(AssemblyExecutor.class);

    public void execute(String type,
                        String assemblyName,
                        Runnable logic) {

        long startTime = System.currentTimeMillis();

        try {

            // Structured MDC fields
            MDC.put("event", "ASSEMBLY_STARTED");
            MDC.put("type", type);
            MDC.put("assembly", assemblyName);
            MDC.put("thread", Thread.currentThread().getName());

            // Clean searchable log
            log.info("ASSEMBLY_STARTED");

            // Execute actual logic
            logic.run();

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("event", "ASSEMBLY_COMPLETED");
            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "SUCCESS");

            log.info("ASSEMBLY_COMPLETED");

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("event", "ASSEMBLY_FAILED");
            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "FAILED");

            log.error("ASSEMBLY_FAILED", e);

            throw e;

        } finally {

            // Cleanup assembly-level MDC
            MDC.remove("event");
            MDC.remove("assembly");
            MDC.remove("duration_ms");
            MDC.remove("status");
            MDC.remove("thread");

            // Keep runId/type controlled by parent flow
        }
    }
}