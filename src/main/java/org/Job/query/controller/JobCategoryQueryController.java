package org.Job.query.controller;

import org.Job.query.model.response.JobCategoryListResponse;
import org.Job.query.model.response.JobCategoryResponse;
import org.Job.query.queries.GetJobCategoriesQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/job-categories")
public class JobCategoryQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    public CompletableFuture<List<JobCategoryResponse>> getJobCategories() {
        return queryGateway.query(
                new GetJobCategoriesQuery(),
                ResponseTypes.instanceOf(JobCategoryListResponse.class)
        ).thenApply(JobCategoryListResponse::getCategories);
    }
}
