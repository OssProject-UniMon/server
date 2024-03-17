package dongguk.capstone.backend.Controller;

import dongguk.capstone.backend.DTO.SignupRequestDTO;
import dongguk.capstone.backend.Repository.UserRepository;
import dongguk.capstone.backend.Service.UserService;
import dongguk.capstone.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController // 스프링 MVC 패턴에서 JSON 형식으로 데이터를 응답하기 위해 @Controller 대신 @RestController 사용
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // 현재 스프링 데이터 JPA

    // ★ 정상인 경우와 비정상인 경우 모두 JSON 응답을 전송하는 방법은 ResponseEntity를 사용하는 것이다. ★
    // => 정상인 경우와 비정상인 경우 모두 처리하고 싶다면, ResponseEntity를 사용하자!


    @PostMapping("/user/signup")
//    @ResponseBody  // @RestController를 사용하면 @ResponseBody가 알아서 추가된 것이기 때문에, 굳이 사용할 필요가 없다.
    public User signup(@RequestBody SignupRequestDTO signupRequestDTO){  // HTTP를 활용한 JSON 형식의 request를 받으려면, @RequestBody가 필요하다!!!!
        // 이렇게 @RequestBody를 사용하면, JSON 형식의 HTTP 요청 데이터(클라이언트)를 매개변수(여기서는 SignupDTO)로 변환하여 받아준다.
        User user = userService.save(signupRequestDTO);
        return user; // UserDAO를 이용해서 User를 만든 뒤, 여기에 User 참조변수 넣어서 User 객체 리턴해주자
        // 여기에 반환값을 user(User user)로 하면, @RestController의 @ResponseBody가 객체(여기서는 User)를 JSON 형식으로 변환해서 그 JSON을 응답으로 전송해줄 것임
    }


    @GetMapping("/user/{email}/check") // {email} 처럼 { }를 사용해야 parameter로 인식함
    public ResponseEntity<Boolean> signupCheck(@PathVariable(name = "email") String email){ // @PathVariable이나 @RequestParam을 사용할 때는 name이나 value 속성을 명시해주어야 한다. 그러지 않으면 IllegalArgumentException 발생
        return ResponseEntity.ok(userService.existsByEmail(email)); // ok의 괄호 안에 들어가는 것의 리턴값의 타입이 제너릭의 타입이어야 한다. (여기서는 Boolean 타입)
    }

    // @RestController의 @ResponseBody가 "return하는 객체"를 응답 JSON으로 바꿔주는데,
    // 그 JSON으로 바꿀 "객체"를 " Response'Entity' "를 통해 만들 수 있다.

    // 즉 정리하자면, HTTP 요청을
    // JSON 형식으로 받는다 => @RequestBody 사용
    // URL Path로부터 필요한 데이터를 얻는다 => @PathVariable 사용

    //  ResponseEntity는 3개의 속성을 가진다!!
    //  => msg : 응답 메시지
    //     success : 요청의 성공 여부를 나타내는 boolean 값
    //     data : API가 반환하는 데이터를 저장
}