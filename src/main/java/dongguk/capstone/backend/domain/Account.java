package dongguk.capstone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "account")
@IdClass(AccountId.class)
public class Account {
    @Id
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