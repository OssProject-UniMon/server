package dongguk.capstone.backend.controller;

import dongguk.capstone.backend.userdto.LoginRequestDTO;
import dongguk.capstone.backend.userdto.LoginResponseDTO;
import dongguk.capstone.backend.userdto.SignupRequestDTO;
import dongguk.capstone.backend.userdto.SignupResponseDTO;
import dongguk.capstone.backend.repo.UserRepository;
import dongguk.capstone.backend.service.UserService;
import dongguk.capstone.backend.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")  // 이 Controller에서 사용할 API 엔드포인트들의 공통된 URL을 나타내는 어노테이션
@Slf4j // 로그 사용 가능
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

    @PostMapping("/signup")
    public SignupResponseDTO signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO){  // HTTP를 활용한 JSON 형식의 request를 받으려면, @RequestBody가 필요하다!!!
        // User user = userService.save(signupRequestDTO);
        userService.save(signupRequestDTO);
        // 이게 된다면, BackendAdvice에도 ResponseEntity 적용하자
        return new SignupResponseDTO(1);
    }

    @GetMapping("/{email}/check")
    public SignupResponseDTO signupCheck(@PathVariable(name = "email") String email){
        HttpHeaders httpHeaders = new HttpHeaders();
        if(userRepository.findByEmail(email).isEmpty()){
            return new SignupResponseDTO(1);
        }else{
            return new SignupResponseDTO(0);
        }
    }


    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        // 1. loginRequestDTO로 먼저 email이 DB에 있는지 확인 후, 그 email을 이용해 User 객체 생성
        // 2. 그 User 객체 참조변수를 통해 메소드로 DB에서 그 객체의 비밀번호와(참조변수.getPassword) loginRequestDTO의 password가 같은지 확인

        // 로그인 시 사용자가 이메일 또는 비밀번호를 입력하지 않았을 경우 처리 (isBlank()를 통해 모든 빈 칸 처리 가능)
        if(loginRequestDTO.getEmail().isBlank()||loginRequestDTO.getPassword().isBlank()){
            return new LoginResponseDTO(2,null);
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        // 로그인 시 사용자가 이메일 또는 비밀번호를 전부 입력했을 경우
        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getEmail());
        if(user.isPresent()){    // 만약 입력받은 email을 가진 User가 DB에 존재한다면
            log.info(user.get().getEmail()); // 그 User의 이메일 로그로 찍어보기
            if(loginRequestDTO.getPassword().equals(user.get().getPassword())){  // 그 User의 DB에 있는 비밀번호와 입력한 비밀번호를 비교
                return new LoginResponseDTO(1, user.get().getUser_id()); // 바꾸기
            }else{  // 비밀번호 잘못 입력한 경우
                return new LoginResponseDTO(0, null); // 바꾸기
            }
        }else{  // 이메일이 DB에 없을 경우(이메일 잘못 입력한 경우)
            return new LoginResponseDTO(0, null); // 바꾸기
        }
    }

//    @PostMapping("/signup")
//    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO){  // HTTP를 활용한 JSON 형식의 request를 받으려면, @RequestBody가 필요하다!!!
//        // User user = userService.save(signupRequestDTO);
//        userService.save(signupRequestDTO);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        // 이게 된다면, BackendAdvice에도 ResponseEntity 적용하자
//        return ResponseEntity.ok(new UserResponseDTO(1));
//    }
//
//    @GetMapping("/{email}/check")
//    public ResponseEntity<UserResponseDTO> signupCheck(@PathVariable(name = "email") String email){
//        HttpHeaders httpHeaders = new HttpHeaders();
//        if(userRepository.findByEmail(email).isEmpty()){
//            return ResponseEntity.ok(new UserResponseDTO(1));
//        }else{
//            return ResponseEntity.badRequest()
//                    .headers(httpHeaders)
//                    .body(new UserResponseDTO(0));
//        }
//    }
//
//
//    @PostMapping("/login")
//    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
//        // 1. loginRequestDTO로 먼저 email이 DB에 있는지 확인 후, 그 email을 이용해 User 객체 생성
//        // 2. 그 User 객체 참조변수를 통해 메소드로 DB에서 그 객체의 비밀번호와(참조변수.getPassword) loginRequestDTO의 password가 같은지 확인
//
//        // 로그인 시 사용자가 이메일 또는 비밀번호를 입력하지 않았을 경우 처리 (isBlank()를 통해 모든 빈 칸 처리 가능)
//        if(loginRequestDTO.getEmail().isBlank()||loginRequestDTO.getPassword().isBlank()){
//            return ResponseEntity.badRequest()
//                    .body(new UserResponseDTO(2));
//        }
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//
//        // 로그인 시 사용자가 이메일 또는 비밀번호를 전부 입력했을 경우
//        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getEmail());
//        if(user.isPresent()){    // 만약 입력받은 email을 가진 User가 DB에 존재한다면
//            log.info(user.get().getEmail()); // 그 User의 이메일 로그로 찍어보기
//            if(loginRequestDTO.getPassword().equals(user.get().getPassword())){  // 그 User의 DB에 있는 비밀번호와 입력한 비밀번호를 비교
//                return ResponseEntity.ok(new UserResponseDTO(1)); // 바꾸기
//            }else{  // 비밀번호 잘못 입력한 경우
//                return ResponseEntity.badRequest()
//                        .body(new UserResponseDTO(0)); // 바꾸기
//            }
//        }else{  // 이메일이 DB에 없을 경우(이메일 잘못 입력한 경우)
//            return ResponseEntity.badRequest()
//                    .body(new UserResponseDTO(0)); // 바꾸기
//        }
//    }
}