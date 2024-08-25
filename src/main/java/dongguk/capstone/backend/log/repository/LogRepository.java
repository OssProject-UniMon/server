package dongguk.capstone.backend.log.repository;

import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.entity.LogEmbedded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, LogEmbedded> {
    @Query("SELECT MAX(l.logEmbedded.logId) FROM Log l WHERE l.logEmbedded.userId = :userId")
    Long findMaxLogIdByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM Log l WHERE l.logEmbedded.userId = :userId")
    List<Log> findLogsByUserId(@Param("userId") Long userId);

    @Query("select l from Log l where l.logEmbedded.userId = :userId and substring(l.date,1,6) = :currentMonth")
    List<Log> findLogsByUserIdAndMonth(@Param("userId") Long userId, @Param("currentMonth") String currentMonth);

    void deleteByLogEmbeddedUserId(Long userId);

    @Query("select l from Log l where l.logEmbedded.userId = :userId and substring(l.date,1,8) = :previousDayString")
    List<Log> findLogsByUserIdAndDay(@Param("userId") Long userId, @Param("previousDayString") String previousDayString);

    @Query("SELECT MAX(l.date) FROM Log l WHERE l.logEmbedded.userId = :userId")
    String findLastSavedDateByUserId(@Param("userId") Long userId);
}
