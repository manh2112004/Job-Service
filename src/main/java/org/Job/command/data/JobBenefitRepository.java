package org.Job.command.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobBenefitRepository extends JpaRepository<JobBenefit, String> {
    List<JobBenefit> findAllByJobId(String jobId);
    void deleteAllByJobId(String jobId);
}
