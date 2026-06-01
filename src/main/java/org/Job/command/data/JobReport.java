package org.Job.command.data;

import jakarta.persistence.*;
import lombok.*;
import org.Job.constant.ReportStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobReport {

    @Id
    private String id;

    private String jobId;

    private String reporterId;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime createdAt;
}
