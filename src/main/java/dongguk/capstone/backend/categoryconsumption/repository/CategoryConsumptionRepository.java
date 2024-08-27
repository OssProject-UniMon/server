package dongguk.capstone.backend.categoryconsumption.repository;

import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryConsumptionRepository extends JpaRepository<CategoryConsumption,Long> {
    @Query("select cc from CategoryConsumption cc where cc.userId = :userId")
    Optional<CategoryConsumption> findCategoryConsumptionByUserId(@Param("userId") Long userId);

    // 이거 2개 이상일 수도 있어서 List로 해야될듯
    @Query("SELECT cc FROM CategoryConsumption cc WHERE cc.userId = :userId AND cc.date = :date")
    List<CategoryConsumption> findCategoryConsumptionByUserIdAndDay(@Param("userId") Long userId, @Param("date") String date);

    @Query("SELECT cc FROM CategoryConsumption cc WHERE cc.userId = :userId AND substring(cc.date,1,6) = :date")
    List<CategoryConsumption> findCategoryConsumptionsByUserIdAndMonth(@Param("userId") Long userId, @Param("date") String date);
}
