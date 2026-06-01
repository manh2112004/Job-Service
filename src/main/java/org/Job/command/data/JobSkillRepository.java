package org.Job.command.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, String> {
    List<JobSkill> findAllByJobId(String jobId);
    void deleteAllByJobId(String jobId);
}
