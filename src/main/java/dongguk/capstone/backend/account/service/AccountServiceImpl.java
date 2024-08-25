package dongguk.capstone.backend.account.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import dongguk.capstone.backend.account.dto.request.AccountReqRegistDTO;
import dongguk.capstone.backend.account.entity.Account;
import dongguk.capstone.backend.account.repository.AccountRepository;
import dongguk.capstone.backend.account.entity.AccountEmbedded;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BarobillApiService barobillApiService;

    @Autowired
    public AccountServiceImpl(UserRepository userRepository, AccountRepository accountRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.RELEASE);
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * 계좌 등록 로직
     * @param accountReqRegistDTO
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public int accountRegist(Long userId, AccountReqRegistDTO accountReqRegistDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        AccountEmbedded accountEmbedded = AccountEmbedded.builder()
                .userId(userId)
                .bankAccountNum(accountReqRegistDTO.getBankAccountNum())
                .build();

        Account account = Account.builder()
                .user(user)
                .accountEmbedded(accountEmbedded)
                .bank(accountReqRegistDTO.getBank())
                .bankAccountType(accountReqRegistDTO.getBankAccountType())
                .bankAccountPwd(accountReqRegistDTO.getBankAccountPwd())
                .webId(accountReqRegistDTO.getWebId())
                .webPwd(accountReqRegistDTO.getWebPwd())
                .identityNum(accountReqRegistDTO.getIdentityNum())
                .build();

        accountRepository.save(account);


        return barobillApiService.bankAccount.registBankAccount("181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", "DAY1",
                accountReqRegistDTO.getBank(), accountReqRegistDTO.getBankAccountType(), accountReqRegistDTO.getBankAccountNum(),
                accountReqRegistDTO.getBankAccountPwd(), accountReqRegistDTO.getWebId(),
                accountReqRegistDTO.getWebPwd(), accountReqRegistDTO.getIdentityNum(),"","");
    }
}
