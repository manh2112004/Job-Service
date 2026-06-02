package org.Job.command.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobBenefitRequest {
    @NotBlank(message = "Tiêu đề phúc lợi không được để trống")
    private String title;

    private String description;
    private String icon;
}
