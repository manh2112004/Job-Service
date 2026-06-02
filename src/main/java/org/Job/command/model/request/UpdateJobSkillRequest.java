package org.Job.command.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobSkillRequest {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String skillName;

    @NotNull(message = "Trường bắt buộc không được để trống")
    private Boolean required;
}
