package com.socgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class LTVComputeService {

    private static final Logger log = LoggerFactory.getLogger(RWAComputeService.class);

    private final AssemblyExecutor executor;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public LTVComputeService(AssemblyExecutor executor) {
        this.executor = executor;
    }

    public void processType(String runId, String type) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("Type processing started runId={} type={}", runId, type);

            Future<?> f1 = threadPool.submit(() ->
                    runWithContext(runId, type, "S610_updateBalance", () -> simulateWork(400))
            );

            Future<?> f2 = threadPool.submit(() ->
                    runWithContext(runId, type, "S620_updateMaturity", () -> simulateWork(800))
            );

            Future<?> f3 = threadPool.submit(() ->
                    runWithContext(runId, type, "S609_updateCounterparty", () -> simulateWork(300))
            );

            Future<?> f4 = threadPool.submit(() ->
                    runWithContext(runId, type, "S650_updateCcpTransaction", () -> simulateWork(600))
            );

            f1.get();
            f2.get();
            f3.get();
            f4.get();

            long duration = System.currentTimeMillis() - startTime;

            log.info("Type processing completed runId={} duration_ms={}", runId, duration);

        } catch (Exception e) {
            log.error("Type processing failed runId={}", runId, e);
            throw new RuntimeException(e);
        }
    }


    private void runWithContext(String runId, String type, String assembly, Runnable task) {

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
        }
    }
}