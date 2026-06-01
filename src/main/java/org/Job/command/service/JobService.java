package org.Job.command.service;

import org.Job.command.model.request.CreateJobRequest;

import java.util.concurrent.CompletableFuture;

public interface JobService {
    CompletableFuture<String> createJob(String userId, CreateJobRequest request);
}
