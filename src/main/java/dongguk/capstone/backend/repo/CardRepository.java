package dongguk.capstone.backend.repo;

import dongguk.capstone.backend.domain.Card;
import dongguk.capstone.backend.serializable.CardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, CardId> {
    @Query("select c from Card c where c.cardId.userId = :userId and c.cardId.cardNum = :cardNum")
    Optional<Card> findByUserIdAndCardNum(@Param("userId") Long userId, @Param("cardNum") String cardNum);
}
