package dongguk.capstone.backend.domain;

import dongguk.capstone.backend.serializable.AccountEmbedded;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {
    @EmbeddedId
    private AccountEmbedded accountEmbedded;

    @Column(name = "bank")
    private String bank;

    @Column(name = "bank_account_type")
    private String bankAccountType;

    @Column(name = "bank_account_pwd")
    private String bankAccountPwd;

    @Column(name = "web_id")
    private String webId;

    @Column(name = "web_pwd")
    private String webPwd;

    @Column(name = "identity_num")
    private String identityNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;


//    // 엔티티의 기본 키가 복합 키인 경우이므로 findById 메서드를 사용할 수 없습니다.
//    @EmbeddedId
//    private AccountId id;
//
//    @MapsId("userId") // 복합 키의 일부인 userId를 매핑합니다.
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}