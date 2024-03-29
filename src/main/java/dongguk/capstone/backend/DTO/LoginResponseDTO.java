package dongguk.capstone.backend.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponseDTO {
    private final int serverCode;
    private final Long userId;
}
