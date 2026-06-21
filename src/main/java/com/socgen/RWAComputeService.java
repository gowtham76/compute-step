package com.socgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class RWAComputeService {

    private static final Logger log =
            LoggerFactory.getLogger(RWAComputeService.class);

    private final AssemblyExecutor executor;

    private final ExecutorService threadPool =
            Executors.newFixedThreadPool(5);

    public RWAComputeService(AssemblyExecutor executor) {
        this.executor = executor;
    }

    public void processType(String runId, String type) {

        long startTime = System.currentTimeMillis();

        int totalAssemblies = 4;

        try {

            MDC.put("event", "TYPE_STARTED");
            MDC.put("runId", runId);
            MDC.put("type", type);
            MDC.put("total_assemblies",
                    String.valueOf(totalAssemblies));

            log.info("TYPE_STARTED");

            Future<?> f1 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S210_updateBalance",
                            () -> simulateWork(400)
                    )
            );

            Future<?> f2 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S320_updateMaturity",
                            () -> simulateWork(1200)
                    )
            );

            Future<?> f3 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S309_updateCounterparty",
                            () -> simulateWork(500)
                    )
            );

            Future<?> f4 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S350_updateCcpTransaction",
                            () -> simulateWork(1600)
                    )
            );

            // Wait for all assemblies
            f1.get();
            f2.get();
            f3.get();
            f4.get();

            long duration =
                    System.currentTimeMillis() - startTime;

            MDC.put("event", "TYPE_COMPLETED");
            MDC.put("duration_ms",
                    String.valueOf(duration));
            MDC.put("status", "SUCCESS");

            log.info("TYPE_COMPLETED");

        } catch (Exception e) {

            long duration =
                    System.currentTimeMillis() - startTime;

            MDC.put("event", "TYPE_FAILED");
            MDC.put("duration_ms",
                    String.valueOf(duration));
            MDC.put("status", "FAILED");

            log.error("TYPE_FAILED", e);

            throw new RuntimeException(e);

        } finally {

            MDC.remove("event");
            MDC.remove("duration_ms");
            MDC.remove("status");
            MDC.remove("total_assemblies");
        }
    }

    private void runWithContext(String runId,
                                String type,
                                String assembly,
                                Runnable task) {

        try {

            MDC.put("runId", runId);
            MDC.put("type", type);

            executor.execute(type, assembly, task);

        } finally {

            MDC.clear();
        }
    }

    private void simulateWork(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            MDC.put("event", "THREAD_INTERRUPTED");
            MDC.put("thread",
                    Thread.currentThread().getName());

            log.error("THREAD_INTERRUPTED", e);
        }
    }
}