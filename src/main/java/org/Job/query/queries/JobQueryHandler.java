package org.Job.query.queries;

import org.Job.client.CompanyClient;
import org.Job.client.dto.CompanyResponse;
import org.Job.command.data.*;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobStatus;
import org.Job.query.model.response.JobDetailResponse;
import org.Job.query.model.response.JobCategoryListResponse;
import org.Job.query.model.response.JobCategoryResponse;
import org.Job.query.model.response.JobListResponse;
import org.Job.query.model.response.JobPageResponse;
import org.Job.query.model.response.JobResponse;
import org.Job.query.model.response.JobSkillResponse;
import org.Job.query.model.response.JobSkillListResponse;
import org.Job.query.model.response.JobBenefitResponse;
import org.Job.query.model.response.JobBenefitListResponse;
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
        if (job.getStatus() == JobStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc");
        }

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

    @QueryHandler
    @Transactional(readOnly = true)
    public JobListResponse handle(GetSimilarJobsQuery query) {
        Job originalJob = jobRepository.findById(query.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        List<String> targetCategoryIds = jobCategoryMappingRepository.findAllByJobId(originalJob.getId())
                .stream()
                .map(JobCategoryMapping::getCategoryId)
                .collect(Collectors.toList());

        List<String> targetSkills = jobSkillRepository.findAllByJobId(originalJob.getId())
                .stream()
                .map(s -> s.getSkillName().toLowerCase().trim())
                .collect(Collectors.toList());

        List<Job> allOpenJobs = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.OPEN && !j.getId().equals(originalJob.getId()))
                .collect(Collectors.toList());

        if (allOpenJobs.isEmpty()) {
            return new JobListResponse(List.of());
        }

        List<String> openJobIds = allOpenJobs.stream().map(Job::getId).collect(Collectors.toList());

        List<JobCategoryMapping> allMappings = jobCategoryMappingRepository.findAllByJobIdIn(openJobIds);
        java.util.Map<String, List<String>> jobToCategoriesMap = allMappings.stream()
                .collect(Collectors.groupingBy(
                        JobCategoryMapping::getJobId,
                        Collectors.mapping(JobCategoryMapping::getCategoryId, Collectors.toList())
                ));

        List<JobSkill> allSkills = jobSkillRepository.findAllByJobIdIn(openJobIds);
        java.util.Map<String, List<String>> jobToSkillsMap = allSkills.stream()
                .collect(Collectors.groupingBy(
                        JobSkill::getJobId,
                        Collectors.mapping(s -> s.getSkillName().toLowerCase().trim(), Collectors.toList())
                ));

        List<Job> filteredJobs = allOpenJobs.stream()
                .filter(job -> {
                    // Category filter
                    if (query.getCategoryId() != null && !query.getCategoryId().isBlank()) {
                        List<String> jobCategories = jobToCategoriesMap.getOrDefault(job.getId(), List.of());
                        if (!jobCategories.contains(query.getCategoryId())) {
                            return false;
                        }
                    }

                    // Skills filter
                    if (query.getSkills() != null && !query.getSkills().isEmpty()) {
                        List<String> jobSkills = jobToSkillsMap.getOrDefault(job.getId(), List.of());
                        boolean matchSkill = query.getSkills().stream()
                                .map(s -> s.toLowerCase().trim())
                                .anyMatch(jobSkills::contains);
                        if (!matchSkill) {
                            return false;
                        }
                    }

                    // Company filter
                    if (query.getCompanyId() != null && !query.getCompanyId().isBlank()) {
                        if (job.getCompanyId() == null || !job.getCompanyId().equals(query.getCompanyId().trim())) {
                            return false;
                        }
                    }

                    // Location filter
                    if (query.getLocation() != null && !query.getLocation().isBlank()) {
                        if (job.getLocation() == null || !job.getLocation().toLowerCase().contains(query.getLocation().toLowerCase().trim())) {
                            return false;
                        }
                    }

                    // EmploymentType filter
                    if (query.getEmploymentType() != null) {
                        if (job.getEmploymentType() != query.getEmploymentType()) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        if (filteredJobs.isEmpty()) {
            return new JobListResponse(List.of());
        }

        java.util.Map<String, CompanyResponse> companyCache = new java.util.HashMap<>();

        List<JobResponse> jobs = filteredJobs.stream()
                .map(job -> {
                    int score = 0;

                    // 1. Categories similarity
                    List<String> jobCategories = jobToCategoriesMap.getOrDefault(job.getId(), List.of());
                    for (String catId : jobCategories) {
                        if (targetCategoryIds.contains(catId)) {
                            score += 5;
                        }
                    }

                    // 2. Skills similarity
                    List<String> jobSkills = jobToSkillsMap.getOrDefault(job.getId(), List.of());
                    for (String skill : jobSkills) {
                        if (targetSkills.contains(skill)) {
                            score += 3;
                        }
                    }

                    // 3. Attribute matching
                    if (job.getLevel() != null && job.getLevel() == originalJob.getLevel()) {
                        score += 2;
                    }
                    if (job.getWorkingType() != null && job.getWorkingType() == originalJob.getWorkingType()) {
                        score += 2;
                    }
                    if (job.getEmploymentType() != null && job.getEmploymentType() == originalJob.getEmploymentType()) {
                        score += 2;
                    }
                    if (job.getCompanyId() != null && job.getCompanyId().equals(originalJob.getCompanyId())) {
                        score += 3;
                    }
                    if (job.getLocation() != null && job.getLocation().equalsIgnoreCase(originalJob.getLocation())) {
                        score += 1;
                    }

                    return new ScoredJob(job, score);
                })
                .sorted(java.util.Comparator.comparingInt(ScoredJob::getScore).reversed())
                .limit(query.getSize())
                .map(scoredJob -> {
                    Job job = scoredJob.getJob();
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
        return new JobListResponse(jobs);
    }

    private static class ScoredJob {
        private final Job job;
        private final int score;

        public ScoredJob(Job job, int score) {
            this.job = job;
            this.score = score;
        }

        public Job getJob() {
            return job;
        }

        public int getScore() {
            return score;
        }
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public JobListResponse handle(GetLatestJobsQuery query) {
        if (query.getLimit() <= 0 || query.getLimit() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit phải trong khoảng 1..100");
        }

        Pageable pageable = PageRequest.of(
                0,
                query.getLimit(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Job> spec = Specification.where((root, cq, cb) ->
                cb.equal(root.get("status"), JobStatus.OPEN)
        );

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);
        List<Job> latestJobs = jobPage.getContent();

        if (latestJobs.isEmpty()) {
            return new JobListResponse(List.of());
        }

        List<String> jobIds = latestJobs.stream().map(Job::getId).collect(Collectors.toList());

        List<JobCategoryMapping> allMappings = jobCategoryMappingRepository.findAllByJobIdIn(jobIds);
        java.util.Map<String, List<JobCategoryMapping>> jobToMappingsMap = allMappings.stream()
                .collect(Collectors.groupingBy(JobCategoryMapping::getJobId));

        List<JobSkill> allSkills = jobSkillRepository.findAllByJobIdIn(jobIds);
        java.util.Map<String, List<String>> jobToSkillsMap = allSkills.stream()
                .collect(Collectors.groupingBy(
                        JobSkill::getJobId,
                        Collectors.mapping(JobSkill::getSkillName, Collectors.toList())
                ));

        java.util.Map<String, CompanyResponse> companyCache = new java.util.HashMap<>();

        List<JobResponse> dtoList = latestJobs.stream()
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

                    List<String> skills = jobToSkillsMap.getOrDefault(job.getId(), List.of());

                    List<String> categories = jobToMappingsMap.getOrDefault(job.getId(), List.of())
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

        return new JobListResponse(dtoList);
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public JobCategoryListResponse handle(GetJobCategoriesQuery query) {
        List<JobCategoryResponse> list = jobCategoryRepository.findAll().stream()
                .map(c -> JobCategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .description(c.getDescription())
                        .active(c.getActive())
                        .build())
                .collect(Collectors.toList());
        return new JobCategoryListResponse(list);
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public JobSkillListResponse handle(GetJobSkillsQuery query) {
        Job job = jobRepository.findById(query.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        if (job.getStatus() == JobStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc");
        }

        List<JobSkillResponse> skills = jobSkillRepository.findAllByJobId(job.getId())
                .stream()
                .map(s -> JobSkillResponse.builder()
                        .id(s.getId())
                        .skillName(s.getSkillName())
                        .required(s.getRequired())
                        .build())
                .collect(Collectors.toList());

        return new JobSkillListResponse(skills);
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public JobBenefitListResponse handle(GetJobBenefitsQuery query) {
        Job job = jobRepository.findById(query.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        if (job.getStatus() == JobStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc");
        }

        List<JobBenefitResponse> benefits = jobBenefitRepository.findAllByJobId(job.getId())
                .stream()
                .map(b -> JobBenefitResponse.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .description(b.getDescription())
                        .icon(b.getIcon())
                        .build())
                .collect(Collectors.toList());

        return new JobBenefitListResponse(benefits);
    }
}
