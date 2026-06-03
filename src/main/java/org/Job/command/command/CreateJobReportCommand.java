package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateJobReportCommand {

    @TargetAggregateIdentifier
    private final String jobId;

    private final String reportId;
    private final String reporterId;
    private final String reason;
    private final String description;
}
