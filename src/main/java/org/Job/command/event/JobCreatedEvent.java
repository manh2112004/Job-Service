package org.Job.command.event;

import lombok.*;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCreatedEvent {
    private String jobId;
    private String companyId;
    private String recruiterId;
    private String title;
    private String description;
    private String responsibilities;
    private String whoYouAre;
    private String niceToHaves;
    private String location;
    private WorkingType workingType;
    private EmploymentType employmentType;
    private JobLevel level;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
    private Integer capacity;
    private LocalDate deadline;
    private Boolean featured;
    private Boolean urgent;

    private List<JobSkillEventInfo> skills;
    private List<JobBenefitEventInfo> benefits;
    private List<String> categoryIds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobSkillEventInfo {
        private String skillName;
        private Boolean required;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobBenefitEventInfo {
        private String title;
        private String description;
        private String icon;
    }
}
