package dongguk.capstone.backend.report.repository;

import dongguk.capstone.backend.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {
    @Query("select r from Report r where r.userId = :userId and r.date =:date")
    Optional<Report> findReportByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
}
