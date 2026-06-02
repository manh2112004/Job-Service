package org.Job.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Job.constant.EmploymentType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSimilarJobsQuery {
    private String jobId;
    private int size;
    private String categoryId;
    private List<String> skills;
    private String companyId;
    private String location;
    private EmploymentType employmentType;
}
