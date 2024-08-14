package dongguk.capstone.backend.card.entity;

import dongguk.capstone.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card")
public class Card {
    @EmbeddedId // 복합키 하는 방법
    private CardEmbedded cardEmbedded;

    @Column(name = "card_company")
    private String cardCompany;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "web_id")
    private String webId;

    @Column(name = "web_pwd")
    private String webPwd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
