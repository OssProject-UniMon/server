package dongguk.capstone.backend.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.CardLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import com.baroservice.ws.PagedCardLogEx;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dongguk.capstone.backend.accountdto.*;
import dongguk.capstone.backend.domain.Account;
import dongguk.capstone.backend.domain.Card;
import dongguk.capstone.backend.domain.Log;
import dongguk.capstone.backend.domain.User;
import dongguk.capstone.backend.repository.AccountRepository;
import dongguk.capstone.backend.repository.CardRepository;
import dongguk.capstone.backend.repository.LogRepository;
import dongguk.capstone.backend.repository.UserRepository;
import dongguk.capstone.backend.serializable.AccountEmbedded;
import dongguk.capstone.backend.serializable.CardEmbedded;
import dongguk.capstone.backend.serializable.LogEmbedded;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value; // application.properties 파일의 값을 사용하려면 이 Value 애노테이션 사용해야 됨
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import java.net.http.HttpRequest;

@Service
@Transactional
@Slf4j
public class LedgerService {

//    @Value("${openai.api.key}")
//    private String apikey; // 연동할 chat gpt assistant api의 api key

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final LogRepository logRepository;
    private final BarobillApiService barobillApiService;

    private static final String LEDGER_FLASK_SERVER_URL = "http://172.31.47.145:5000/classify";
//    private static final String LEDGER_FLASK_SERVER_URL = "http://127.0.0.1:5000/classify";


    public LedgerService(UserRepository userRepository, AccountRepository accountRepository, CardRepository cardRepository, LogRepository logRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
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

//    public JsonNode callChatGpt(String userMsg) throws JsonProcessingException{
//        final String url = "https://api.openai.com/v1/chat/completions";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apikey);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        Map<String, Object> bodyMap = getStringObjectMap(userMsg);
//
//        String body = objectMapper.writeValueAsString(bodyMap);
//
//        HttpEntity<String> request = new HttpEntity<>(body,headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//
//        return objectMapper.readTree(response.getBody());
//    }
//
//    private static Map<String, Object> getStringObjectMap(String userMsg) {
//        Map<String,Object> bodyMap = new HashMap<>();
//        bodyMap.put("model","gpt-4");
//
//        List<Map<String, String>> messages = new ArrayList<>();
//        Map<String, String> userMessage = new HashMap<>();
//        userMessage.put("role","user");
//        userMessage.put("content", userMsg);
//        messages.add(userMessage);
//
//        Map<String, String> assistantMesssage = new HashMap<>();
//        assistantMesssage.put("role","system");
//        assistantMesssage.put("content","당신은 제가 보내주는 업종을 부가적인 설명 없이, 가계부의 내역에 들어갈 오직 하나의 비용 종류를 매핑해서 알려주는 AI입니다." +
//                "매핑할 비용 종류는 식비, 문화비, 카페, 스포츠, 숙박비, 잡화소매, 쇼핑비, 개인이체, 교통비, 의료비, 보험비, 구독/정기결제, 교육비 이렇게 있습니다.");
//        messages.add(assistantMesssage);
//
//        bodyMap.put("messages",messages);
//        return bodyMap;
//    }


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
        AccountEmbedded accountEmbedded = new AccountEmbedded();
        if(userRepository.findById(userId).isPresent()) {
            account.setUser(userRepository.findById(userId).get());
            accountEmbedded.setUserId(userId);
            accountEmbedded.setBankAccountNum(accountRegistRequestDTO.getBankAccountNum());
            account.setAccountEmbedded(accountEmbedded);
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
        CardEmbedded cardEmbedded = new CardEmbedded();
        if (userRepository.findById(userId).isPresent()){
            card.setUser(userRepository.findById(userId).get());
            cardEmbedded.setUserId(userId);
            cardEmbedded.setCardNum(cardRegistRequestDTO.getCardNum());
            card.setCardEmbedded(cardEmbedded);
            card.setCardCompany(cardRegistRequestDTO.getCardCompany());
            card.setCardType(cardRegistRequestDTO.getCardType());
            card.setWebId(cardRegistRequestDTO.getWebId());
            card.setWebPwd(cardRegistRequestDTO.getWebPwd());
            cardRepository.save(card);

            return barobillApiService.card.registCard("3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", card.getCardCompany(), card.getCardType(), cardEmbedded.getCardNum(),
                    card.getWebId(), card.getWebPwd(), "", "");
        }
        return 0;
    }

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

                log.info("account : {}",account);
                log.info("accountNum : {}",account.getAccountEmbedded().getBankAccountNum());

                log.info("card : {}",card);
                log.info("cardNum : {}",card.getCardEmbedded().getCardNum());


                // startDateString, endDateString이 바로빌에서 원하는 요청 데이터 형태랑 다른 듯???
                // 바로빌 API를 사용하여 계좌 조회
                PagedBankAccountLogEx accountLog = barobillApiService.bankAccount.getPeriodBankAccountLogEx(
                        "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
                        account.getAccountEmbedded().getBankAccountNum(), startDateString, endDateString, 10, 1, 2);

                log.info("accountLog : {}",accountLog);


                // 바로빌 API를 사용하여 카드 조회
                PagedCardLogEx cardLog = barobillApiService.card.getPeriodCardLogEx(
                        "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03", "2018204468", "capstone11",
                        card.getCardEmbedded().getCardNum(), startDateString, endDateString, 10, 1, 2);

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
                                log.info("bankAccountLogEx.getTransDT() : {}", bankAccountLogEx.getTransDT());
                                log.info("cardLogEx.getUseDT() : {}", cardLogEx.getUseDT());
                                // 계좌 결제 내역과 카드 결제 정보를 순서대로 넣으면 곧 일시 순서대로 넣는다
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
    public LogsResponseDTO log(Long userId) {
        LogsResponseDTO logsResponseDTO = new LogsResponseDTO(); // 거래 내역 로그 응답

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
        logsResponseDTO.setLogList(list);
        log.info("logsResponseDTO : {}",logsResponseDTO);
        return logsResponseDTO;
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