package org.Job.command.controller;

import jakarta.validation.Valid;
import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.model.request.UpdateJobSkillsRequest;
import org.Job.command.model.request.UpdateJobSkillRequest;
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

    @DeleteMapping("/{jobId}")
    public CompletableFuture<String> deleteJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.deleteJob(jwt.getSubject(), jobId);
    }

    @PutMapping("/{jobId}/close")
    public CompletableFuture<String> closeJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.closeJob(jwt.getSubject(), jobId);
    }

    @PutMapping("/{jobId}/skills")
    public CompletableFuture<String> updateJobSkills(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobSkillsRequest request
    ) {
        return jobService.updateJobSkills(jwt.getSubject(), jobId, request);
    }

    @PutMapping("/skills/{skillId}")
    public CompletableFuture<String> updateSingleJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String skillId,
            @Valid @RequestBody UpdateJobSkillRequest request
    ) {
        return jobService.updateSingleJobSkill(jwt.getSubject(), skillId, request);
    }
}

