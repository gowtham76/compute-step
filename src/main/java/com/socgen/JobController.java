package com.socgen;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class JobController {

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

        // ✅ Generate unique runId
        String runId = UUID.randomUUID().toString();

        try {
            // ✅ Set root context
            MDC.put("runId", runId);
            MDC.put("service", "compute-step");

            switch (runType.toUpperCase()) {

                case "RWA":
                    rwaComputeService.processType(runId, "RWA");
                    break;

                case "ECL":
                    ecLomputeService.processType(runId, "ECL");
                    break;

                case "LTV":
                    ltvComputeService.processType(runId, "LTV");

                default:
                    return "❌ Invalid runType. Allowed: RWA, ECL, LTV, ALL";
            }

            return "✅ Job completed. runId=" + runId;

        } finally {
            MDC.clear(); // cleanup request
        }
    }
}