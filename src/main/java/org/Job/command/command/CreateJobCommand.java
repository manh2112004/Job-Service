package org.Job.command.command;

import lombok.Builder;
import lombok.Data;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CreateJobCommand {
    @TargetAggregateIdentifier
    private final String jobId;
    private final String companyId;
    private final String recruiterId;
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
    private final Boolean featured;
    private final Boolean urgent;

    private final List<JobSkillCommandInfo> skills;
    private final List<JobBenefitCommandInfo> benefits;
    private final List<String> categoryIds;

    @Data
    @Builder
    public static class JobSkillCommandInfo {
        private final String skillName;
        private final Boolean required;
    }

    @Data
    @Builder
    public static class JobBenefitCommandInfo {
        private final String title;
        private final String description;
        private final String icon;
    }
}
