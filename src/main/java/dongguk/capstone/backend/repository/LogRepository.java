package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Log;
import dongguk.capstone.backend.serializable.LogEmbedded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, LogEmbedded> {
    @Query("SELECT MAX(l.logEmbedded.logId) FROM Log l WHERE l.logEmbedded.userId = :userId")
    Long findMaxLogIdByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM Log l WHERE l.logEmbedded.userId = :userId")
    List<Log> findLogsByUserId(@Param("userId") Long userId);

    void deleteByLogEmbeddedUserId(Long userId);
}
