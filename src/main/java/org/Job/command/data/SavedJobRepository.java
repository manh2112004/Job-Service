package org.Job.command.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, String> {
    Optional<SavedJob> findByCandidateIdAndJobId(String candidateId, String jobId);
    Page<SavedJob> findAllByCandidateId(String candidateId, Pageable pageable);
    boolean existsByCandidateIdAndJobId(String candidateId, String jobId);
}
