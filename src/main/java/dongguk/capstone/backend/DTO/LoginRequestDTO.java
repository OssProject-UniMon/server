package dongguk.capstone.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @Data 어노테이션은 @Getter/@Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor를 합쳐놓은 종합 선물 세트
// 다만 callSuper, includeFieldName, exclude와 같은 어노테이션 parameter를 지정할 수는 없어서,
// 해당 parameter 사용이 필요하다면 그 어노테이션을 개별적으로 따로 명시해주면 된다.
public class LoginRequestDTO {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
