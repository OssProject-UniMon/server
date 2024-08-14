package dongguk.capstone.backend.user.controller;

import dongguk.capstone.backend.user.dto.request.LoginReqDTO;
import dongguk.capstone.backend.user.dto.response.LoginResDTO;
import dongguk.capstone.backend.user.dto.request.SignupReqDTO;
import dongguk.capstone.backend.user.dto.response.SignupResDTO;
import dongguk.capstone.backend.user.repository.UserRepository;
import dongguk.capstone.backend.user.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")  // 이 Controller에서 사용할 API 엔드포인트들의 공통된 URL을 나타내는 어노테이션
@Slf4j // 로그 사용 가능
public class UserApiController {

    private final UserServiceImpl userServiceImpl;
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
    @Operation(summary = "회원 가입", description = "회원 가입으로 이동합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SignupResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SignupResDTO signup(@Valid @RequestBody SignupReqDTO signupReqDTO){  // HTTP를 활용한 JSON 형식의 request를 받으려면, @RequestBody가 필요하다!!!
        // User user = userService.save(signupRequestDTO);
        userServiceImpl.save(signupReqDTO);
        // 이게 된다면, BackendAdvice에도 ResponseEntity 적용하자
        return new SignupResDTO(1);
    }

    @GetMapping("/{email}/check")
    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인을 진행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SignupResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SignupResDTO signupCheck(@PathVariable(name = "email") String email){
        HttpHeaders httpHeaders = new HttpHeaders();
        if(userRepository.findByEmail(email).isEmpty()){
            return new SignupResDTO(1);
        }else{
            return new SignupResDTO(0);
        }
    }


    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = LoginResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResDTO> login(@RequestBody LoginReqDTO loginReqDTO) {
        LoginResDTO loginResDTO = userServiceImpl.login(loginReqDTO);
        if (loginResDTO.getServerCode() == 1) {
            return ResponseEntity.ok(loginResDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResDTO);
        }
    }
}