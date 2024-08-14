package dongguk.capstone.backend.dailyconsumption.repository;

import dongguk.capstone.backend.dailyconsumption.entity.DailyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DailyConsumptionRepository extends JpaRepository<DailyConsumption,Long> {
    @Query("select dc from DailyConsumption dc where dc.userId = :userId")
    Optional<DailyConsumption> findDailyConsumptionByUserId(@Param("userId") Long userId);
}
