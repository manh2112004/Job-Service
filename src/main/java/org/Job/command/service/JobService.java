package org.Job.command.service;

import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.model.request.UpdateJobSkillsRequest;
import org.Job.command.model.request.UpdateJobSkillRequest;

import java.util.concurrent.CompletableFuture;

public interface JobService {
    CompletableFuture<String> createJob(String userId, CreateJobRequest request);

    CompletableFuture<String> updateJob(String userId, String jobId, UpdateJobRequest request);

    CompletableFuture<String> publishJob(String userId, String jobId);

    CompletableFuture<String> deleteJob(String userId, String jobId);

    CompletableFuture<String> closeJob(String userId, String jobId);

    CompletableFuture<String> updateJobSkills(String userId, String jobId, UpdateJobSkillsRequest request);

    CompletableFuture<String> updateSingleJobSkill(String userId, String skillId, UpdateJobSkillRequest request);
}


