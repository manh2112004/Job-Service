package org.Job.command.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobCategoryCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String slug;
    private String description;
    private Boolean active;
}
