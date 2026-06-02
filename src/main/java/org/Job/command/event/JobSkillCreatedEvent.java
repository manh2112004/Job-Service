package org.Job.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillCreatedEvent {
    private String jobId;
    private String skillId;
    private String skillName;
    private Boolean required;
}
