package org.Job.command.event;

import org.Job.command.data.*;
import org.Job.constant.JobStatus;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JobEventHandler {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private JobBenefitRepository jobBenefitRepository;

    @Autowired
    private JobCategoryMappingRepository jobCategoryMappingRepository;

    @EventHandler
    @Transactional
    public void on(JobCreatedEvent event) {
        LocalDateTime now = LocalDateTime.now();

        Job job = Job.builder()
                .id(event.getJobId())
                .companyId(event.getCompanyId())
                .recruiterId(event.getRecruiterId())
                .title(event.getTitle())
                .description(event.getDescription())
                .responsibilities(event.getResponsibilities())
                .whoYouAre(event.getWhoYouAre())
                .niceToHaves(event.getNiceToHaves())
                .location(event.getLocation())
                .workingType(event.getWorkingType())
                .employmentType(event.getEmploymentType())
                .level(event.getLevel())
                .minSalary(event.getMinSalary())
                .maxSalary(event.getMaxSalary())
                .currency(event.getCurrency() != null ? event.getCurrency().trim() : "USD")
                .capacity(event.getCapacity())
                .applicationCount(0)
                .viewCount(0)
                .deadline(event.getDeadline())
                .status(JobStatus.OPEN)
                .featured(event.getFeatured() != null ? event.getFeatured() : false)
                .urgent(event.getUrgent() != null ? event.getUrgent() : false)
                .publishedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        jobRepository.save(job);

        // Save skills
        if (event.getSkills() != null) {
            for (JobCreatedEvent.JobSkillEventInfo skillInfo : event.getSkills()) {
                JobSkill skill = JobSkill.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .skillName(skillInfo.getSkillName().trim())
                        .required(skillInfo.getRequired() != null ? skillInfo.getRequired() : false)
                        .build();
                jobSkillRepository.save(skill);
            }
        }

        // Save benefits
        if (event.getBenefits() != null) {
            for (JobCreatedEvent.JobBenefitEventInfo benefitInfo : event.getBenefits()) {
                JobBenefit benefit = JobBenefit.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .title(benefitInfo.getTitle().trim())
                        .description(benefitInfo.getDescription() != null ? benefitInfo.getDescription().trim() : null)
                        .icon(benefitInfo.getIcon() != null ? benefitInfo.getIcon().trim() : null)
                        .build();
                jobBenefitRepository.save(benefit);
            }
        }

        // Save category mappings
        if (event.getCategoryIds() != null) {
            for (String categoryId : event.getCategoryIds()) {
                JobCategoryMapping mapping = JobCategoryMapping.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .categoryId(categoryId.trim())
                        .build();
                jobCategoryMappingRepository.save(mapping);
            }
        }
    }
    @EventHandler
    @Transactional
    public void on(JobUpdatedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        if (event.getTitle() != null) job.setTitle(event.getTitle());
        if (event.getDescription() != null) job.setDescription(event.getDescription());
        if (event.getResponsibilities() != null) job.setResponsibilities(event.getResponsibilities());
        if (event.getWhoYouAre() != null) job.setWhoYouAre(event.getWhoYouAre());
        if (event.getNiceToHaves() != null) job.setNiceToHaves(event.getNiceToHaves());
        if (event.getLocation() != null) job.setLocation(event.getLocation());
        if (event.getWorkingType() != null) job.setWorkingType(event.getWorkingType());
        if (event.getEmploymentType() != null) job.setEmploymentType(event.getEmploymentType());
        if (event.getLevel() != null) job.setLevel(event.getLevel());
        if (event.getMinSalary() != null) job.setMinSalary(event.getMinSalary());
        if (event.getMaxSalary() != null) job.setMaxSalary(event.getMaxSalary());
        if (event.getCurrency() != null) job.setCurrency(event.getCurrency());
        if (event.getCapacity() != null) job.setCapacity(event.getCapacity());
        if (event.getDeadline() != null) job.setDeadline(event.getDeadline());
        if (event.getStatus() != null) job.setStatus(event.getStatus());
        if (event.getFeatured() != null) job.setFeatured(event.getFeatured());
        if (event.getUrgent() != null) job.setUrgent(event.getUrgent());

        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        // Update skills if provided
        if (event.getSkills() != null) {
            jobSkillRepository.deleteAllByJobId(event.getJobId());
            for (JobCreatedEvent.JobSkillEventInfo skillInfo : event.getSkills()) {
                JobSkill skill = JobSkill.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .skillName(skillInfo.getSkillName().trim())
                        .required(skillInfo.getRequired() != null ? skillInfo.getRequired() : false)
                        .build();
                jobSkillRepository.save(skill);
            }
        }

        // Update benefits if provided
        if (event.getBenefits() != null) {
            jobBenefitRepository.deleteAllByJobId(event.getJobId());
            for (JobCreatedEvent.JobBenefitEventInfo benefitInfo : event.getBenefits()) {
                JobBenefit benefit = JobBenefit.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .title(benefitInfo.getTitle().trim())
                        .description(benefitInfo.getDescription() != null ? benefitInfo.getDescription().trim() : null)
                        .icon(benefitInfo.getIcon() != null ? benefitInfo.getIcon().trim() : null)
                        .build();
                jobBenefitRepository.save(benefit);
            }
        }

        // Update category mappings if provided
        if (event.getCategoryIds() != null) {
            jobCategoryMappingRepository.deleteAllByJobId(event.getJobId());
            for (String categoryId : event.getCategoryIds()) {
                JobCategoryMapping mapping = JobCategoryMapping.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .categoryId(categoryId.trim())
                        .build();
                jobCategoryMappingRepository.save(mapping);
            }
        }
    }

    @EventHandler
    @Transactional
    public void on(JobPublishedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        job.setStatus(JobStatus.OPEN);
        job.setPublishedAt(event.getPublishedAt());
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobApprovedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        job.setStatus(JobStatus.OPEN);
        job.setPublishedAt(event.getApprovedAt());
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobRejectedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        job.setStatus(JobStatus.REJECTED);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobDeletedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setStatus(JobStatus.DELETED);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobClosedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        job.setStatus(JobStatus.CLOSED);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobSkillsUpdatedEvent event) {
        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        // Update skills: delete all existing and save new ones
        if (event.getSkills() != null) {
            jobSkillRepository.deleteAllByJobId(event.getJobId());
            for (JobCreatedEvent.JobSkillEventInfo skillInfo : event.getSkills()) {
                JobSkill skill = JobSkill.builder()
                        .id(UUID.randomUUID().toString())
                        .jobId(event.getJobId())
                        .skillName(skillInfo.getSkillName().trim())
                        .required(skillInfo.getRequired() != null ? skillInfo.getRequired() : false)
                        .build();
                jobSkillRepository.save(skill);
            }
        }
    }

    @EventHandler
    @Transactional
    public void on(SingleJobSkillUpdatedEvent event) {
        JobSkill skill = jobSkillRepository.findById(event.getSkillId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy kỹ năng"));

        if (event.getSkillName() != null) {
            skill.setSkillName(event.getSkillName().trim());
        }
        if (event.getRequired() != null) {
            skill.setRequired(event.getRequired());
        }
        jobSkillRepository.save(skill);

        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobSkillCreatedEvent event) {
        JobSkill skill = JobSkill.builder()
                .id(event.getSkillId())
                .jobId(event.getJobId())
                .skillName(event.getSkillName().trim())
                .required(event.getRequired() != null ? event.getRequired() : false)
                .build();
        jobSkillRepository.save(skill);

        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobBenefitCreatedEvent event) {
        JobBenefit benefit = JobBenefit.builder()
                .id(event.getBenefitId())
                .jobId(event.getJobId())
                .title(event.getTitle().trim())
                .description(event.getDescription() != null ? event.getDescription().trim() : null)
                .icon(event.getIcon() != null ? event.getIcon().trim() : null)
                .build();
        jobBenefitRepository.save(benefit);

        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(SingleJobBenefitUpdatedEvent event) {
        JobBenefit benefit = jobBenefitRepository.findById(event.getBenefitId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy phúc lợi"));

        if (event.getTitle() != null) {
            benefit.setTitle(event.getTitle().trim());
        }
        if (event.getDescription() != null) {
            benefit.setDescription(event.getDescription().trim());
        }
        if (event.getIcon() != null) {
            benefit.setIcon(event.getIcon().trim());
        }
        jobBenefitRepository.save(benefit);

        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @EventHandler
    @Transactional
    public void on(JobBenefitDeletedEvent event) {
        jobBenefitRepository.deleteById(event.getBenefitId());

        Job job = jobRepository.findById(event.getJobId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }
}

