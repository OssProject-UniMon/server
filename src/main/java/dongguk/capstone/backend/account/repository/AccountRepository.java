package dongguk.capstone.backend.account.repository;

import dongguk.capstone.backend.account.entity.Account;
import dongguk.capstone.backend.serializable.AccountEmbedded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, AccountEmbedded> { // 나중에 변경 필요
    // 복합키로 pk를 설정한 엔티티의 경우 findById 같은 메소드를 사용하지 못하고(여러 개의 칼럼으로 pk를 구성해서 pk가 id 하나가 아니기 때문),
    // 따로 쿼리문과 find 메소드를 설정해주어야 한다.
    @Query("SELECT a FROM Account a WHERE a.accountEmbedded.userId = :userId AND a.accountEmbedded.bankAccountNum = :bankAccountNum")
    // 이 :userId와 :bankAccountNum의 의미는 무엇인가?
    // => : 기호는 Spring Data JPA에서 사용되는 Named Parameter(이름이 지정된 매개변수)를 나타냅니다. 이것은 쿼리의 매개변수를 식별하는 데 사용됩니다.
    // => 쿼리 내부에서 :userId와 :bankAccountNum은 실제 매개변수 값으로 대체됩니다.
    Optional<Account> findByUserIdAndBankAccountNum(@Param("userId") Long userId, @Param("bankAccountNum") String bankAccountNum);

    @Query("SELECT a FROM Account a WHERE a.accountEmbedded.userId = :userId")
    Optional<Account> findByUserId(@Param("userId") Long userId);
}