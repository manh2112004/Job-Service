package org.Job.command.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobReportRepository extends JpaRepository<JobReport, String> {

    boolean existsByJobIdAndReporterId(String jobId, String reporterId);
}
