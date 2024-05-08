package dongguk.capstone.backend.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRegistRequestDTO {
    private String cardCompany;
    private String cardType;
    private String cardNum;
    private String webId;
    private String webPwd;
}
