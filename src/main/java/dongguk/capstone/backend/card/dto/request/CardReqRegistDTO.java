package dongguk.capstone.backend.card.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardReqRegistDTO {
    private String cardCompany;
    private String cardType;
    private String cardNum;
    private String webId;
    private String webPwd;
}
