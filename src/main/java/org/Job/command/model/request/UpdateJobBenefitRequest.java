package org.Job.command.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobBenefitRequest {
    private String title;

    private String description;
    private String icon;
}
