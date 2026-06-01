package org.Job.query.controller;

import org.Job.query.model.response.JobDetailResponse;
import org.Job.query.queries.GetJobDetailQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
