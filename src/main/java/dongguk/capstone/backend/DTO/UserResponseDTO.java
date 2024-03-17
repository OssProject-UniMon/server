package dongguk.capstone.backend.DTO;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
//@Setter
public class UserResponseDTO { // 여기에 일단 User API에 응답으로 보내줄 필드를 전부 설정하자
    private final int serverCode;

    public UserResponseDTO(int serverCode){
        this.serverCode = serverCode;
    }

}
