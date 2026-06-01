package org.Job.query.controller;

import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;
import org.Job.query.model.response.JobDetailResponse;
import org.Job.query.model.response.JobPageResponse;
import org.Job.query.queries.GetJobDetailQuery;
import org.Job.query.queries.GetJobsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
