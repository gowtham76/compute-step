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

            // MDC context
            MDC.put("type", type);
            MDC.put("assembly", assemblyName);
            MDC.put("thread", Thread.currentThread().getName());

            log.info(
                    "ASSEMBLY_STARTED runId={} type={} assembly={} thread={}",
                    MDC.get("runId"),
                    type,
                    assemblyName,
                    Thread.currentThread().getName()
            );

            // Execute actual assembly logic
            logic.run();

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "SUCCESS");

            log.info(
                    "ASSEMBLY_COMPLETED runId={} type={} assembly={} duration_ms={} status={} thread={}",
                    MDC.get("runId"),
                    type,
                    assemblyName,
                    duration,
                    "SUCCESS",
                    Thread.currentThread().getName()
            );

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - startTime;

            MDC.put("duration_ms", String.valueOf(duration));
            MDC.put("status", "FAILED");

            log.error(
                    "ASSEMBLY_FAILED runId={} type={} assembly={} duration_ms={} status={} thread={}",
                    MDC.get("runId"),
                    type,
                    assemblyName,
                    duration,
                    "FAILED",
                    Thread.currentThread().getName(),
                    e
            );

            throw e;

        } finally {

            // Cleanup only assembly-level MDC values
            MDC.remove("assembly");
            MDC.remove("duration_ms");
            MDC.remove("status");
            MDC.remove("thread");

            // DO NOT remove runId/type here
            // because parent execution flow may still need them
        }
    }
}