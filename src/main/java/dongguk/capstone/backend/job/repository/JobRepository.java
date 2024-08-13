package dongguk.capstone.backend.job.repository;

import dongguk.capstone.backend.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.userId = :userId")
    Optional<Job> findJobByUserId(@Param("userId") Long userId);

    @Query("SELECT j.jobId FROM Job j WHERE j.userId = :userId")
    Long findJobIdByUserId(@Param("userId") Long userId);
}
