package org.Job.command.data;

import jakarta.persistence.*;
import lombok.*;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.JobStatus;
import org.Job.constant.WorkingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    private String id;

    private String companyId;

    private String recruiterId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String whoYouAre;

    @Column(columnDefinition = "TEXT")
    private String niceToHaves;

    private String location;

    @Enumerated(EnumType.STRING)
    private WorkingType workingType;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    private JobLevel level;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private String currency;

    private Integer capacity;

    private Integer applicationCount;

    private Integer viewCount;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private Boolean featured;

    private Boolean urgent;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
