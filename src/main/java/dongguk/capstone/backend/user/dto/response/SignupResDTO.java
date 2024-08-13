package dongguk.capstone.backend.user.dto.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SignupResDTO {  // 여기에 일단 User API에 응답으로 보내줄 필드를 전부 설정하자

    // 그래서 나중에 응답으로 줘야할 것 추가해서 생기면 여기에 추가해야 할 듯..!
    // 여기에 status랑 success 여부(true/false), message , body에 들어갈 data 설정해두자!!!!!!!!!!
    // 이게 맞나..?

    private final int serverCode;
//
//    private int status;
//    private String message;
//    private boolean success;

}