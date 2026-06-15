package org.Job.command.controller;

import jakarta.validation.Valid;
import org.Job.command.model.request.CreateJobRequest;
import org.Job.command.model.request.UpdateJobRequest;
import org.Job.command.model.request.UpdateJobSkillsRequest;
import org.Job.command.model.request.UpdateJobSkillRequest;
import org.Job.command.model.request.CreateJobSkillRequest;
import org.Job.command.model.request.CreateJobBenefitRequest;
import org.Job.command.model.request.UpdateJobBenefitRequest;
import org.Job.command.model.request.CreateJobReportRequest;
import org.Job.command.service.JobService;
import org.Job.event.KafkaEvent;
import org.Job.event.KafkaEventProducer;
import org.Job.event.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobCommandController {

    @Autowired
    private JobService jobService;

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @PostMapping
    public CompletableFuture<String> createJob(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateJobRequest request
    ) {
        return jobService.createJob(jwt.getSubject(), request).thenApply(jobId -> {
            kafkaEventProducer.sendEvent(KafkaTopic.JOB_EVENTS, KafkaEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("JobCreatedEvent")
                    .userId(jwt.getSubject())
                    .referenceId(jobId)
                    .referenceType("JOB")
                    .title("Đăng tin tuyển dụng thành công")
                    .message("Tin tuyển dụng '" + request.getTitle() + "' đã được tạo thành công.")
                    .createdAt(LocalDateTime.now())
                    .build());
            return jobId;
        });
    }

    @PutMapping("/{jobId}")
    public CompletableFuture<String> updateJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobRequest request
    ) {
        return jobService.updateJob(jwt.getSubject(), jobId, request).thenApply(result -> {
            kafkaEventProducer.sendEvent(KafkaTopic.JOB_EVENTS, KafkaEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("JobUpdatedEvent")
                    .userId(jwt.getSubject())
                    .referenceId(jobId)
                    .referenceType("JOB")
                    .title("Cập nhật tin tuyển dụng")
                    .message("Tin tuyển dụng '" + request.getTitle() + "' đã được cập nhật.")
                    .createdAt(LocalDateTime.now())
                    .build());
            return result;
        });
    }

    @PutMapping("/{jobId}/publish")
    public CompletableFuture<String> publishJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.publishJob(jwt.getSubject(), jobId);
    }

    @DeleteMapping("/{jobId}")
    public CompletableFuture<String> deleteJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.deleteJob(jwt.getSubject(), jobId);
    }

    @PutMapping("/{jobId}/close")
    public CompletableFuture<String> closeJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.closeJob(jwt.getSubject(), jobId).thenApply(result -> {
            kafkaEventProducer.sendEvent(KafkaTopic.JOB_EVENTS, KafkaEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("JobClosedEvent")
                    .userId(jwt.getSubject())
                    .referenceId(jobId)
                    .referenceType("JOB")
                    .title("Đóng tin tuyển dụng")
                    .message("Tin tuyển dụng của bạn đã đóng.")
                    .createdAt(LocalDateTime.now())
                    .build());
            return result;
        });
    }

    @PutMapping("/{jobId}/expire")
    public CompletableFuture<String> expireJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        kafkaEventProducer.sendEvent(KafkaTopic.JOB_EVENTS, KafkaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("JobExpiredEvent")
                .userId(jwt.getSubject())
                .referenceId(jobId)
                .referenceType("JOB")
                .title("Tin tuyển dụng hết hạn")
                .message("Tin tuyển dụng của bạn đã hết hạn.")
                .createdAt(LocalDateTime.now())
                .build());
        return CompletableFuture.completedFuture("Đã chuyển trạng thái tin tuyển dụng sang hết hạn thành công");
    }

    @PutMapping("/{jobId}/reopen")
    public CompletableFuture<String> reopenJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.reopenJob(jwt.getSubject(), jobId);
    }

    @PutMapping("/{jobId}/skills")
    public CompletableFuture<String> updateJobSkills(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobSkillsRequest request
    ) {
        return jobService.updateJobSkills(jwt.getSubject(), jobId, request);
    }

    @PutMapping("/skills/{skillId}")
    public CompletableFuture<String> updateSingleJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String skillId,
            @Valid @RequestBody UpdateJobSkillRequest request
    ) {
        return jobService.updateSingleJobSkill(jwt.getSubject(), skillId, request);
    }

    @DeleteMapping("/skills/{skillId}")
    public CompletableFuture<String> deleteJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String skillId
    ) {
        return jobService.deleteJobSkill(jwt.getSubject(), skillId);
    }

    @PostMapping("/{jobId}/skills")
    public CompletableFuture<String> addJobSkill(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobSkillRequest request
    ) {
        return jobService.addJobSkill(jwt.getSubject(), jobId, request);
    }

    @PostMapping("/{jobId}/benefits")
    public CompletableFuture<String> addJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobBenefitRequest request
    ) {
        return jobService.addJobBenefit(jwt.getSubject(), jobId, request);
    }

    @PutMapping("/benefits/{benefitId}")
    public CompletableFuture<String> updateSingleJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String benefitId,
            @Valid @RequestBody UpdateJobBenefitRequest request
    ) {
        return jobService.updateSingleJobBenefit(jwt.getSubject(), benefitId, request);
    }

    @DeleteMapping("/benefits/{benefitId}")
    public CompletableFuture<String> deleteJobBenefit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String benefitId
    ) {
        return jobService.deleteJobBenefit(jwt.getSubject(), benefitId);
    }

    @PostMapping("/{jobId}/reports")
    public CompletableFuture<String> createJobReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId,
            @Valid @RequestBody CreateJobReportRequest request
    ) {
        return jobService.createJobReport(jwt.getSubject(), jobId, request);
    }

    @PostMapping("/{jobId}/save")
    public CompletableFuture<String> saveJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.saveJob(jwt.getSubject(), jobId);
    }

    @DeleteMapping("/{jobId}/save")
    public CompletableFuture<String> unsaveJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String jobId
    ) {
        return jobService.unsaveJob(jwt.getSubject(), jobId);
    }
}

