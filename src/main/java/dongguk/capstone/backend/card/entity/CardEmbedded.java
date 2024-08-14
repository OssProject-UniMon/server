package dongguk.capstone.backend.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardEmbedded implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "card_num")
    private String cardNum;
}
