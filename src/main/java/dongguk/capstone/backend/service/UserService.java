package dongguk.capstone.backend.service;

import dongguk.capstone.backend.userdto.SignupRequestDTO;
import dongguk.capstone.backend.repo.UserRepository;
import dongguk.capstone.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // JPA를 사용할 때는 모든 작업을 트랜잭션 안에서 실행해야 하므로 @Transactional 애노테이션 추가 (주로 서비스 계층에서 작업이 이루어지므로, 서비스에 추가하는 것)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원 가입 로직
     * @param signupRequestDTO
     * @return
     */
    // DTO로 얻은 데이터 값들을 Entity로 전달하는 작업은 비즈니스 로직이므로 Service에서 이루어짐.
    public User save(SignupRequestDTO signupRequestDTO) { // 여기서 DTO가 아니라 user를 해야될 듯
        User user = new User();
        // 근데 여기에 if문으로 조건이 붙어야 되지 않을까? 예를 들어 이메일 확인 되면 이 밑에 애들이 진행되게끔..!
        user.setNickname(signupRequestDTO.getNickname());
        user.setEmail(signupRequestDTO.getEmail());
        user.setPassword(signupRequestDTO.getPassword());
        user.setMajor(signupRequestDTO.getMajor());
        user.setGrade(signupRequestDTO.getGrade());
        user.setGender(signupRequestDTO.getGender());
        user.setIncomeBracket(signupRequestDTO.getIncome_bracket());
        user.setScholarshipStatus(signupRequestDTO.getScholarship_status());
        user.setDistrict(signupRequestDTO.getDistrict());
        userRepository.save(user);
        // if문 여기까지
        // 이메일 확인이 잘못되는 등 잘못된 것이 있으면 여기에 else로 처리
        // 그리고 뭐 하나라도 입력 안했을 때도 처리해야 됨!!!!

        return user;
    }
}
