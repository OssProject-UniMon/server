package dongguk.capstone.backend.scholarship.repository;

import dongguk.capstone.backend.scholarship.entity.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
}
