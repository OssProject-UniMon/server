package dongguk.capstone.backend.advice;

import dongguk.capstone.backend.user.dto.response.SignupResDTO;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// @ControllerAdvice가 @ExceptionHandler가 적용된 메소드들에 AOP를 적용
// => 모든 @Controller에서 전역적으로 발생할 수 있는 예외를 잡아서 처리한다.
// => 따라서 @Controller에서 따로 예외 처리 구문을 만들지 않아도 AOP로 적용되기 때문에 알아서 여기에서 예외 처리가 된다.
public class BackendAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SignupResDTO methodArgumentNotValidException(MethodArgumentNotValidException e){
        return new SignupResDTO(0); // 필수적으로 입력해야 할 칸에 입력하지 않았을 경우 serverCode == 0 (임시적으로)
    }

}