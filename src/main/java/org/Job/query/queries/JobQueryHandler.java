package org.Job.query.queries;

import org.Job.client.CompanyClient;
import org.Job.client.dto.CompanyResponse;
import org.Job.command.data.*;
import org.Job.query.model.response.JobDetailResponse;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobQueryHandler {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private JobBenefitRepository jobBenefitRepository;

    @Autowired
    private JobCategoryMappingRepository jobCategoryMappingRepository;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @Autowired
    private CompanyClient companyClient;

    @QueryHandler
    @Transactional(readOnly = true)
    public JobDetailResponse handle(GetJobDetailQuery query) {
        Job job = jobRepository.findById(query.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        CompanyResponse company = companyClient.getCompany(job.getCompanyId());

        JobDetailResponse.CompanyDto companyDto = null;
        String companySummary = null;
        if (company != null) {
            companyDto = JobDetailResponse.CompanyDto.builder()
                    .id(company.getId())
                    .companyName(company.getCompanyName())
                    .logoUrl(company.getLogoUrl())
                    .build();
            companySummary = company.getDescription();
        } else {
            companyDto = JobDetailResponse.CompanyDto.builder()
                    .id(job.getCompanyId())
                    .companyName("N/A")
                    .logoUrl(null)
                    .build();
        }

        List<JobDetailResponse.SkillDto> skills = jobSkillRepository.findAllByJobId(job.getId())
                .stream()
                .map(s -> JobDetailResponse.SkillDto.builder()
                        .id(s.getId())
                        .skillName(s.getSkillName())
                        .required(s.getRequired())
                        .build())
                .collect(Collectors.toList());

        List<JobDetailResponse.BenefitDto> benefits = jobBenefitRepository.findAllByJobId(job.getId())
                .stream()
                .map(b -> JobDetailResponse.BenefitDto.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .description(b.getDescription())
                        .icon(b.getIcon())
                        .build())
                .collect(Collectors.toList());

        List<JobDetailResponse.CategoryDto> categories = jobCategoryMappingRepository.findAllByJobId(job.getId())
                .stream()
                .map(m -> jobCategoryRepository.findById(m.getCategoryId()).orElse(null))
                .filter(c -> c != null)
                .map(c -> JobDetailResponse.CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toList());

        return JobDetailResponse.builder()
                .title(job.getTitle())
                .company(companyDto)
                .location(job.getLocation())
                .jobType(job.getEmploymentType() != null ? job.getEmploymentType().name() : null)
                .salary(JobDetailResponse.SalaryDto.builder()
                        .minSalary(job.getMinSalary())
                        .maxSalary(job.getMaxSalary())
                        .currency(job.getCurrency())
                        .build())
                .deadline(job.getDeadline())
                .postedAt(job.getPublishedAt())
                .capacity(job.getCapacity())
                .applicationCount(job.getApplicationCount())
                .description(job.getDescription())
                .responsibilities(job.getResponsibilities())
                .whoYouAre(job.getWhoYouAre())
                .niceToHaves(job.getNiceToHaves())
                .skills(skills)
                .benefits(benefits)
                .categories(categories)
                .companySummary(companySummary)
                .build();
    }
}
