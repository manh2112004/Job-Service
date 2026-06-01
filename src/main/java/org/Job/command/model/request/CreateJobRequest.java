package org.Job.command.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    @NotBlank(message = "Tên công việc không được để trống")
    private String title;

    @NotBlank(message = "Mô tả công việc không được để trống")
    private String description;

    private String responsibilities;

    @NotBlank(message = "Yêu cầu công việc (who you are) không được để trống")
    private String whoYouAre;

    private String niceToHaves;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    @NotNull(message = "Hình thức làm việc không được để trống")
    private WorkingType workingType;

    @NotNull(message = "Loại hợp đồng không được để trống")
    private EmploymentType employmentType;

    @NotNull(message = "Cấp bậc không được để trống")
    private JobLevel level;

    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
    private Integer capacity;

    @NotNull(message = "Hạn nộp hồ sơ không được để trống")
    private LocalDate deadline;

    @NotBlank(message = "Mã công ty không được để trống")
    private String companyId;

    private Boolean featured;
    private Boolean urgent;

    @Valid
    private List<JobSkillRequest> skills;

    @Valid
    private List<JobBenefitRequest> benefits;

    private List<String> categoryIds;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobSkillRequest {
        @NotBlank(message = "Tên kỹ năng không được để trống")
        private String skillName;

        @NotNull(message = "Trường bắt buộc không được để trống")
        private Boolean required;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobBenefitRequest {
        @NotBlank(message = "Tiêu đề phúc lợi không được để trống")
        private String title;

        private String description;
        private String icon;
    }
}
