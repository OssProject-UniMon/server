package dongguk.capstone.backend.schedule.repository;

import dongguk.capstone.backend.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId")
    List<Schedule> findSchedulesByUserId(@Param("userId") Long userId);
}
