package dongguk.capstone.backend.monthlyaggregation.repository;

import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MonthlyAggregationRepository extends JpaRepository<MonthlyAggregation,Long> {

    @Query("select ma from MonthlyAggregation ma where ma.userId = :userId")
    Optional<MonthlyAggregation> findMonthlyAggregationByUserId(@Param("userId") Long userId);
}
