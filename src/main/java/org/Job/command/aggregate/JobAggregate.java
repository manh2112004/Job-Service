package org.Job.command.aggregate;

import org.Job.command.command.CreateJobCommand;
import org.Job.command.command.UpdateJobCommand;
import org.Job.command.command.PublishJobCommand;
import org.Job.command.command.ApproveJobCommand;
import org.Job.command.command.RejectJobCommand;
import org.Job.command.command.BlockJobCommand;
import org.Job.command.command.UnblockJobCommand;
import org.Job.command.command.DeleteJobCommand;
import org.Job.command.command.CloseJobCommand;
import org.Job.command.command.UpdateJobSkillsCommand;
import org.Job.command.command.CreateJobSkillCommand;
import org.Job.command.command.UpdateSingleJobSkillCommand;
import org.Job.command.command.CreateJobBenefitCommand;
import org.Job.command.command.UpdateSingleJobBenefitCommand;
import org.Job.command.command.DeleteJobBenefitCommand;
import org.Job.command.event.JobCreatedEvent;
import org.Job.command.event.JobSkillCreatedEvent;
import org.Job.command.event.JobSkillsUpdatedEvent;
import org.Job.command.event.SingleJobSkillUpdatedEvent;
import org.Job.command.event.JobBenefitCreatedEvent;
import org.Job.command.event.SingleJobBenefitUpdatedEvent;
import org.Job.command.event.JobBenefitDeletedEvent;
import org.Job.command.event.JobUpdatedEvent;
import org.Job.command.event.JobPublishedEvent;
import org.Job.command.event.JobApprovedEvent;
import org.Job.command.event.JobRejectedEvent;
import org.Job.command.event.JobBlockedEvent;
import org.Job.command.event.JobUnblockedEvent;
import org.Job.command.event.JobDeletedEvent;
import org.Job.command.event.JobClosedEvent;
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

    @CommandHandler
    public String handle(PublishJobCommand command) {
        AggregateLifecycle.apply(JobPublishedEvent.builder()
                .jobId(command.getJobId())
                .publishedAt(java.time.LocalDateTime.now())
                .build());
        return "Xuất bản công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobPublishedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(ApproveJobCommand command) {
        AggregateLifecycle.apply(JobApprovedEvent.builder()
                .jobId(command.getJobId())
                .approvedAt(java.time.LocalDateTime.now())
                .build());
        return "Duyệt công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobApprovedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(RejectJobCommand command) {
        AggregateLifecycle.apply(JobRejectedEvent.builder()
                .jobId(command.getJobId())
                .rejectedAt(java.time.LocalDateTime.now())
                .build());
        return "Từ chối công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobRejectedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(BlockJobCommand command) {
        AggregateLifecycle.apply(JobBlockedEvent.builder()
                .jobId(command.getJobId())
                .blockedAt(java.time.LocalDateTime.now())
                .build());
        return "Khóa công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobBlockedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(UnblockJobCommand command) {
        AggregateLifecycle.apply(JobUnblockedEvent.builder()
                .jobId(command.getJobId())
                .unblockedAt(java.time.LocalDateTime.now())
                .build());
        return "Mở khóa công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobUnblockedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(DeleteJobCommand command) {
        AggregateLifecycle.apply(JobDeletedEvent.builder()
                .jobId(command.getJobId())
                .build());
        return "Xóa công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }

    @CommandHandler
    public String handle(CloseJobCommand command) {
        AggregateLifecycle.apply(JobClosedEvent.builder()
                .jobId(command.getJobId())
                .build());
        return "Đóng công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobClosedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(UpdateJobSkillsCommand command) {
        AggregateLifecycle.apply(JobSkillsUpdatedEvent.builder()
                .jobId(command.getJobId())
                .skills(command.getSkills() == null ? null : command.getSkills().stream()
                        .map(s -> JobCreatedEvent.JobSkillEventInfo.builder()
                                .skillName(s.getSkillName())
                                .required(s.getRequired())
                                .build())
                        .collect(Collectors.toList()))
                .build());
        return "Cập nhật kỹ năng công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobSkillsUpdatedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(UpdateSingleJobSkillCommand command) {
        AggregateLifecycle.apply(SingleJobSkillUpdatedEvent.builder()
                .jobId(command.getJobId())
                .skillId(command.getSkillId())
                .skillName(command.getSkillName())
                .required(command.getRequired())
                .build());
        return "Cập nhật kỹ năng công việc thành công";
    }

    @EventSourcingHandler
    public void on(SingleJobSkillUpdatedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(CreateJobSkillCommand command) {
        AggregateLifecycle.apply(JobSkillCreatedEvent.builder()
                .jobId(command.getJobId())
                .skillId(command.getSkillId())
                .skillName(command.getSkillName())
                .required(command.getRequired())
                .build());
        return command.getSkillId();
    }

    @EventSourcingHandler
    public void on(JobSkillCreatedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(CreateJobBenefitCommand command) {
        AggregateLifecycle.apply(JobBenefitCreatedEvent.builder()
                .jobId(command.getJobId())
                .benefitId(command.getBenefitId())
                .title(command.getTitle())
                .description(command.getDescription())
                .icon(command.getIcon())
                .build());
        return command.getBenefitId();
    }

    @EventSourcingHandler
    public void on(JobBenefitCreatedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(UpdateSingleJobBenefitCommand command) {
        AggregateLifecycle.apply(SingleJobBenefitUpdatedEvent.builder()
                .jobId(command.getJobId())
                .benefitId(command.getBenefitId())
                .title(command.getTitle())
                .description(command.getDescription())
                .icon(command.getIcon())
                .build());
        return "Cập nhật phúc lợi thành công";
    }

    @EventSourcingHandler
    public void on(SingleJobBenefitUpdatedEvent event) {
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(DeleteJobBenefitCommand command) {
        AggregateLifecycle.apply(JobBenefitDeletedEvent.builder()
                .jobId(command.getJobId())
                .benefitId(command.getBenefitId())
                .build());
        return "Xóa phúc lợi thành công";
    }

    @EventSourcingHandler
    public void on(JobBenefitDeletedEvent event) {
        this.jobId = event.getJobId();
    }
}

