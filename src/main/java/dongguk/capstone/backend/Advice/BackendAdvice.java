package dongguk.capstone.backend.Advice;

import dongguk.capstone.backend.DTO.SignupResponseDTO;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// @ControllerAdvice가 @ExceptionHandler가 적용된 메소드들에 AOP를 적용
// => 모든 @Controller에서 전역적으로 발생할 수 있는 예외를 잡아서 처리한다.
// => 따라서 @Controller에서 따로 예외 처리 구문을 만들지 않아도 AOP로 적용되기 때문에 알아서 여기에서 예외 처리가 된다.
public class BackendAdvice {
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<UserResponseDTO> methodArgumentNotValidException(MethodArgumentNotValidException e){
//        return ResponseEntity.badRequest().build(); // 필수적으로 입력해야 할 칸에 입력하지 않았을 경우 serverCode == 2 (임시적으로)
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SignupResponseDTO methodArgumentNotValidException(MethodArgumentNotValidException e){
        return new SignupResponseDTO(2); // 필수적으로 입력해야 할 칸에 입력하지 않았을 경우 serverCode == 2 (임시적으로)
    }

}