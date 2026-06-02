package org.Job.command.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobSkillsRequest {
    @Valid
    @NotNull(message = "Danh sách kỹ năng không được để trống")
    private List<CreateJobRequest.JobSkillRequest> skills;
}
