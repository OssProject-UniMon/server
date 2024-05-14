package dongguk.capstone.backend.domain;

import dongguk.capstone.backend.serializable.CardEmbedded;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
