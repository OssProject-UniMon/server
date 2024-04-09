package dongguk.capstone.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AccountId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "bank_account_num")
    private String bankAccountNum;
}
