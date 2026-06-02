package org.Job.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillsUpdatedEvent {
    private String jobId;
    private List<JobCreatedEvent.JobSkillEventInfo> skills;
}
