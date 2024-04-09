package dongguk.capstone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {

    // 엔티티의 기본 키가 복합 키인 경우이므로 findById 메서드를 사용할 수 없습니다.
    @EmbeddedId
    private AccountId id;

    @MapsId("userId") // 복합 키의 일부인 userId를 매핑합니다.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @Column(name = "bank_account_num", length = 60)
    private String bankAccountNum;


    @Column(name = "bank", length = 20)
    private String bank;

    @Column(name = "bank_account_type", length = 20)
    private String bankAccountType;

    @Column(name = "bank_account_pwd", length = 60)
    private String bankAccountPwd;

    @Column(name = "web_id", length = 50)
    private String webId;

    @Column(name = "web_pwd", length = 50)
    private String webPwd;

    @Column(name = "identity_num", length = 50)
    private String identityNum;
}