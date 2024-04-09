package dongguk.capstone.backend.usertest;

import dongguk.capstone.backend.userdto.SignupRequestDTO;
import dongguk.capstone.backend.repo.UserRepository;
import dongguk.capstone.backend.service.UserService;
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
        userService.save(signupRequestDTO);
        Boolean b = userRepository.findByEmail("mmm@naver.com").isPresent();

        // then
        assertThat(b).isTrue();
    }

}
