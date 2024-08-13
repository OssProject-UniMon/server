package dongguk.capstone.backend.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResDTO {
    private final int serverCode;
    private final Long userId;
    private final String nickname;
    private final int accountStatus;
    private final int cardStatus;
    private final String bankAccountNum;
}
