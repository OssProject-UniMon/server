package dongguk.capstone.backend.categoryconsumption.repository;

import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryConsumptionRepository extends JpaRepository<CategoryConsumption,Long> {

    @Query("select cc from CategoryConsumption cc where cc.userId = :userId")
    Optional<CategoryConsumption> findCategoryConsumptionByUserId(@Param("userId") Long userId);
}
