package org.Job.command.aggregate;

import org.Job.command.command.CreateJobCommand;
import org.Job.command.command.UpdateJobCommand;
import org.Job.command.event.JobCreatedEvent;
import org.Job.command.event.JobUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.stream.Collectors;

@Aggregate
public class JobAggregate {

    @AggregateIdentifier
    private String jobId;

    private String companyId;

    public JobAggregate() {
    }

    @CommandHandler
    public JobAggregate(CreateJobCommand command) {
        AggregateLifecycle.apply(JobCreatedEvent.builder()
                .jobId(command.getJobId())
                .companyId(command.getCompanyId())
                .recruiterId(command.getRecruiterId())
                .title(command.getTitle())
                .description(command.getDescription())
                .responsibilities(command.getResponsibilities())
                .whoYouAre(command.getWhoYouAre())
                .niceToHaves(command.getNiceToHaves())
                .location(command.getLocation())
                .workingType(command.getWorkingType())
                .employmentType(command.getEmploymentType())
                .level(command.getLevel())
                .minSalary(command.getMinSalary())
                .maxSalary(command.getMaxSalary())
                .currency(command.getCurrency())
                .capacity(command.getCapacity())
                .deadline(command.getDeadline())
                .featured(command.getFeatured())
                .urgent(command.getUrgent())
                .skills(command.getSkills() == null ? null : command.getSkills().stream()
                        .map(s -> JobCreatedEvent.JobSkillEventInfo.builder()
                                .skillName(s.getSkillName())
                                .required(s.getRequired())
                                .build())
                        .collect(Collectors.toList()))
                .benefits(command.getBenefits() == null ? null : command.getBenefits().stream()
                        .map(b -> JobCreatedEvent.JobBenefitEventInfo.builder()
                                .title(b.getTitle())
                                .description(b.getDescription())
                                .icon(b.getIcon())
                                .build())
                        .collect(Collectors.toList()))
                .categoryIds(command.getCategoryIds())
                .build());
    }

    @EventSourcingHandler
    public void on(JobCreatedEvent event) {
        this.jobId = event.getJobId();
        this.companyId = event.getCompanyId();
    }

    @CommandHandler
    public String handle(UpdateJobCommand command) {
        AggregateLifecycle.apply(JobUpdatedEvent.builder()
                .jobId(command.getJobId())
                .title(command.getTitle())
                .description(command.getDescription())
                .responsibilities(command.getResponsibilities())
                .whoYouAre(command.getWhoYouAre())
                .niceToHaves(command.getNiceToHaves())
                .location(command.getLocation())
                .workingType(command.getWorkingType())
                .employmentType(command.getEmploymentType())
                .level(command.getLevel())
                .minSalary(command.getMinSalary())
                .maxSalary(command.getMaxSalary())
                .currency(command.getCurrency())
                .capacity(command.getCapacity())
                .deadline(command.getDeadline())
                .status(command.getStatus())
                .featured(command.getFeatured())
                .urgent(command.getUrgent())
                .skills(command.getSkills() == null ? null : command.getSkills().stream()
                        .map(s -> JobCreatedEvent.JobSkillEventInfo.builder()
                                .skillName(s.getSkillName())
                                .required(s.getRequired())
                                .build())
                        .collect(Collectors.toList()))
                .benefits(command.getBenefits() == null ? null : command.getBenefits().stream()
                        .map(b -> JobCreatedEvent.JobBenefitEventInfo.builder()
                                .title(b.getTitle())
                                .description(b.getDescription())
                                .icon(b.getIcon())
                                .build())
                        .collect(Collectors.toList()))
                .categoryIds(command.getCategoryIds())
                .build());
        return "Cập nhật công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobUpdatedEvent event) {
        this.jobId = event.getJobId();
    }
}

