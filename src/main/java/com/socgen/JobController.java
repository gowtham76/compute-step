package com.socgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class JobController {

    private static final Logger log =
            LoggerFactory.getLogger(JobController.class);

    private final RWAComputeService rwaComputeService;
    private final ECLomputeService ecLomputeService;
    private final LTVComputeService ltvComputeService;

    public JobController(RWAComputeService rwaComputeService,
                         ECLomputeService ecLomputeService,
                         LTVComputeService ltvComputeService) {

        this.rwaComputeService = rwaComputeService;
        this.ecLomputeService = ecLomputeService;
        this.ltvComputeService = ltvComputeService;
    }

    @GetMapping("/run/{runType}")
    public String runJob(@PathVariable String runType) {

        String runId = UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();

        try {

            MDC.put("runId", runId);
            MDC.put("service", "compute-step");
            MDC.put("run_type", runType.toUpperCase());

            MDC.put("event", "RUN_STARTED");

            log.info("RUN_STARTED");

            switch (runType.toUpperCase()) {

                case "RWA":
                    rwaComputeService.processType(runId, "RWA");
                    break;

                case "ECL":
                    ecLomputeService.processType(runId, "ECL");
                    break;

                case "LTV":
                    ltvComputeService.processType(runId, "LTV");
                    break;

                default:

                    MDC.put("event", "RUN_INVALID");

                    log.error("RUN_INVALID");

                    return "❌ Invalid runType";
            }

            long duration =
                    System.currentTimeMillis() - startTime;

            MDC.put("event", "RUN_COMPLETED");
            MDC.put("duration_ms",
                    String.valueOf(duration));
            MDC.put("status", "SUCCESS");

            log.info("RUN_COMPLETED");

            return "✅ Job completed. runId=" + runId;

        } catch (Exception e) {

            long duration =
                    System.currentTimeMillis() - startTime;

            MDC.put("event", "RUN_FAILED");
            MDC.put("duration_ms",
                    String.valueOf(duration));
            MDC.put("status", "FAILED");

            log.error("RUN_FAILED", e);

            throw e;

        } finally {

            MDC.clear();
        }
    }
}