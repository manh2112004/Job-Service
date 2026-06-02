package org.Job.command.aggregate;

import org.Job.command.command.CreateJobCategoryCommand;
import org.Job.command.event.JobCategoryCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class JobCategoryAggregate {

    @AggregateIdentifier
    private String id;

    public JobCategoryAggregate() {
    }

    @CommandHandler
    public JobCategoryAggregate(CreateJobCategoryCommand command) {
        AggregateLifecycle.apply(JobCategoryCreatedEvent.builder()
                .id(command.getId())
                .name(command.getName())
                .slug(command.getSlug())
                .description(command.getDescription())
                .active(command.getActive())
                .build());
    }

    @EventSourcingHandler
    public void on(JobCategoryCreatedEvent event) {
        this.id = event.getId();
    }
}
