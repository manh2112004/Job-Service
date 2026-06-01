package org.Job.command.service.impl;

import org.Job.client.CompanyClient;
import org.Job.client.dto.CompanyMemberResponse;
import org.Job.command.command.CreateJobCommand;
import org.Job.command.command.UpdateJobCommand;
import org.Job.command.command.PublishJobCommand;
import org.Job.command.command.DeleteJobCommand;
import org.Job.command.command.CloseJobCommand;
import org.Job.command.data.Job;
import org.Job.command.data.JobRepository;
import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.service.JobService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private CompanyClient companyClient;

    @Autowired
    private JobRepository jobRepository;


    @Override
    public CompletableFuture<String> createJob(String userId, CreateJobRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        // Validate company membership and permissions via Company Service
        CompanyMemberResponse member = companyClient.getCompanyMember(request.getCompanyId().trim(), userId);
        if (member == null || !Boolean.TRUE.equals(member.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đăng tuyển cho công ty này");
        }

        // Only allow OWNER, HR_MANAGER, or RECRUITER roles to post jobs
        String role = member.getRole();
        if (!"OWNER".equals(role) && !"HR_MANAGER".equals(role) && !"RECRUITER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đăng tuyển cho công ty này");
        }

        CreateJobCommand command = CreateJobCommand.builder()
                .jobId(UUID.randomUUID().toString())
                .companyId(request.getCompanyId().trim())
                .recruiterId(userId)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .responsibilities(request.getResponsibilities() != null ? request.getResponsibilities().trim() : null)
                .whoYouAre(request.getWhoYouAre().trim())
                .niceToHaves(request.getNiceToHaves() != null ? request.getNiceToHaves().trim() : null)
                .location(request.getLocation().trim())
                .workingType(request.getWorkingType())
                .employmentType(request.getEmploymentType())
                .level(request.getLevel())
                .minSalary(request.getMinSalary())
                .maxSalary(request.getMaxSalary())
                .currency(request.getCurrency())
                .capacity(request.getCapacity())
                .deadline(request.getDeadline())
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .urgent(request.getUrgent() != null ? request.getUrgent() : false)
                .skills(request.getSkills() == null ? Collections.emptyList() : request.getSkills().stream()
                        .map(s -> CreateJobCommand.JobSkillCommandInfo.builder()
                                .skillName(s.getSkillName().trim())
                                .required(s.getRequired())
                                .build())
                        .collect(Collectors.toList()))
                .benefits(request.getBenefits() == null ? Collections.emptyList() : request.getBenefits().stream()
                        .map(b -> CreateJobCommand.JobBenefitCommandInfo.builder()
                                .title(b.getTitle().trim())
                                .description(b.getDescription() != null ? b.getDescription().trim() : null)
                                .icon(b.getIcon() != null ? b.getIcon().trim() : null)
                                .build())
                        .collect(Collectors.toList()))
                .categoryIds(request.getCategoryIds() == null ? Collections.emptyList() : request.getCategoryIds().stream()
                        .map(String::trim)
                        .collect(Collectors.toList()))
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> updateJob(String userId, String jobId, UpdateJobRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        // Validate permissions: must be recruiter who created the job OR authorized company member
        if (!job.getRecruiterId().equals(userId)) {
            CompanyMemberResponse member = companyClient.getCompanyMember(job.getCompanyId(), userId);
            if (member == null || !Boolean.TRUE.equals(member.getActive())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa thông tin công việc này");
            }
            String role = member.getRole();
            if (!"OWNER".equals(role) && !"HR_MANAGER".equals(role) && !"RECRUITER".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa thông tin công việc này");
            }
        }

        UpdateJobCommand command = UpdateJobCommand.builder()
                .jobId(jobId)
                .title(request.getTitle() != null ? request.getTitle().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .responsibilities(request.getResponsibilities() != null ? request.getResponsibilities().trim() : null)
                .whoYouAre(request.getWhoYouAre() != null ? request.getWhoYouAre().trim() : null)
                .niceToHaves(request.getNiceToHaves() != null ? request.getNiceToHaves().trim() : null)
                .location(request.getLocation() != null ? request.getLocation().trim() : null)
                .workingType(request.getWorkingType())
                .employmentType(request.getEmploymentType())
                .level(request.getLevel())
                .minSalary(request.getMinSalary())
                .maxSalary(request.getMaxSalary())
                .currency(request.getCurrency() != null ? request.getCurrency().trim() : null)
                .capacity(request.getCapacity())
                .deadline(request.getDeadline())
                .status(request.getStatus())
                .featured(request.getFeatured())
                .urgent(request.getUrgent())
                .skills(request.getSkills() == null ? null : request.getSkills().stream()
                        .map(s -> CreateJobCommand.JobSkillCommandInfo.builder()
                                .skillName(s.getSkillName().trim())
                                .required(s.getRequired())
                                .build())
                        .collect(Collectors.toList()))
                .benefits(request.getBenefits() == null ? null : request.getBenefits().stream()
                        .map(b -> CreateJobCommand.JobBenefitCommandInfo.builder()
                                .title(b.getTitle().trim())
                                .description(b.getDescription() != null ? b.getDescription().trim() : null)
                                .icon(b.getIcon() != null ? b.getIcon().trim() : null)
                                .build())
                        .collect(Collectors.toList()))
                .categoryIds(request.getCategoryIds() == null ? null : request.getCategoryIds().stream()
                        .map(String::trim)
                        .collect(Collectors.toList()))
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> publishJob(String userId, String jobId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        // Validate permissions: must be recruiter who created the job OR authorized company member
        if (!job.getRecruiterId().equals(userId)) {
            CompanyMemberResponse member = companyClient.getCompanyMember(job.getCompanyId(), userId);
            if (member == null || !Boolean.TRUE.equals(member.getActive())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xuất bản công việc này");
            }
            String role = member.getRole();
            if (!"OWNER".equals(role) && !"HR_MANAGER".equals(role) && !"RECRUITER".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xuất bản công việc này");
            }
        }

        PublishJobCommand command = PublishJobCommand.builder()
                .jobId(jobId)
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> deleteJob(String userId, String jobId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        // Validate permissions: must be recruiter who created the job OR authorized company member
        if (!job.getRecruiterId().equals(userId)) {
            CompanyMemberResponse member = companyClient.getCompanyMember(job.getCompanyId(), userId);
            if (member == null || !Boolean.TRUE.equals(member.getActive())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa công việc này");
            }
            String role = member.getRole();
            if (!"OWNER".equals(role) && !"HR_MANAGER".equals(role) && !"RECRUITER".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa công việc này");
            }
        }

        DeleteJobCommand command = DeleteJobCommand.builder()
                .jobId(jobId)
                .build();

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> closeJob(String userId, String jobId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được user từ token");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));

        // Validate permissions: must be recruiter who created the job OR authorized company member
        if (!job.getRecruiterId().equals(userId)) {
            CompanyMemberResponse member = companyClient.getCompanyMember(job.getCompanyId(), userId);
            if (member == null || !Boolean.TRUE.equals(member.getActive())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đóng công việc này");
            }
            String role = member.getRole();
            if (!"OWNER".equals(role) && !"HR_MANAGER".equals(role) && !"RECRUITER".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đóng công việc này");
            }
        }

        CloseJobCommand command = CloseJobCommand.builder()
                .jobId(jobId)
                .build();

        return commandGateway.send(command);
    }
}
