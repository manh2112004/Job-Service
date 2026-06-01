package org.Job.query.queries;

import org.Job.client.CompanyClient;
import org.Job.client.dto.CompanyResponse;
import org.Job.command.data.*;
import org.Job.constant.JobStatus;
import org.Job.query.model.response.JobDetailResponse;
import org.Job.query.model.response.JobPageResponse;
import org.Job.query.model.response.JobResponse;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @QueryHandler
    @Transactional(readOnly = true)
    public JobPageResponse handle(GetJobsQuery query) {
        if (query.getPage() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page phải >= 0");
        }
        if (query.getSize() <= 0 || query.getSize() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size phải trong khoảng 1..100");
        }

        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Job> spec = Specification.where((root, cq, cb) ->
                cb.equal(root.get("status"), JobStatus.OPEN)
        );

        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String keyword = "%" + query.getKeyword().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), keyword),
                            cb.like(cb.lower(root.get("description")), keyword)
                    )
            );
        }

        if (query.getLocation() != null && !query.getLocation().isBlank()) {
            String loc = "%" + query.getLocation().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) ->
                    cb.like(cb.lower(root.get("location")), loc)
            );
        }

        if (query.getWorkingType() != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("workingType"), query.getWorkingType())
            );
        }

        if (query.getEmploymentType() != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("employmentType"), query.getEmploymentType())
            );
        }

        if (query.getLevel() != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("level"), query.getLevel())
            );
        }

        if (query.getCompanyId() != null && !query.getCompanyId().isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("companyId"), query.getCompanyId().trim())
            );
        }

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        java.util.Map<String, CompanyResponse> companyCache = new java.util.HashMap<>();

        List<JobResponse> dtoList = jobPage.getContent().stream()
                .map(job -> {
                    CompanyResponse company = companyCache.computeIfAbsent(job.getCompanyId(),
                            cid -> companyClient.getCompany(cid));

                    JobResponse.CompanyDto companyDto = null;
                    if (company != null) {
                        companyDto = JobResponse.CompanyDto.builder()
                                .id(company.getId())
                                .companyName(company.getCompanyName())
                                .logoUrl(company.getLogoUrl())
                                .build();
                    } else {
                        companyDto = JobResponse.CompanyDto.builder()
                                .id(job.getCompanyId())
                                .companyName("N/A")
                                .logoUrl(null)
                                .build();
                    }

                    List<String> skills = jobSkillRepository.findAllByJobId(job.getId())
                            .stream()
                            .map(JobSkill::getSkillName)
                            .collect(Collectors.toList());

                    List<String> categories = jobCategoryMappingRepository.findAllByJobId(job.getId())
                            .stream()
                            .map(m -> jobCategoryRepository.findById(m.getCategoryId()).orElse(null))
                            .filter(c -> c != null)
                            .map(JobCategory::getName)
                            .collect(Collectors.toList());

                    return JobResponse.builder()
                            .id(job.getId())
                            .title(job.getTitle())
                            .company(companyDto)
                            .location(job.getLocation())
                            .workingType(job.getWorkingType() != null ? job.getWorkingType().name() : null)
                            .employmentType(job.getEmploymentType() != null ? job.getEmploymentType().name() : null)
                            .level(job.getLevel() != null ? job.getLevel().name() : null)
                            .salary(JobResponse.SalaryDto.builder()
                                    .minSalary(job.getMinSalary())
                                    .maxSalary(job.getMaxSalary())
                                    .currency(job.getCurrency())
                                    .build())
                            .deadline(job.getDeadline())
                            .postedAt(job.getPublishedAt())
                            .urgent(job.getUrgent())
                            .featured(job.getFeatured())
                            .skills(skills)
                            .categories(categories)
                            .build();
                })
                .collect(Collectors.toList());

        return new JobPageResponse(
                dtoList,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
}
