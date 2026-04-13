package com.socgen;

import com.socgen.ComputeService;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class JobController {

    private final ComputeService computeService;

    public JobController(ComputeService computeService) {
        this.computeService = computeService;
    }

    @GetMapping("/run")
    public String runJob() {

        // ✅ Generate unique runId
        String runId = UUID.randomUUID().toString();

        try {
            // ✅ Set root context
            MDC.put("runId", runId);
            MDC.put("service", "compute-step");

            computeService.processType(runId, "RWA");
            computeService.processType(runId, "ECL");
            computeService.processType(runId, "LTV");

            return "✅ Job completed. runId=" + runId;

        } finally {
            MDC.clear(); // cleanup request
        }
    }
}