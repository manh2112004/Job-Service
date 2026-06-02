package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateJobBenefitCommand {
    @TargetAggregateIdentifier
    private final String jobId;
    private final String benefitId;
    private final String title;
    private final String description;
    private final String icon;
}
