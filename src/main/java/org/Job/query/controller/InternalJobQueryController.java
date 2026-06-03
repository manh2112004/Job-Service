package org.Job.query.controller;

import org.Job.query.queries.GetJobDetailQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/internal/jobs")
public class InternalJobQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/{jobId}/exists")
    public CompletableFuture<Boolean> checkJobExists(@PathVariable String jobId) {
        return queryGateway.query(
                new GetJobDetailQuery(jobId),
                ResponseTypes.instanceOf(org.Job.query.model.response.JobDetailResponse.class)
        ).thenApply(res -> true).exceptionally(err -> false);
    }

    @GetMapping("/{jobId}")
    public CompletableFuture<Map<String, String>> getInternalJob(@PathVariable String jobId) {
        return queryGateway.query(
                new GetJobDetailQuery(jobId),
                ResponseTypes.instanceOf(org.Job.query.model.response.JobDetailResponse.class)
        ).thenApply(res -> Map.of(
                "id", jobId,
                "companyId", res.getCompany() != null ? res.getCompany().getId() : ""
        )).exceptionally(err -> Map.of());
    }
}
