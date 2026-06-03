package org.Job.command.controller;

import jakarta.validation.Valid;
import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.model.request.UpdateJobSkillsRequest;
import org.Job.command.model.request.UpdateJobSkillRequest;
import org.Job.command.model.request.CreateJobSkillRequest;
import org.Job.command.model.request.CreateJobBenefitRequest;
import org.Job.command.model.request.UpdateJobBenefitRequest;
import org.Job.command.model.request.CreateJobReportRequest;
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

    @PutMapping("/{jobId}/reopen")
    public CompletableFuture<String> reopenJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.reopenJob(jwt.getSubject(), jobId);
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

    @DeleteMapping("/skills/{skillId}")
    public CompletableFuture<String> deleteJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String skillId
    ) {
        return jobService.deleteJobSkill(jwt.getSubject(), skillId);
    }

    @PostMapping("/{jobId}/skills")
    public CompletableFuture<String> addJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobSkillRequest request
    ) {
        return jobService.addJobSkill(jwt.getSubject(), jobId, request);
    }

    @PostMapping("/{jobId}/benefits")
    public CompletableFuture<String> addJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobBenefitRequest request
    ) {
        return jobService.addJobBenefit(jwt.getSubject(), jobId, request);
    }

    @PutMapping("/benefits/{benefitId}")
    public CompletableFuture<String> updateSingleJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String benefitId,
            @Valid @RequestBody UpdateJobBenefitRequest request
    ) {
        return jobService.updateSingleJobBenefit(jwt.getSubject(), benefitId, request);
    }

    @DeleteMapping("/benefits/{benefitId}")
    public CompletableFuture<String> deleteJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String benefitId
    ) {
        return jobService.deleteJobBenefit(jwt.getSubject(), benefitId);
    }

    @PostMapping("/{jobId}/reports")
    public CompletableFuture<String> createJobReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobReportRequest request
    ) {
        return jobService.createJobReport(jwt.getSubject(), jobId, request);
    }
}

