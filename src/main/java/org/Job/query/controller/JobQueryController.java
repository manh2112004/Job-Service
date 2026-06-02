package org.Job.query.controller;

import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;
import org.Job.query.model.response.JobDetailResponse;
import org.Job.query.model.response.JobListResponse;
import org.Job.query.model.response.JobPageResponse;
import org.Job.query.model.response.JobResponse;
import org.Job.query.queries.GetJobDetailQuery;
import org.Job.query.queries.GetJobsQuery;
import org.Job.query.queries.GetSimilarJobsQuery;
import org.Job.query.queries.GetLatestJobsQuery;
import org.Job.query.queries.GetJobSkillsQuery;
import org.Job.query.model.response.JobSkillResponse;
import org.Job.query.model.response.JobSkillListResponse;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/{jobId}")
    public CompletableFuture<JobDetailResponse> getJobById(@PathVariable String jobId) {
        return queryGateway.query(
                new GetJobDetailQuery(jobId),
                ResponseTypes.instanceOf(JobDetailResponse.class)
        );
    }

    @GetMapping
    public CompletableFuture<JobPageResponse> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) WorkingType workingType,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) JobLevel level,
            @RequestParam(required = false) String companyId
    ) {
        GetJobsQuery query = new GetJobsQuery(page, size, keyword, location, workingType, employmentType, level, companyId);
        return queryGateway.query(
                query,
                ResponseTypes.instanceOf(JobPageResponse.class)
        );
    }

    @GetMapping("/{jobId}/similar")
    public CompletableFuture<List<JobResponse>> getSimilarJobs(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EmploymentType employmentType
    ) {
        return queryGateway.query(
                new GetSimilarJobsQuery(jobId, size, categoryId, skills, companyId, location, employmentType),
                ResponseTypes.instanceOf(JobListResponse.class)
        ).thenApply(JobListResponse::getJobs);
    }

    @GetMapping("/latest")
    public CompletableFuture<List<JobResponse>> getLatestJobs(
            @RequestParam(defaultValue = "10") int size
    ) {
        return queryGateway.query(
                new GetLatestJobsQuery(size),
                ResponseTypes.instanceOf(JobListResponse.class)
        ).thenApply(JobListResponse::getJobs);
    }

    @GetMapping("/{jobId}/skills")
    public CompletableFuture<List<JobSkillResponse>> getJobSkills(@PathVariable String jobId) {
        return queryGateway.query(
                new GetJobSkillsQuery(jobId),
                ResponseTypes.instanceOf(JobSkillListResponse.class)
        ).thenApply(JobSkillListResponse::getSkills);
    }
}
