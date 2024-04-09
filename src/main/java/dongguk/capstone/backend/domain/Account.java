package dongguk.capstone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

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