package dongguk.capstone.backend.userdto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponseDTO {
    private final int serverCode;
    private final Long userId;
    private final String nickname;
    private final int accountStatus;
    private final int cardStatus;
}
