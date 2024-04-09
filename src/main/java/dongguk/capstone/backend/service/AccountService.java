package dongguk.capstone.backend.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import dongguk.capstone.backend.accountdto.AccountLogsRequestDTO;
import dongguk.capstone.backend.accountdto.AccountLogsResponseDTO;
import dongguk.capstone.backend.accountdto.AccountRegistRequestDTO;
import dongguk.capstone.backend.accountdto.LogsListDTO;
import dongguk.capstone.backend.domain.Account;
import dongguk.capstone.backend.repo.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final BarobillApiService barobillApiService;

    public AccountService(AccountRepository accountRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
        this.accountRepository = accountRepository;
    }

    /**
     * 계좌 등록 로직
     * @param accountRegistRequestDTO
     * @param userId
     * @return
     */
    public int regist(AccountRegistRequestDTO accountRegistRequestDTO, Long userId) {
        // 먼저 Account 도메인(Entity)를 선언하고, 거기에 AccountRegistRequestDTO 내용 다 넣기
        // 그 다음, 내용을 다 넣은 Account를 AccountRepository에 넣기
        // Account account = new Account(); 이걸 만드는 것이 맞나? 바로빌 API대로 해야되는 것 아닌가?
        // 바로빌 테스트 코드 참고하면서 하자


        // 혹시 이렇게 AccountRegistRequestDTO로 받아온 내용들을,
        // DB에도 저장하고 바로빌 서비스에도 저장해야 되는걸까?
        // 그래야 이렇게 등록하고, 나중에 거래 내역 조회할 때 DB에 내용 들고와서 바로빌의 입출금 내역 조회 API 사용할 수 있으니까..?!

        Account account = new Account();
        account.setUser_id(userId);
        account.setBankAccountNum(accountRegistRequestDTO.getBank_account_num());
        account.setBank(accountRegistRequestDTO.getBank());
        account.setBankAccountType(accountRegistRequestDTO.getBank_account_type());
        account.setBankAccountPwd(accountRegistRequestDTO.getBank_account_pwd());
        account.setWebId(accountRegistRequestDTO.getWeb_id());
        account.setWebPwd(accountRegistRequestDTO.getWeb_pwd());
        account.setIdentityNum(accountRegistRequestDTO.getIdentity_num());
        accountRepository.save(account);

//        int result = barobill.RegistBankAccount("연동인증키", "사업자번호", "수집주기", "은행코드", "계좌유형", "계좌번호", ...)
        int result = barobillApiService.bankAccount.registBankAccount("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "MINUTE10",
                accountRegistRequestDTO.getBank(), accountRegistRequestDTO.getBank_account_type(), accountRegistRequestDTO.getBank_account_num(), accountRegistRequestDTO.getBank_account_pwd(),
                accountRegistRequestDTO.getWeb_id(), accountRegistRequestDTO.getWeb_pwd(), accountRegistRequestDTO.getIdentity_num(),"","");
        return result;
    }

    /**
     * 거래 내역 조회 로직
     * @param userId
     * @return
     */
    public AccountLogsResponseDTO log(Long userId, AccountLogsRequestDTO accountLogsRequestDTO) {
        AccountLogsResponseDTO accountLogsResponseDTO = new AccountLogsResponseDTO();
        if(accountRepository.findById(userId).isPresent()){
            Account logAccount = accountRepository.findById(userId).get();
            PagedBankAccountLogEx result = barobillApiService.bankAccount.getPeriodBankAccountLogEx("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
                    logAccount.getBankAccountNum(), accountLogsRequestDTO.getStartDate(), accountLogsRequestDTO.getEndDate(), 10, 1, 2); // 10, 1, 2는 일단 고정, 10은 나중에 바꾸자

            List<LogsListDTO> list = new ArrayList<>();

            if (result.getCurrentPage() < 0) {  // 호출 실패
                System.out.println(result.getCurrentPage()); // 나중에 이 호출 실패했을 때의 exception handler 구현하자
            } else {  // 호출 성공
                for (BankAccountLogEx bankAccountLogEx : result.getBankAccountLogList().getBankAccountLogEx()) {
                    // for-each문으로 logs의 내용 중 필요한 필드만 AccountLogsResponseDTO로 옮긴다.
                    LogsListDTO logsListDTO = getLogsListDTO(bankAccountLogEx);
                    list.add(logsListDTO);
                }
            }

            accountLogsResponseDTO.setLogList(list);
            return  accountLogsResponseDTO;
        }
        return accountLogsResponseDTO; // userId가 없을 때, 즉 아무것도 없는 accountLogsResponseDTO가 반환된다.
    }

    // 유지보수 측면에서 특정한 기능을 수행하는 부분을 분리하여 별도의 메소드로 추출하는 것이 코드가 더 간결해지고 의도가 명확해질 수 있다.
    private static LogsListDTO getLogsListDTO(BankAccountLogEx bankAccountLogEx) {
        LogsListDTO logsListDTO = new LogsListDTO();
        logsListDTO.setDeposit(bankAccountLogEx.getDeposit());
        logsListDTO.setWithdraw(bankAccountLogEx.getWithdraw());
        logsListDTO.setBalance(bankAccountLogEx.getBalance());
        logsListDTO.setTransDt(bankAccountLogEx.getTransDT());
        logsListDTO.setTransType(bankAccountLogEx.getTransType());
//        logsListDTO.setTransOffice(bankAccountLogEx.getTransOffice());
        logsListDTO.setTransRemark(bankAccountLogEx.getTransRemark());
        return logsListDTO;
    }
}