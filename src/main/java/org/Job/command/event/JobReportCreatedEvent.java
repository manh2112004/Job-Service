package org.Job.command.event;

import lombok.Builder;
import lombok.Data;
import org.Job.constant.ReportStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class JobReportCreatedEvent {

    private final String jobId;
    private final String reportId;
    private final String reporterId;
    private final String reason;
    private final String description;
    private final ReportStatus status;
    private final LocalDateTime createdAt;
}
