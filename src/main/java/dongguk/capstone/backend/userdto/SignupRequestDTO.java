package dongguk.capstone.backend.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

//@Getter
//@Setter
//@ToString
@Data  // @Data 어노테이션 사용하면 위에 어노테이션 전부 포함되어 있어서 생략 가능
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {  // 회원 가입의 request DTO

    // @NotBlank를 사용해서 오류가 발생했을 때,
    // ExceptionHandler를 사용하는 클래스를 만들어서 JSON 형태로 HTTP 응답을 보내자

    @NotBlank(message = "이메일을 입력해주세요") // validation 의존성에 포함된 애노테이션
    // 왜 그냥 @NotBlank만 붙이면 실행이 안되지 ,,? => Controller에 @Valid를 안붙여줬기 때문..!
    // MethodArgumentNotValidException 실행됨
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    // MethodArgumentNotValidException 실행됨
    private String email; // 이메일

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password; // 비밀번호

    @NotBlank(message = "이름을 입력해주세요")
    private String nickname; // 사용자 이름

    @NotBlank(message = "전공을 입력해주세요")
    private String major; // 사용자 전공

    // @NotBlank가 int나 double에는 적용이 안된다..?
//    @NotBlank(message = "학점을 입력해주세요")
    private double grade; // 사용자 학점

    @NotBlank(message = "성별을 입력해주세요")
    private String gender; // 사용자 성별, 남자 = 1 여자 = 2 이렇게 저장할 것

    @NotBlank(message = "소득 분위를 입력해주세요")
    private String income_bracket; // 사용자 소득 분위

//    @NotBlank(message = "장학금 수혜 여부를 입력해주세요")
    private int scholarship_status; // 사용자 장학금 수혜 여부, 받으면 1 안받으면 0

    @NotBlank(message = "거주지를 입력해주세요")
    private String district; // 사용자 거주지 (구)
}
