package org.Job.command.controller;

import jakarta.validation.Valid;
import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobCommandController {

    @Autowired
    private JobService jobService;

    @PostMapping
    public CompletableFuture<String> createJob(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateJobRequest request
    ) {
        return jobService.createJob(jwt.getSubject(), request);
    }

    @PutMapping("/{jobId}")
    public CompletableFuture<String> updateJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobRequest request
    ) {
        return jobService.updateJob(jwt.getSubject(), jobId, request);
    }

    @PutMapping("/{jobId}/publish")
    public CompletableFuture<String> publishJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.publishJob(jwt.getSubject(), jobId);
    }
}
