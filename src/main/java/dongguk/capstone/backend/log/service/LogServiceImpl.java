package dongguk.capstone.backend.log.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.CardLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import com.baroservice.ws.PagedCardLogEx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dongguk.capstone.backend.account.entity.Account;
import dongguk.capstone.backend.account.repository.AccountRepository;
import dongguk.capstone.backend.card.entity.Card;
import dongguk.capstone.backend.card.repository.CardRepository;
import dongguk.capstone.backend.log.dto.LogsListDTO;
import dongguk.capstone.backend.log.dto.response.LogsResDTO;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.serializable.LogEmbedded;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LogServiceImpl implements LogService{

//    @Value("${openai.api.key}")
//    private String apikey; // 연동할 chat gpt assistant api의 api key

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final LogRepository logRepository;
    private final BarobillApiService barobillApiService;

    private static final String LEDGER_FLASK_SERVER_URL = "http://13.124.16.179:5000/classify";


    public LogServiceImpl(UserRepository userRepository, AccountRepository accountRepository, CardRepository cardRepository, LogRepository logRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.RELEASE);
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.logRepository = logRepository;
    }

    public static String classifyTransaction(String useStoreName, String category) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String transactionDetails = "가게명 : " + useStoreName + " 업종 : " + category;
            log.info("transactionDetails : {}",transactionDetails);

            // Create the JSON payload
            String json = mapper.writeValueAsString(Map.of("transaction_details", transactionDetails));

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(LEDGER_FLASK_SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = mapper.readTree(response.body());
                log.info("responseJson : {}",responseJson);
                return responseJson.path("result").asText();
            } else {
                log.error("Failed to classify transaction: {}", response.body());
                throw new RuntimeException("Failed to classify transaction: " + response.body());
            }
        } catch (Exception e) {
            log.error("Exception occurred while classifying transaction: {}", e.getMessage());
            throw new RuntimeException("Exception occurred while classifying transaction", e);
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행, cron(크론) 표현식은 분, 시간, 날짜, 월, 요일, 년도 순서대로 필드를 가지며, 각 필드는 공백으로 구분
    public void fetchAndSaveLogs(){
        List<User> users = userRepository.findAll();
        log.info("users : {}",users);
        Account account = new Account();
        Card card = new Card();
        for(User user : users){
            List<Account> accounts = accountRepository.findAll();
            List<Card> cards = cardRepository.findAll();

            log.info("accounts : {}",accounts);
            log.info("cards : {}",cards);

            // 오늘 날짜를 가져옴
            LocalDate today = LocalDate.now();
            log.info("today : {}",today);

            // 시작일은 오늘로부터 3달 전으로 설정
            LocalDate startDate = today.minusMonths(3);
            log.info("startDate : {}",startDate);

            // 종료일은 오늘 날짜로 설정
            log.info("endDate : {}", today);

            // 시작일을 LocalDateTime으로 변환하고 시간을 00:00:00으로 설정
            LocalDateTime startDateTime = startDate.atStartOfDay();
            log.info("startDateTime : {}",startDateTime);

            // 종료일을 LocalDateTime으로 변환하고 시간을 23:59:59로 설정
            LocalDateTime endDateTime = today.atTime(23, 59, 59);
            log.info("startDateTime : {}",startDateTime);

            String startDateString = startDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateString = endDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));


            // 포맷터를 사용하여 LocalDateTime을 String으로 변환
            log.info("startDateString : {}",startDate);
            log.info("endDateString : {}", today);

            for(Account acc : accounts){
                if(user.getUserId().equals(acc.getUser().getUserId())){
                    account = acc;
                }
            }
            for(Card c : cards){
                if(user.getUserId().equals(c.getUser().getUserId())){
                    card = c;
                }
            }

            // 기존 로그 삭제
            logRepository.deleteByLogEmbeddedUserId(user.getUserId());

            try {
                // 바로빌 API를 사용하여 계좌 조회
                PagedBankAccountLogEx accountLog = barobillApiService.bankAccount.getPeriodBankAccountLogEx(
                        "181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", "capstone11",
                        account.getAccountEmbedded().getBankAccountNum(), startDateString, endDateString, 100, 1, 2);

                log.info("accountLog : {}",accountLog);

                // 바로빌 API를 사용하여 카드 조회
                PagedCardLogEx cardLog = barobillApiService.card.getPeriodCardLogEx(
                        "181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", "capstone11",
                        card.getCardEmbedded().getCardNum(), startDateString, endDateString, 100, 1, 2);

                log.info("cardLog : {}",cardLog);

                log.info("accountLog.getCurrentPage() : {}",accountLog.getCurrentPage());

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
                                LogsListDTO logsListDTO = getLogsListDTO(bankAccountLogEx, cardLogEx); // 여기서 IOException이 발생할 수 있기 때문에 try-catch문 사용
                                // 2. LogsListDTO를 Log 엔티티로 매핑하여 저장
                                Log logEntity = mapToLogEntity(user.getUserId(),cardLogEx.getCardNum(),logsListDTO);
                                logRepository.save(logEntity); // LogRepository를 통해 저장
                                processedCardLogs.add(cardLogEx); // 처리된 카드 로그를 리스트에 추가
                                processedBankAccountLogs.add(bankAccountLogEx); // 처리된 계좌 로그를 리스트에 추가
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("IOException occurred: {}", e.getMessage());
                // IOException이 발생했을 때의 처리를 여기에 추가합니다.
            }
        }
    }


    /**
     * 거래 내역 조회 로직
     * 계좌 : 바로빌 계좌 조회 API를 통해 계좌 거래 내역 받아오기
     * 카드 : 계좌 거래 내역과 일시가 같은 거래 내역을 비교하여 상점 카테고리 분류
     * @param userId
     * @return
     */

    @Override
    @Transactional(readOnly = true)
    public LogsResDTO log(Long userId) {
        LogsResDTO logsResDTO = new LogsResDTO(); // 거래 내역 로그 응답

        List<Log> logs = logRepository.findAll();
        List<LogsListDTO> list = new ArrayList<>();

        log.info("logs : {}", logs);

        for (Log l : logs) {
            if (userId.equals(l.getLogEmbedded().getUserId())) {
                LogsListDTO logsListDTO = new LogsListDTO();
                logsListDTO.setDeposit(l.getDeposit());
                logsListDTO.setWithdraw(l.getWithdraw());
                logsListDTO.setBalance(l.getBalance());
                logsListDTO.setDate(l.getDate());
                logsListDTO.setUseStoreName(l.getUseStoreName());
                logsListDTO.setCategory(l.getCategory());
                list.add(logsListDTO);
            }
        }
        logsResDTO.setLogList(list);
        log.info("logsResponseDTO : {}", logsResDTO);
        return logsResDTO;
    }


    // 유지보수 측면에서 특정한 기능을 수행하는 부분을 분리하여 별도의 메소드로 추출하는 것이 코드가 더 간결해지고 의도가 명확해질 수 있다.
    // static 메소드에서는 인스턴스 필드를 직접 참조할 수 없다. (bizInfoCheckService)
    private LogsListDTO getLogsListDTO(BankAccountLogEx bankAccountLogEx, CardLogEx cardLogEx) throws IOException {
        LogsListDTO logsListDTO = new LogsListDTO();
        logsListDTO.setDeposit(bankAccountLogEx.getDeposit());
        logsListDTO.setWithdraw(bankAccountLogEx.getWithdraw());
        logsListDTO.setBalance(bankAccountLogEx.getBalance());
        logsListDTO.setDate(cardLogEx.getUseDT());
        logsListDTO.setUseStoreName(cardLogEx.getUseStoreName());
        logsListDTO.setUseStoreCorpNum(cardLogEx.getUseStoreCorpNum());
        // 여기서 사업자번호를 URL에 넣고 Jsoup를 통해 보내야 함
        // URL 인코딩
        String encodedCorpNum = URLEncoder.encode(cardLogEx.getUseStoreCorpNum(), "UTF-8");
        log.info("cardLogEx.getUseStoreCorpNum() : {}",cardLogEx.getUseStoreCorpNum());
        String url = "https://bizno.net/article/" + encodedCorpNum;
        // Jsoup를 통해 URL에 접속하고 웹 페이지를 가져옴
        // Jsoup를 사용하여 HTML 문서 파싱
        Document doc = Jsoup.connect(url).get();
        // 선택한 요소에 대한 CSS 선택자를 사용하여 특정 요소 선택
        Element thElement = doc.select("th:contains(종목)").first();
        // 선택한 요소의 형제인 <td> 태그를 선택
        if(thElement==null){
//            logsListDTO.setCategory("기타");
            String flaskCategory = classifyTransaction(cardLogEx.getUseStoreName(), " ");
            log.info("flaskCategory : {}", flaskCategory);
            String category = switch (flaskCategory) {
                case "1" -> "오락";
                case "2" -> "문화";
                case "3" -> "카페";
                case "4" -> "스포츠";
                case "5" -> "음식점";
                case "6" -> "숙박비";
                case "7" -> "잡화소매";
                case "8" -> "쇼핑";
                case "9" -> "개인이체";
                case "10" -> "교통비";
                case "11" -> "의료비";
                case "12" -> "보험비";
                case "13" -> "구독/정기결제";
                case "14" -> "교육비";
                case "15" -> "모바일페이";
                default -> "기타";
            };
            logsListDTO.setCategory(category);
        }else {
            Element tdElement = thElement.nextElementSibling();
            log.info("tdElement : {}", tdElement);
            if (tdElement != null) {
                // <td> 태그의 텍스트 값을 가져옴
                String category = tdElement.text();
                if (category.equals("-")) {
                    logsListDTO.setCategory("기타");
                } else {
                    // Flask 서버 호출
                    try {
                        String flaskCategory = classifyTransaction(cardLogEx.getUseStoreName(), category);
                        log.info("flaskCategory : {}", flaskCategory);
                        // 여기에 switch 문으로 숫자 처리를 해야될 듯!
                        category = switch (flaskCategory) {
                            case "1" -> "오락";
                            case "2" -> "문화";
                            case "3" -> "카페";
                            case "4" -> "스포츠";
                            case "5" -> "음식점";
                            case "6" -> "숙박비";
                            case "7" -> "잡화소매";
                            case "8" -> "쇼핑";
                            case "9" -> "개인이체";
                            case "10" -> "교통비";
                            case "11" -> "의료비";
                            case "12" -> "보험비";
                            case "13" -> "구독/정기결제";
                            case "14" -> "교육비";
                            case "15" -> "모바일페이";
                            default -> "기타";
                        };
                        logsListDTO.setCategory(category);
                    } catch (Exception e) {
                        log.error("Exception occurred: {}", e.getMessage());
                    }
                }
            }
        }
        return logsListDTO;
    }

    private Log mapToLogEntity(Long userId, String cardNum, LogsListDTO logsListDTO) {
        Log logEntity = new Log();
        LogEmbedded logEmbedded = new LogEmbedded();
        // userId에 따라 logId 값을 설정
        Long logId = logRepository.findMaxLogIdByUserId(userId); // 해당 userId의 최대 logId를 가져옴
        if (logId == null) {
            logId = 1L; // 최대 logId가 없으면 1로 초기화
        } else {
            logId++; // 최대 logId가 있으면 1 증가
        }
        logEmbedded.setLogId(logId);
        logEmbedded.setUserId(userId);
        logEntity.setLogEmbedded(logEmbedded);
        // 나머지 필드 값 설정
        logEntity.setCardNum(cardNum);
        logEntity.setDeposit(logsListDTO.getDeposit());
        logEntity.setWithdraw(logsListDTO.getWithdraw());
        logEntity.setBalance(logsListDTO.getBalance());
        logEntity.setDate(logsListDTO.getDate());
        logEntity.setUseStoreName(logsListDTO.getUseStoreName());
        log.info("logsListDTO.getUseStoreName() : {}",logsListDTO.getUseStoreName());
        logEntity.setCategory(logsListDTO.getCategory());
        return logEntity;
    }
}
