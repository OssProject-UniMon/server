package dongguk.capstone.backend.accountdto;

import lombok.*;

@Data
@AllArgsConstructor // 클래스에 대한 모든 필드를 파라미터로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 해당 클래스에 매개변수가 없는 기본 생성자를 자동으로 생성
public class AccountRegistRequestDTO {
    private String bank;
    private String bank_account_type;
    private String bank_account_num;
    private String bank_account_pwd;
    private String web_id;
    private String web_pwd;
    private String identity_num;
}
