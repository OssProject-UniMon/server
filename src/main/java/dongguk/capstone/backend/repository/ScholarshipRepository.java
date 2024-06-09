package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
}
