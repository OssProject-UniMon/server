package dongguk.capstone.backend.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.CardLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import com.baroservice.ws.PagedCardLogEx;
import dongguk.capstone.backend.accountdto.*;
import dongguk.capstone.backend.domain.Account;
import dongguk.capstone.backend.domain.Card;
import dongguk.capstone.backend.repo.AccountRepository;
import dongguk.capstone.backend.repo.CardRepository;
import dongguk.capstone.backend.repo.UserRepository;
import dongguk.capstone.backend.serializable.AccountId;
import dongguk.capstone.backend.serializable.CardId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class LedgerService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final BarobillApiService barobillApiService;

    public LedgerService(UserRepository userRepository, AccountRepository accountRepository, CardRepository cardRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * 계좌 등록 로직
     * @param accountRegistRequestDTO
     * @param userId
     * @return
     */
    public int accountRegist(Long userId, AccountRegistRequestDTO accountRegistRequestDTO) {
        // 먼저 Account 도메인(Entity)를 선언하고, 거기에 AccountRegistRequestDTO 내용 다 넣기
        // 그 다음, 내용을 다 넣은 Account를 AccountRepository에 넣기
        // Account account = new Account(); 이걸 만드는 것이 맞나? 바로빌 API대로 해야되는 것 아닌가?
        // 바로빌 테스트 코드 참고하면서 하자


        // 혹시 이렇게 AccountRegistRequestDTO로 받아온 내용들을,
        // DB에도 저장하고 바로빌 서비스에도 저장해야 되는걸까?
        // 그래야 이렇게 등록하고, 나중에 거래 내역 조회할 때 DB에 내용 들고와서 바로빌의 입출금 내역 조회 API 사용할 수 있으니까..?!
        Account account = new Account();
        AccountId accountId = new AccountId();
        if(userRepository.findById(userId).isPresent()) {
            account.setUser(userRepository.findById(userId).get());
            accountId.setUserId(userId);
            accountId.setBankAccountNum(accountRegistRequestDTO.getBankAccountNum());
            account.setAccountId(accountId);
            account.setBank(accountRegistRequestDTO.getBank());
            account.setBankAccountType(accountRegistRequestDTO.getBankAccountType());
            account.setBankAccountPwd(accountRegistRequestDTO.getBankAccountPwd());
            account.setWebId(accountRegistRequestDTO.getWebId());
            account.setWebPwd(accountRegistRequestDTO.getWebPwd());
            account.setIdentityNum(accountRegistRequestDTO.getIdentityNum());
            accountRepository.save(account);

//          int result = barobill.RegistBankAccount("연동인증키", "사업자번호", "수집주기", "은행코드", "계좌유형", "계좌번호", ...)
            return barobillApiService.bankAccount.registBankAccount("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "MINUTE10",
                    accountRegistRequestDTO.getBank(), accountRegistRequestDTO.getBankAccountType(), accountRegistRequestDTO.getBankAccountNum(), accountRegistRequestDTO.getBankAccountPwd(),
                    accountRegistRequestDTO.getWebId(), accountRegistRequestDTO.getWebPwd(), accountRegistRequestDTO.getIdentityNum(),"","");
        }
        return 0;
    }

    /**
     * 카드 등록 로직
     * @param userId
     * @param cardRegistRequestDTO
     * @return
     */
    public int cardRegist(Long userId, CardRegistRequestDTO cardRegistRequestDTO) {
        Card card = new Card();
        CardId cardId = new CardId();
        if (userRepository.findById(userId).isPresent()){
            card.setUser(userRepository.findById(userId).get());
            cardId.setUserId(userId);
            cardId.setCardNum(cardRegistRequestDTO.getCardNum());
            card.setCardId(cardId);
            card.setCardCompany(cardRegistRequestDTO.getCardCompany());
            card.setCardType(cardRegistRequestDTO.getCardType());
            card.setWebId(cardRegistRequestDTO.getWebId());
            card.setWebPwd(cardRegistRequestDTO.getWebPwd());
            cardRepository.save(card);

            return barobillApiService.card.registCard("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", card.getCardCompany(), card.getCardType(), cardId.getCardNum(),
                    card.getWebId(), card.getWebPwd(), "", "");
        }
        return 0;
    }

    /**
     * 거래 내역 조회 로직
     * 계좌 : 바로빌 계좌 조회 API를 통해 계좌 거래 내역 받아오기
     * 카드 : 계좌 거래 내역과 일시가 같은 거래 내역을 비교하여 상점 카테고리 분류
     * @param userId
     * @return
     */
    public LogsResponseDTO log(Long userId, LogsRequestDTO logsRequestDTO) {
        LogsResponseDTO logsResponseDTO = new LogsResponseDTO(); // 거래 내역 로그 응답
        log.info("logsRequestDTO : {}",logsRequestDTO);
        // 사용자의 계좌 정보 조회
        Optional<Account> accountOptional = accountRepository.findByUserIdAndBankAccountNum(userId,logsRequestDTO.getBankAccountNum());
        // 사용자의 카드 정보 조회
        Optional<Card> cardOptional = cardRepository.findByUserIdAndCardNum(userId, logsRequestDTO.getCardNum());
        log.info("accountOptional : {}",accountOptional);
        log.info("cardOptional : {}",cardOptional);
        if(accountOptional.isPresent() && cardOptional.isPresent()) {
            // 바로빌 API를 사용하여 계좌 조회
            PagedBankAccountLogEx accountLog = barobillApiService.bankAccount.getPeriodBankAccountLogEx("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
                    logsRequestDTO.getBankAccountNum(), logsRequestDTO.getStartDate(), logsRequestDTO.getEndDate(), 10, 1, 2); // 10, 1, 2는 일단 고정, 10은 나중에 바꾸자

            // 바로빌 API를 사용하여 카드 조회
            PagedCardLogEx cardLog = barobillApiService.card.getPeriodCardLogEx("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
                    logsRequestDTO.getCardNum(), logsRequestDTO.getStartDate(), logsRequestDTO.getEndDate(), 10, 1, 2);

            log.info("accountLog : {}",accountLog);
            log.info("cardLog : {}",cardLog);

            List<LogsListDTO> list = new ArrayList<>();

            log.info("accountLog.getCurrentPage() : {}", accountLog.getCurrentPage());
            if (accountLog.getCurrentPage() < 0) {  // 호출 실패
                System.out.println(accountLog.getCurrentPage()); // 나중에 이 호출 실패했을 때의 exception handler 구현하자
            } else {  // 호출 성공
                // 계좌 조회 API의 거래 내역과 카드 조회 API의 상점 분류 로직 구현
                // 모든 계좌 결제 내역과 카드 결제 내역을 고려하여 순서대로 logsListDTO에 추가
                List<CardLogEx> processedCardLogs = new ArrayList<>(); // 처리된 카드 로그를 저장할 리스트
                List<BankAccountLogEx> processedBankAccountLogs = new ArrayList<>(); // 처리된 계좌 로그를 저장할 리스트

                for (BankAccountLogEx bankAccountLogEx : accountLog.getBankAccountLogList().getBankAccountLogEx()) {
                    for (CardLogEx cardLogEx : cardLog.getCardLogList().getCardLogEx()) {
                        // 이미 처리된 카드 로그나 계좌 로그는 건너뜀
                        if (processedCardLogs.contains(cardLogEx) || processedBankAccountLogs.contains(bankAccountLogEx)) {
                            continue;
                        }
                        if (!bankAccountLogEx.getTransType().contains("이체")) {
                            // 여기에 transType 안에 "이체" 라는 단어가 없는 계좌 내역에 한해서 진행 (if문 사용)
                            log.info("bankAccountLogEx.getTransDT() : {}", bankAccountLogEx.getTransDT());
                            log.info("cardLogEx.getUseDT() : {}", cardLogEx.getUseDT());
                            // 계좌 결제 내역과 카드 결제 정보를 순서대로 넣으면 곧 일시 순서대로 넣는다
                            LogsListDTO logsListDTO = getLogsListDTO(bankAccountLogEx, cardLogEx);
                            log.info("logsListDTO : {}", logsListDTO);
                            list.add(logsListDTO);
                            processedCardLogs.add(cardLogEx); // 처리된 카드 로그를 리스트에 추가
                            processedBankAccountLogs.add(bankAccountLogEx); // 처리된 계좌 로그를 리스트에 추가
                        }

                        // 이체, 기타에 대해서는 여기서 else if문으로 처리하기
                    }
                }
            }
            logsResponseDTO.setLogList(list);
        }

        return logsResponseDTO;
    }



//    public AccountLogsResponseDTO log(Long userId, AccountLogsRequestDTO accountLogsRequestDTO) {
//        AccountLogsResponseDTO accountLogsResponseDTO = new AccountLogsResponseDTO();
//        if(accountRepository.findById(userId).isPresent()){
//            Account logAccount = accountRepository.findById(userId).get();
//            PagedBankAccountLogEx result = barobillApiService.bankAccount.getPeriodBankAccountLogEx("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
//                    logAccount.getBankAccountNum(), accountLogsRequestDTO.getStartDate(), accountLogsRequestDTO.getEndDate(), 10, 1, 2); // 10, 1, 2는 일단 고정, 10은 나중에 바꾸자
//
//            List<LogsListDTO> list = new ArrayList<>();
//
//            if (result.getCurrentPage() < 0) {  // 호출 실패
//                System.out.println(result.getCurrentPage()); // 나중에 이 호출 실패했을 때의 exception handler 구현하자
//            } else {  // 호출 성공
//                for (BankAccountLogEx bankAccountLogEx : result.getBankAccountLogList().getBankAccountLogEx()) {
//                    // for-each문으로 logs의 내용 중 필요한 필드만 AccountLogsResponseDTO로 옮긴다.
//                    LogsListDTO logsListDTO = getLogsListDTO(bankAccountLogEx);
//                    list.add(logsListDTO);
//                }
//            }
//
//            accountLogsResponseDTO.setLogList(list);
//            return  accountLogsResponseDTO;
//        }
//        return accountLogsResponseDTO; // userId가 없을 때, 즉 아무것도 없는 accountLogsResponseDTO가 반환된다.
//    }

    // 유지보수 측면에서 특정한 기능을 수행하는 부분을 분리하여 별도의 메소드로 추출하는 것이 코드가 더 간결해지고 의도가 명확해질 수 있다.
    private static LogsListDTO getLogsListDTO(BankAccountLogEx bankAccountLogEx, CardLogEx cardLogEx) {
        LogsListDTO logsListDTO = new LogsListDTO();
        logsListDTO.setDeposit(bankAccountLogEx.getDeposit());
        logsListDTO.setWithdraw(bankAccountLogEx.getWithdraw());
        logsListDTO.setBalance(bankAccountLogEx.getBalance());
        logsListDTO.setDate(bankAccountLogEx.getTransDT());
        logsListDTO.setUseStoreName(cardLogEx.getUseStoreName());
        logsListDTO.setCategory(cardLogEx.getUseStoreBizType()); // 상점 업태
        return logsListDTO;
    }
}