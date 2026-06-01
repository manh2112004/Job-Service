package org.Job.command.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "job_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategory {

    @Id
    private String id;

    @Column(unique = true)
    private String name;

    private String slug;

    private String description;

    private Boolean active;
}