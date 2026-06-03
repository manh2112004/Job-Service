package org.Job.command.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateJobReportRequest {

    @NotBlank(message = "Lý do báo cáo không được để trống")
    @Size(max = 255, message = "Lý do báo cáo không được vượt quá 255 ký tự")
    private String reason;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;
}
