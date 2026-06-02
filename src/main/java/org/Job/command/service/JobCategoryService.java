package org.Job.command.service;

import org.Job.command.model.request.CreateJobCategoryRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.concurrent.CompletableFuture;

public interface JobCategoryService {
    CompletableFuture<String> createJobCategory(Jwt jwt, CreateJobCategoryRequest request);
}
