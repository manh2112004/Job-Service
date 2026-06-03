package org.Job.query.model.response;

import java.util.List;

public class JobReportPageResponse extends PageResponse<JobReportResponse> {
    public JobReportPageResponse(List<JobReportResponse> content, int page, int size,
                                  long totalElements, int totalPages) {
        super(content, page, size, totalElements, totalPages);
    }
}
