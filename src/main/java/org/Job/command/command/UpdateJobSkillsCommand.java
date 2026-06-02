package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Data
@Builder
public class UpdateJobSkillsCommand {
    @TargetAggregateIdentifier
    private final String jobId;
    private final List<CreateJobCommand.JobSkillCommandInfo> skills;
}
