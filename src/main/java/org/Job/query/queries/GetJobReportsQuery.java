package org.Job.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Job.constant.ReportStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetJobReportsQuery {
    private int page;
    private int size;
    private ReportStatus status;  // optional filter
    private String jobId;         // optional filter
}
