package dongguk.capstone.backend.Controller;

import dongguk.capstone.backend.DTO.LoginRequestDTO;
import dongguk.capstone.backend.DTO.SignupRequestDTO;
import dongguk.capstone.backend.DTO.UserResponseDTO;
import dongguk.capstone.backend.Repository.UserRepository;
import dongguk.capstone.backend.Service.UserService;
import dongguk.capstone.backend.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // 현재 스프링 데이터 JPA

    // ★ 정상인 경우와 비정상인 경우 모두 JSON 응답을 전송하는 방법은 ResponseEntity를 사용하는 것이다. ★
    // => 정상인 경우와 비정상인 경우 모두 처리하고 싶다면, ResponseEntity를 사용하자!

    // @RestController의 @ResponseBody가 "return하는 객체"를 응답 JSON으로 바꿔주는데,
    // 그 JSON으로 바꿀 "객체"를 " Response'Entity' "를 통해 만들 수 있다.

    // 즉 정리하자면, HTTP 요청을
    // JSON 형식으로 받는다 => @RequestBody 사용
    // URL Path로부터 필요한 데이터를 얻는다 => @PathVariable 사용

    //  ResponseEntity는 3개의 속성을 가진다!!
    //  => msg : 응답 메시지
    //     success : 요청의 성공 여부를 나타내는 boolean 값
    //     data : API가 반환하는 데이터를 저장


    @PostMapping("/user/signup")
    public UserResponseDTO signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO){  // HTTP를 활용한 JSON 형식의 request를 받으려면, @RequestBody가 필요하다!!!
        // User user = userService.save(signupRequestDTO);
        userService.save(signupRequestDTO);
        return new UserResponseDTO(1);
    }


    @GetMapping("/user/{email}/check") // {email} 처럼 { }를 사용해야 parameter로 인식함
    public UserResponseDTO signupCheck(@PathVariable(name = "email") String email){ // @PathVariable이나 @RequestParam을 사용할 때는 name이나 value 속성을 명시해주어야 한다. 그러지 않으면 IllegalArgumentException 발생
        if(userRepository.findByEmail(email).isEmpty()){  // 입력한 이메일을 가진 User가 DB에 존재하지 않는다면 => 중복된 이메일이 아니므로 사용 가능!
            return new UserResponseDTO(1);
        }else {  // 입력한 이메일을 가진 User가 DB에 존재한다면 => 중복된 이메일이므로 사용 불가능!
            return new UserResponseDTO(0); // ok의 괄호 안에 들어가는 것의 리턴값의 타입이 제너릭의 타입이어야 한다. (여기서는 Boolean 타입)
        }
    }


    @PostMapping("/user/login")
    public UserResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        // 1. loginRequestDTO로 먼저 email이 DB에 있는지 확인 후, 그 email을 이용해 User 객체 생성
        // 2. 그 User 객체 참조변수를 통해 메소드로 DB에서 그 객체의 비밀번호와(참조변수.getPassword) loginRequestDTO의 password가 같은지 확인

        // 로그인 시 사용자가 이메일 또는 비밀번호를 입력하지 않았을 경우 처리 (isBlank()를 통해 모든 빈 칸 처리 가능)
        if(loginRequestDTO.getEmail().isBlank()||loginRequestDTO.getPassword().isBlank()){
            return new UserResponseDTO(2);
        }

        // 로그인 시 사용자가 이메일 또는 비밀번호를 전부 입력했을 경우
        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getEmail());
        if(user.isPresent()){    // 만약 입력받은 email을 가진 User가 DB에 존재한다면
            if(loginRequestDTO.getPassword().equals(user.get().getPassword())){  // 그 User의 DB에 있는 비밀번호와 입력한 비밀번호를 비교
                return new UserResponseDTO(1); // 바꾸기
            }else{  // 비밀번호 잘못 입력한 경우
                return new UserResponseDTO(0); // 바꾸기
            }
        }else{  // 이메일이 DB에 없을 경우(이메일 잘못 입력한 경우)
            return new UserResponseDTO(0); // 바꾸기
        }
    }
}