package dongguk.capstone.backend.report.repository;

import dongguk.capstone.backend.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {

}
