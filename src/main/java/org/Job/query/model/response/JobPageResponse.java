package org.Job.query.model.response;

import java.util.List;

public class JobPageResponse extends PageResponse<JobResponse> {
    public JobPageResponse(List<JobResponse> content, int page, int size, long totalElements, int totalPages) {
        super(content, page, size, totalElements, totalPages);
    }
}
