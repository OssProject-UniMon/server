package dongguk.capstone.backend.account.dto.request;

import lombok.*;

@Data
@AllArgsConstructor // 클래스에 대한 모든 필드를 파라미터로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 해당 클래스에 매개변수가 없는 기본 생성자를 자동으로 생성
public class AccountReqRegistDTO {
    private String bank;
    private String bankAccountType;
    private String bankAccountNum;
    private String bankAccountPwd;
    private String webId;
    private String webPwd;
    private String identityNum;
}
