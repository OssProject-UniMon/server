package dongguk.capstone.backend.user.service;

import dongguk.capstone.backend.account.entity.Account;
import dongguk.capstone.backend.card.entity.Card;
import dongguk.capstone.backend.account.repository.AccountRepository;
import dongguk.capstone.backend.card.repository.CardRepository;
import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import dongguk.capstone.backend.monthlyaggregation.repository.MonthlyAggregationRepository;
import dongguk.capstone.backend.user.dto.request.LoginReqDTO;
import dongguk.capstone.backend.user.dto.response.LoginResDTO;
import dongguk.capstone.backend.user.dto.request.SignupReqDTO;
import dongguk.capstone.backend.user.repository.UserRepository;
import dongguk.capstone.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
//@Transactional // JPA를 사용할 때는 모든 작업을 트랜잭션 안에서 실행해야 하므로 @Transactional 애노테이션 추가 (주로 서비스 계층에서 작업이 이루어지므로, 서비스에 추가하는 것)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;

    /**
     * 회원 가입 로직
     * @param signupReqDTO
     * @return
     */
    // DTO로 얻은 데이터 값들을 Entity로 전달하는 작업은 비즈니스 로직이므로 Service에서 이루어짐.
    @Override
    @Transactional
    public User save(SignupReqDTO signupReqDTO) {
        User user = User.builder()
                .nickname(signupReqDTO.getNickname())
                .email(signupReqDTO.getEmail())
                .password(signupReqDTO.getPassword())
                .major(signupReqDTO.getMajor())
                .grade(signupReqDTO.getGrade())
                .gender(signupReqDTO.getGender())
                .incomeBracket(signupReqDTO.getIncome_bracket())
                .scholarshipStatus(signupReqDTO.getScholarship_status())
                .district(signupReqDTO.getDistrict())
                .accountStatus(0)  // 회원가입 시 계좌 상태를 0으로 설정
                .cardStatus(0)     // 회원가입 시 카드 상태를 0으로 설정
                .build();

        userRepository.save(user);
        return user;
    }


    /**
     * 로그인 로직
     * @param loginReqDTO
     * @return
     */
    @Override
    @Transactional
    public LoginResDTO login(LoginReqDTO loginReqDTO) {
        Optional<User> user = userRepository.findByEmail(loginReqDTO.getEmail());

        if (user.isPresent()) {
            if (loginReqDTO.getPassword().equals(user.get().getPassword())) { // 입력한 비밀번호가 이메일에 대응되는 비밀번호와 맞을 경우
                Long userId = user.get().getUserId();

                // Account와 Card 확인
                Optional<Account> account = accountRepository.findByUserId(userId);
                Optional<Card> card = cardRepository.findByUserId(userId);

                user.get().setAccountStatus(account.isPresent()? 1 : 0);
                user.get().setCardStatus(card.isPresent()? 1 : 0);
                userRepository.save(user.get()); // 상태 업데이트 후 저장
                if(account.isPresent() && card.isPresent()){ // 계좌와 카드 등록을 이미 했을 경우
                    return new LoginResDTO(1, userId, user.get().getNickname(), user.get().getAccountStatus(), user.get().getCardStatus(),
                            account.get().getAccountEmbedded().getBankAccountNum());
                } else { // 계좌와 카드 등록이 안되어 있을 경우
                    return new LoginResDTO(1, userId, user.get().getNickname(), user.get().getAccountStatus(), user.get().getCardStatus(),
                            null);
                }
            } else { // 비밀번호가 틀렸을 경우
                return new LoginResDTO(0, null, null, 0, 0, null);
            }
        } else { // 이메일이 틀렸을 경우
            return new LoginResDTO(0, null, null, 0, 0, null);
        }
    }
}
