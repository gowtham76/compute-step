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

            log.info(
                    "TYPE_STARTED runId={} type={} totalAssemblies={}",
                    runId,
                    type,
                    totalAssemblies
            );

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
                            () -> simulateWork(800)
                    )
            );

            Future<?> f3 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S309_updateCounterparty",
                            () -> simulateWork(300)
                    )
            );

            Future<?> f4 = threadPool.submit(() ->
                    runWithContext(
                            runId,
                            type,
                            "S350_updateCcpTransaction",
                            () -> simulateWork(600)
                    )
            );

            // Wait for all assemblies
            f1.get();
            f2.get();
            f3.get();
            f4.get();

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "TYPE_COMPLETED runId={} type={} duration_ms={} status=SUCCESS totalAssemblies={}",
                    runId,
                    type,
                    duration,
                    totalAssemblies
            );

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "TYPE_FAILED runId={} type={} duration_ms={} status=FAILED",
                    runId,
                    type,
                    duration,
                    e
            );

            throw new RuntimeException(e);
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

            log.error(
                    "THREAD_INTERRUPTED thread={}",
                    Thread.currentThread().getName(),
                    e
            );
        }
    }
}