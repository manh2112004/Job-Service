package org.Job.command.service;

import org.Job.command.model.request.CreateJobCategoryRequest;
import org.Job.command.model.request.UpdateJobCategoryRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.concurrent.CompletableFuture;

public interface JobCategoryService {
    CompletableFuture<String> createJobCategory(Jwt jwt, CreateJobCategoryRequest request);
    CompletableFuture<String> updateJobCategory(Jwt jwt, String categoryId, UpdateJobCategoryRequest request);
    CompletableFuture<String> deleteJobCategory(Jwt jwt, String categoryId);
}

