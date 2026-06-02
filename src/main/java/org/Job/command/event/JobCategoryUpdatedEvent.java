package org.Job.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryUpdatedEvent {
    private String id;
    private String name;
    private String slug;
    private String description;
    private Boolean active;
}
