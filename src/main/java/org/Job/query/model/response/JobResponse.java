package org.Job.query.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private String id;
    private String title;
    private CompanyDto company;
    private String location;
    private String workingType;
    private String employmentType;
    private String level;
    private SalaryDto salary;
    private LocalDate deadline;
    private LocalDateTime postedAt;
    private Boolean urgent;
    private Boolean featured;
    private List<String> skills;
    private List<String> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyDto {
        private String id;
        private String companyName;
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalaryDto {
        private BigDecimal minSalary;
        private BigDecimal maxSalary;
        private String currency;
    }
}
