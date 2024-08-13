package dongguk.capstone.backend.usertest;

import dongguk.capstone.backend.user.dto.request.SignupReqDTO;
import dongguk.capstone.backend.user.repository.UserRepository;
import dongguk.capstone.backend.user.service.UserService;
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
        SignupReqDTO signupReqDTO = new SignupReqDTO();
        signupReqDTO.setEmail("mmm");

        // when
        userService.save(signupReqDTO);
        Boolean b = userRepository.findByEmail("mmm@naver.com").isPresent();

        // then
        assertThat(b).isTrue();
    }

}
