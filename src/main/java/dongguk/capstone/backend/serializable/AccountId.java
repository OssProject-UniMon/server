package dongguk.capstone.backend.serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "bank_account_num")
    private String bankAccountNum;
}
