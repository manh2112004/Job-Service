package org.Job.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobBenefitCreatedEvent {
    private String jobId;
    private String benefitId;
    private String title;
    private String description;
    private String icon;
}
