package dongguk.capstone.backend.account.entity;

import dongguk.capstone.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}