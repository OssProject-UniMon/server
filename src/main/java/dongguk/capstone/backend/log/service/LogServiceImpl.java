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
import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import dongguk.capstone.backend.categoryconsumption.repository.CategoryConsumptionRepository;
import dongguk.capstone.backend.dailyconsumption.service.DailyConsumptionService;
import dongguk.capstone.backend.log.dto.LogsListDTO;
import dongguk.capstone.backend.log.dto.response.LogsResDTO;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.log.entity.LogEmbedded;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

@Service
@Slf4j
public class LogServiceImpl implements LogService{
    private final DailyConsumptionService dailyConsumptionService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final LogRepository logRepository;
    private final BarobillApiService barobillApiService;

    private final CategoryConsumptionRepository categoryConsumptionRepository;

    private static final String LEDGER_FLASK_SERVER_URL = "http://43.202.249.208:5000/classify";

    public LogServiceImpl(DailyConsumptionService dailyConsumptionService,
                          UserRepository userRepository, AccountRepository accountRepository,
                          CardRepository cardRepository, LogRepository logRepository,
                          CategoryConsumptionRepository categoryConsumptionRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.RELEASE);
        this.dailyConsumptionService = dailyConsumptionService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.logRepository = logRepository;
        this.categoryConsumptionRepository = categoryConsumptionRepository;
    }

    public static String classifyTransaction(String useStoreName, String category) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String transactionDetails = "가게명 : " + useStoreName + " 업종 : " + category;
            log.info("transactionDetails : {}",transactionDetails);

            String json = mapper.writeValueAsString(Map.of("transaction_details", transactionDetails));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(LEDGER_FLASK_SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

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
    @Scheduled(cron = "0 0 0 * * *")
    public void fetchAndSaveLogs() {
        List<User> users = userRepository.findAll();
        log.info("users : {}", users);

        for (User user : users) {
            processUserLogs(user);
        }
    }

    private void processUserLogs(User user) {
        List<Account> accounts = accountRepository.findAll();
        List<Card> cards = cardRepository.findAll();

        log.info("accounts : {}", accounts);
        log.info("cards : {}", cards);

        LocalDate today = LocalDate.now();
        LocalDate startDate = getStartDateForUser(user, today);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);

        String startDateString = startDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDateString = endDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Optional<Account> accountOpt = findAccountForUser(user, accounts);
        Optional<Card> cardOpt = findCardForUser(user, cards);

        if (accountOpt.isEmpty() || cardOpt.isEmpty()) {
            log.warn("Account or Card not found for user {}", user.getUserId());
            return;
        }

        Account account = accountOpt.get();
        Card card = cardOpt.get();

        try {
            PagedBankAccountLogEx accountLog = barobillApiService.bankAccount.getPeriodBankAccountLogEx(
                    "181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", "capstone11",
                    account.getAccountEmbedded().getBankAccountNum(), startDateString, endDateString, 100, 1, 2);

            PagedCardLogEx cardLog = barobillApiService.card.getPeriodCardLogEx(
                    "181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", "capstone11",
                    card.getCardEmbedded().getCardNum(), startDateString, endDateString, 100, 1, 2);

            processLogs(accountLog, cardLog, user.getUserId());
            dailyConsumptionService.saveDailyConsumption(user.getUserId(), endDateString);
        } catch (Exception e) {
            log.error("Exception occurred while processing logs for user {}: {}", user.getUserId(), e.getMessage());
        }
    }


    private LocalDate getStartDateForUser(User user, LocalDate today) {
        String lastSavedDate = logRepository.findLastSavedDateByUserId(user.getUserId());
        if (lastSavedDate != null) {
            return LocalDate.parse(lastSavedDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).plusDays(1);
        } else {
            return today.minusMonths(3);
        }
    }

    private Optional<Account> findAccountForUser(User user, List<Account> accounts) {
        return accounts.stream()
                .filter(acc -> user.getUserId().equals(acc.getUser().getUserId()))
                .findFirst();
    }

    private Optional<Card> findCardForUser(User user, List<Card> cards) {
        return cards.stream()
                .filter(c -> user.getUserId().equals(c.getUser().getUserId()))
                .findFirst();
    }

    private void processLogs(PagedBankAccountLogEx accountLog, PagedCardLogEx cardLog, Long userId) {
        try{
            List<CardLogEx> processedCardLogs = new ArrayList<>();
            List<BankAccountLogEx> processedBankAccountLogs = new ArrayList<>();

            if (accountLog.getCurrentPage() < 0) {
                log.error("Failed to retrieve account log");
                return;
            }

            for (BankAccountLogEx bankAccountLogEx : accountLog.getBankAccountLogList().getBankAccountLogEx()) {
                for (CardLogEx cardLogEx : cardLog.getCardLogList().getCardLogEx()) {
                    if (processedCardLogs.contains(cardLogEx) || processedBankAccountLogs.contains(bankAccountLogEx)) {
                        continue;
                    }

                    if (!bankAccountLogEx.getTransType().contains("이체")) {
                        LogsListDTO logsListDTO = getLogsListDTO(bankAccountLogEx, cardLogEx);
                        Log logEntity = mapToLogEntity(userId, cardLogEx.getCardNum(), logsListDTO);
                        logRepository.save(logEntity);
                        processedCardLogs.add(cardLogEx);
                        processedBankAccountLogs.add(bankAccountLogEx);
                    }
                }
            }
        } catch (Exception e){
            log.error("[LogService] processLogs error : ",e);
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
                LogsListDTO logsListDTO = LogsListDTO.builder()
                        .deposit(l.getDeposit())
                        .withdraw(l.getWithdraw())
                        .balance(l.getBalance())
                        .date(l.getDate())
                        .useStoreName(l.getUseStoreName())
                        .category(l.getCategory())
                        .build();
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
        LogsListDTO logsListDTO = LogsListDTO.builder()
                .deposit(bankAccountLogEx.getDeposit())
                .withdraw(bankAccountLogEx.getWithdraw())
                .balance(bankAccountLogEx.getBalance())
                .date(cardLogEx.getUseDT())
                .useStoreName(cardLogEx.getUseStoreName())
                .useStoreCorpNum(cardLogEx.getUseStoreCorpNum())
                .build();
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

    private void updateCategoryConsumption(Long userId, String dateTime, Long amount, String category) {
        // dateTime을 LocalDateTime으로 파싱
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // LocalDate로 변환 (시간 정보는 무시)
        LocalDate currentDate = localDateTime.toLocalDate();

        // 이전 달의 같은 날짜 계산
        LocalDate previousMonthSameDay = currentDate.minusMonths(1);

        // 날짜를 문자열로 포맷팅
        String currentDateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String previousMonthDateStr = previousMonthSameDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 현재 월의 CategoryConsumption 객체 조회
        List<CategoryConsumption> currentMonthCategoryConsumptions = categoryConsumptionRepository.findCategoryConsumptionByUserIdAndDay(userId, currentDateStr);

        // 이전 달의 CategoryConsumption 객체 조회
        List<CategoryConsumption> previousMonthCategoryConsumptions = categoryConsumptionRepository.findCategoryConsumptionByUserIdAndDay(userId, previousMonthDateStr);

        // 카테고리별 소비량 초기화
        long entertainmentConsumption = 0L;
        long cultureConsumption = 0L;
        long cafeConsumption = 0L;
        long sportsConsumption = 0L;
        long foodConsumption = 0L;
        long accommodationConsumption = 0L;
        long retailConsumption = 0L;
        long shoppingConsumption = 0L;
        long transferConsumption = 0L;
        long transportationConsumption = 0L;
        long medicalConsumption = 0L;
        long insuranceConsumption = 0L;
        long subConsumption = 0L;
        long eduConsumption = 0L;
        long mobileConsumption = 0L;
        long othersConsumption = 0L;

        // 현재 월의 모든 카테고리 소비량 업데이트
        for (CategoryConsumption cc : currentMonthCategoryConsumptions) {
            entertainmentConsumption += cc.getEntertainmentConsumption();
            cultureConsumption += cc.getCultureConsumption();
            cafeConsumption += cc.getCafeConsumption();
            sportsConsumption += cc.getSportsConsumption();
            foodConsumption += cc.getFoodConsumption();
            accommodationConsumption += cc.getAccommodationConsumption();
            retailConsumption += cc.getRetailConsumption();
            shoppingConsumption += cc.getShoppingConsumption();
            transferConsumption += cc.getTransferConsumption();
            transportationConsumption += cc.getTransportationConsumption();
            medicalConsumption += cc.getMedicalConsumption();
            insuranceConsumption += cc.getInsuranceConsumption();
            subConsumption += cc.getSubConsumption();
            eduConsumption += cc.getEduConsumption();
            mobileConsumption += cc.getMobileConsumption();
            othersConsumption += cc.getOthersConsumption();
        }

        // 카테고리에 따라 소비량 업데이트
        switch (category) {
            case "오락":
                entertainmentConsumption += amount;
                break;
            case "문화":
                cultureConsumption += amount;
                break;
            case "카페":
                cafeConsumption += amount;
                break;
            case "스포츠":
                sportsConsumption += amount;
                break;
            case "음식점":
                foodConsumption += amount;
                break;
            case "숙박비":
                accommodationConsumption += amount;
                break;
            case "잡화소매":
                retailConsumption += amount;
                break;
            case "쇼핑":
                shoppingConsumption += amount;
                break;
            case "개인이체":
                transferConsumption += amount;
                break;
            case "교통비":
                transportationConsumption += amount;
                break;
            case "의료비":
                medicalConsumption += amount;
                break;
            case "보험비":
                insuranceConsumption += amount;
                break;
            case "구독/정기결제":
                subConsumption += amount;
                break;
            case "교육비":
                eduConsumption += amount;
                break;
            case "모바일페이":
                mobileConsumption += amount;
                break;
            default: // 기타
                othersConsumption += amount;
                break;
        }

        // CategoryConsumption 객체 생성 및 저장
        CategoryConsumption categoryConsumption = CategoryConsumption.builder()
                .userId(userId)
                .date(currentDateStr)
                .isLastCategoryConsumption(!previousMonthCategoryConsumptions.isEmpty())
                .entertainmentConsumption(entertainmentConsumption)
                .cultureConsumption(cultureConsumption)
                .cafeConsumption(cafeConsumption)
                .sportsConsumption(sportsConsumption)
                .foodConsumption(foodConsumption)
                .accommodationConsumption(accommodationConsumption)
                .retailConsumption(retailConsumption)
                .shoppingConsumption(shoppingConsumption)
                .transferConsumption(transferConsumption)
                .transportationConsumption(transportationConsumption)
                .medicalConsumption(medicalConsumption)
                .insuranceConsumption(insuranceConsumption)
                .subConsumption(subConsumption)
                .eduConsumption(eduConsumption)
                .mobileConsumption(mobileConsumption)
                .othersConsumption(othersConsumption)
                .build();

        // CategoryConsumption 객체 저장
        categoryConsumptionRepository.save(categoryConsumption);
    }

    private Log mapToLogEntity(Long userId, String cardNum, LogsListDTO logsListDTO) {
        // userId에 따라 logId 값을 설정
        Long logId = logRepository.findMaxLogIdByUserId(userId); // 해당 userId의 최대 logId를 가져옴

        if (logId == null) {
            logId = 1L; // 최대 logId가 없으면 1로 초기화
        } else {
            logId++; // 최대 logId가 있으면 1 증가
        }

        LogEmbedded logEmbedded = LogEmbedded.builder()
                .logId(logId)
                .userId(userId)
                .build();

        Log logEntity = Log.builder()
                .logEmbedded(logEmbedded)
                .cardNum(cardNum)
                .deposit(logsListDTO.getDeposit())
                .withdraw(logsListDTO.getWithdraw())
                .balance(logsListDTO.getBalance())
                .date(logsListDTO.getDate())
                .useStoreName(logsListDTO.getUseStoreName())
                .category(logsListDTO.getCategory())
                .build();

        log.info("logsListDTO.getUseStoreName() : {}",logsListDTO.getUseStoreName());

        // 소비 카테고리 업데이트 (출금 금액으로 업데이트)
        updateCategoryConsumption(userId, logsListDTO.getDate(), Long.valueOf(logsListDTO.getWithdraw()), logsListDTO.getCategory());

        return logEntity;
    }
}
