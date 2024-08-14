package dongguk.capstone.backend.account.entity;

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
public class AccountEmbedded implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "bank_account_num")
    private String bankAccountNum;
}
