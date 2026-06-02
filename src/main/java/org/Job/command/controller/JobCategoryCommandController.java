package org.Job.command.controller;

import jakarta.validation.Valid;
import org.Job.command.model.request.CreateJobCategoryRequest;
import org.Job.command.service.JobCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/admin/job-categories")
public class JobCategoryCommandController {

    @Autowired
    private JobCategoryService jobCategoryService;

    @PostMapping
    public CompletableFuture<String> createJobCategory(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateJobCategoryRequest request
    ) {
        return jobCategoryService.createJobCategory(jwt, request);
    }
}
