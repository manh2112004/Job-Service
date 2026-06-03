package org.Job.command.aggregate;

import org.Job.command.command.SaveJobCommand;
import org.Job.command.command.UnsaveJobCommand;
import org.Job.command.event.JobSavedEvent;
import org.Job.command.event.JobUnsavedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class SavedJobAggregate {

    @AggregateIdentifier
    private String id;
    private String candidateId;
    private String jobId;

    public SavedJobAggregate() {
    }

    @CommandHandler
    public SavedJobAggregate(SaveJobCommand command) {
        AggregateLifecycle.apply(JobSavedEvent.builder()
                .id(command.getId())
                .candidateId(command.getCandidateId())
                .jobId(command.getJobId())
                .build());
    }

    @EventSourcingHandler
    public void on(JobSavedEvent event) {
        this.id = event.getId();
        this.candidateId = event.getCandidateId();
        this.jobId = event.getJobId();
    }

    @CommandHandler
    public String handle(UnsaveJobCommand command) {
        AggregateLifecycle.apply(JobUnsavedEvent.builder()
                .id(command.getId())
                .candidateId(command.getCandidateId())
                .jobId(command.getJobId())
                .build());
        return "Bỏ lưu công việc thành công";
    }

    @EventSourcingHandler
    public void on(JobUnsavedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
