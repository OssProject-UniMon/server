package dongguk.capstone.backend.repository;

import dongguk.capstone.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> { // 나중에 변경 필요
}
