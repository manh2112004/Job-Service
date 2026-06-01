package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.JobStatus;
import org.Job.constant.WorkingType;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateJobCommand {
    @TargetAggregateIdentifier
    private final String jobId;
    private final String title;
    private final String description;
    private final String responsibilities;
    private final String whoYouAre;
    private final String niceToHaves;
    private final String location;
    private final WorkingType workingType;
    private final EmploymentType employmentType;
    private final JobLevel level;
    private final BigDecimal minSalary;
    private final BigDecimal maxSalary;
    private final String currency;
    private final Integer capacity;
    private final LocalDate deadline;
    private final JobStatus status;
    private final Boolean featured;
    private final Boolean urgent;

    private final List<CreateJobCommand.JobSkillCommandInfo> skills;
    private final List<CreateJobCommand.JobBenefitCommandInfo> benefits;
    private final List<String> categoryIds;
}
