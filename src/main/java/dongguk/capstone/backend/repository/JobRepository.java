package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.userId = :userId")
    Optional<Job> findJobByUserId(Long userId);
}
