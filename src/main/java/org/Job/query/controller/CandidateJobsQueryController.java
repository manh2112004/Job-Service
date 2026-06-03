package org.Job.query.controller;

import org.Job.query.model.response.JobPageResponse;
import org.Job.query.queries.GetSavedJobsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateJobsQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/{candidateId}/saved-jobs")
    public CompletableFuture<JobPageResponse> getSavedJobs(
            @PathVariable String candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetSavedJobsQuery query = new GetSavedJobsQuery(candidateId, page, size);
        return queryGateway.query(
                query,
                ResponseTypes.instanceOf(JobPageResponse.class)
        );
    }
}
