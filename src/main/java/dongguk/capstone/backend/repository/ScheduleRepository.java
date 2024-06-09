package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT MAX(s.scheduleEmbedded.scheduleId) FROM Schedule s WHERE s.scheduleEmbedded.userId = :userId")
    Long findMaxScheduleIdByUserId(@Param("userId") Long userId);

    List<Schedule> findByScheduleEmbeddedUserId(Long userId);
}
