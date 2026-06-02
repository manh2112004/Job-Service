package org.Job.command.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, String> {
    List<JobSkill> findAllByJobId(String jobId);
    List<JobSkill> findAllByJobIdIn(List<String> jobIds);
    void deleteAllByJobId(String jobId);
}
