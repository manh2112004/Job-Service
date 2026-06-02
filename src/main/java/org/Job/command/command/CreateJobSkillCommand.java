package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateJobSkillCommand {
    @TargetAggregateIdentifier
    private final String jobId;
    private final String skillId;
    private final String skillName;
    private final Boolean required;
}
