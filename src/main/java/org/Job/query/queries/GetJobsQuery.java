package org.Job.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Job.constant.EmploymentType;
import org.Job.constant.JobLevel;
import org.Job.constant.WorkingType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetJobsQuery {
    private int page;
    private int size;
    private String keyword;
    private String location;
    private WorkingType workingType;
    private EmploymentType employmentType;
    private JobLevel level;
    private String companyId;
}
