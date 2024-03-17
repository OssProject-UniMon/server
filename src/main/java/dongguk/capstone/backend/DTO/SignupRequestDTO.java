package dongguk.capstone.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {  // 회원 가입의 request DTO
//    private int userId; // 이건 사용자로부터 입력받는 것이 아니다.
//    private String id; // 이게 name 대용? => nickname으로 어떤가?

    @NotBlank(message = "이메일을 입력해주세요") // validation 의존성에 포함된 애노테이션
    // 왜 안되지 ,,?
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    private String email; // 이메일

    private String password; // 비밀번호
    private String passwordConfirm; // 비밀번호 확인
    private String name; // 사용자 이름
    private String major; // 사용자 전공
    private double grade; // 사용자 학점
    private int gender; // 사용자 성별, 남자 = 1 여자 = 2 이렇게 저장할 것
    private int income_bracket; // 사용자 소득 분위
    private int scholarship_status; // 사용자 장학금 수혜 여부, 받으면 1 안받으면 0
    private String district; // 사용자 거주지 (구)
}
