package dongguk.capstone.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountId implements Serializable {
    private Long user;
    private String bankAccountNum;
}
