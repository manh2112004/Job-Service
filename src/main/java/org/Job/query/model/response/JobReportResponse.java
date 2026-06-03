package org.Job.query.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Job.constant.ReportStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobReportResponse {

    private String id;
    private String jobId;
    private String jobTitle;
    private String reporterId;
    private String reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
