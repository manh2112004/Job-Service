package org.Job.command.service;

import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;

import java.util.concurrent.CompletableFuture;

public interface JobService {
    CompletableFuture<String> createJob(String userId, CreateJobRequest request);

    CompletableFuture<String> updateJob(String userId, String jobId, UpdateJobRequest request);

    CompletableFuture<String> publishJob(String userId, String jobId);
}
