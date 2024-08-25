package dongguk.capstone.backend.card.repository;

import dongguk.capstone.backend.card.entity.Card;
import dongguk.capstone.backend.card.entity.CardEmbedded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, CardEmbedded> {
    @Query("select c from Card c where c.cardEmbedded.userId = :userId and c.cardEmbedded.cardNum = :cardNum")
    Optional<Card> findByUserIdAndCardNum(@Param("userId") Long userId, @Param("cardNum") String cardNum);

    @Query("SELECT c FROM Card c WHERE c.cardEmbedded.userId = :userId")
    Optional<Card> findByUserId(@Param("userId") Long userId);
}
