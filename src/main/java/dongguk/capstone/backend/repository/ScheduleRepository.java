package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleEmbeddedUserId(Long userId);
}
