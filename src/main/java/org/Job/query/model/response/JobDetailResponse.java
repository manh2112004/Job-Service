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
public class JobDetailResponse {
    private String title;
    private CompanyDto company;
    private String location;
    private String jobType;
    private SalaryDto salary;
    private LocalDate deadline;
    private LocalDateTime postedAt;
    private Integer capacity;
    private Integer applicationCount;
    private String description;
    private String responsibilities;
    private String whoYouAre;
    private String niceToHaves;
    private List<CategoryDto> categories;
    private List<SkillDto> skills;
    private List<BenefitDto> benefits;
    private String companySummary;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private String id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDto {
        private String id;
        private String skillName;
        private Boolean required;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BenefitDto {
        private String id;
        private String title;
        private String description;
        private String icon;
    }
}
