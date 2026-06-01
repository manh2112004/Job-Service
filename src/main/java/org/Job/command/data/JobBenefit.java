package org.Job.command.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "job_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobBenefit {

    @Id
    private String id;

    private String jobId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;
}
