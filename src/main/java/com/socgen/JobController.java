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

    public JobController(RWAComputeService rwaComputeService, ECLomputeService ecLomputeService, LTVComputeService ltvComputeService) {
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

            log.info(
                    "RUN_STARTED runId={} runType={} service={}",
                    runId,
                    runType,
                    "compute-step"
            );

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
                    return "❌ Invalid runType";
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "RUN_COMPLETED runId={} runType={} duration_ms={} status=SUCCESS",
                    runId,
                    runType,
                    duration
            );

            return "✅ Job completed. runId=" + runId;

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "RUN_FAILED runId={} runType={} duration_ms={} status=FAILED",
                    runId,
                    runType,
                    duration,
                    e
            );

            throw e;

        } finally {
            MDC.clear();
        }
    }
}