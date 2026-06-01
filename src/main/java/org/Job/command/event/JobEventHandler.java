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
}
