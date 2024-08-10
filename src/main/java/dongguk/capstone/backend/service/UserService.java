package dongguk.capstone.backend.service;

import dongguk.capstone.backend.domain.Account;
import dongguk.capstone.backend.domain.Card;
import dongguk.capstone.backend.repository.AccountRepository;
import dongguk.capstone.backend.repository.CardRepository;
import dongguk.capstone.backend.userdto.LoginRequestDTO;
import dongguk.capstone.backend.userdto.LoginResponseDTO;
import dongguk.capstone.backend.userdto.SignupRequestDTO;
import dongguk.capstone.backend.repository.UserRepository;
import dongguk.capstone.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // JPA를 사용할 때는 모든 작업을 트랜잭션 안에서 실행해야 하므로 @Transactional 애노테이션 추가 (주로 서비스 계층에서 작업이 이루어지므로, 서비스에 추가하는 것)
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    /**
     * 회원 가입 로직
     * @param signupRequestDTO
     * @return
     */
    // DTO로 얻은 데이터 값들을 Entity로 전달하는 작업은 비즈니스 로직이므로 Service에서 이루어짐.
    public User save(SignupRequestDTO signupRequestDTO) {
        User user = new User();
        user.setNickname(signupRequestDTO.getNickname());
        user.setEmail(signupRequestDTO.getEmail());
        user.setPassword(signupRequestDTO.getPassword());
        user.setMajor(signupRequestDTO.getMajor());
        user.setGrade(signupRequestDTO.getGrade());
        user.setGender(signupRequestDTO.getGender());
        user.setIncomeBracket(signupRequestDTO.getIncome_bracket());
        user.setScholarshipStatus(signupRequestDTO.getScholarship_status());
        user.setDistrict(signupRequestDTO.getDistrict());
        user.setAccountStatus(0); // 회원가입 시에는 계좌가 등록되어 있지 않은 상태이므로 0
        user.setCardStatus(0); // 회원가입 시에는 카드가 등록되어 있지 않은 상태이므로 0
        userRepository.save(user);
        return user;
    }

    /**
     * 로그인 로직
     * @param loginRequestDTO
     * @return
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getEmail());

        if (user.isPresent()) {
            if (loginRequestDTO.getPassword().equals(user.get().getPassword())) { // 입력한 비밀번호가 이메일에 대응되는 비밀번호와 맞을 경우
                Long userId = user.get().getUserId();

                // Account와 Card 확인
                Optional<Account> account = accountRepository.findByUserId(userId);
                Optional<Card> card = cardRepository.findByUserId(userId);

                user.get().setAccountStatus(account.isPresent()? 1 : 0);
                user.get().setCardStatus(card.isPresent()? 1 : 0);
                userRepository.save(user.get()); // 상태 업데이트 후 저장
                if(account.isPresent() && card.isPresent()){ // 계좌와 카드 등록을 이미 했을 경우
                    return new LoginResponseDTO(1, userId, user.get().getNickname(), user.get().getAccountStatus(), user.get().getCardStatus(),
                            account.get().getAccountEmbedded().getBankAccountNum());
                } else { // 계좌와 카드 등록이 안되어 있을 경우
                    return new LoginResponseDTO(1, userId, user.get().getNickname(), user.get().getAccountStatus(), user.get().getCardStatus(),
                            null);
                }
            } else { // 비밀번호가 틀렸을 경우
                return new LoginResponseDTO(0, null, null, 0, 0, null);
            }
        } else { // 이메일이 틀렸을 경우
            return new LoginResponseDTO(0, null, null, 0, 0, null);
        }
    }
}
