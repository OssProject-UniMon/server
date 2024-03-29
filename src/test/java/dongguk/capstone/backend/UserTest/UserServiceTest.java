package dongguk.capstone.backend.UserTest;

import dongguk.capstone.backend.DTO.SignupRequestDTO;
import dongguk.capstone.backend.Repository.UserRepository;
import dongguk.capstone.backend.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void signup(){
        // given
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
        signupRequestDTO.setEmail("mmm");

        // when
        // 여기서 그냥 mmm을 넣으려고 할 때 existByEmail이 잘 실행되는지 확인..!
        userService.save(signupRequestDTO);
        Boolean b = userRepository.findByEmail("mmm@naver.com").isPresent();

        // then
        assertThat(b).isTrue();
    }

}
