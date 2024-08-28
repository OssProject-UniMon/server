package dongguk.capstone.backend.gpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import dongguk.capstone.backend.monthlyaggregation.repository.MonthlyAggregationRepository;
import dongguk.capstone.backend.report.entity.Report;
import dongguk.capstone.backend.report.repository.ReportRepository;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import dongguk.capstone.backend.gpt.dto.GPTRequestDTO;
import dongguk.capstone.backend.gpt.dto.GPTResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptServiceImpl implements GptService{
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;
    private static final String BUDGET_FLASK_SERVER_URL = "http://43.202.249.208:5000/budget";
    private static final String ADVICE_FLASK_SERVER_URL = "http://43.202.249.208:5000/advice";

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일마다 진행
    // 저번 달을 기준으로 예산을 구성
    public void gptBudget() {
        List<User> userList = userRepository.findAll();
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (User user : userList) {
            LocalDate currentMonth = LocalDate.now();
            String stringCurrentMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

            LocalDate lastMonth = currentMonth.minusMonths(1); // 저번 달을 기준으로 예산을 구성하기 위해 1달을 뺌
            String stringLastMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserIdAndMonth(user.getUserId(), stringLastMonth)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 월별 집계가 없습니다."));

            try {
                String consumptionDetails = getGptRequestDTO(monthlyAggregation);
                log.info("consumptionDetails : {}", consumptionDetails);

                String json = mapper.writeValueAsString(Map.of("consumption_details", consumptionDetails));

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(BUDGET_FLASK_SERVER_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                log.info("request : "+request);

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body().trim();

                    // JSON 응답에서 "result" 필드를 추출
                    JsonNode jsonResponse = mapper.readTree(responseBody);
                    String responseText = jsonResponse.path("result").asText();
                    log.info("responseText : {}",responseText);

                    Long totalBudget = null;
                    Map<String, Long> budgetMap = new HashMap<>();

                    String[] lines = responseText.split("\n");
                    for (String line : lines) {
                        log.info("line :"+line);
                        if (line.contains("총 예산")) {
                            totalBudget = Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                        } else if (line.contains(":")) {
                            String[] parts = line.split(":");
                            if (parts.length == 2) {
                                String category = parts[0].trim();
                                Long budget = Long.parseLong(parts[1].replaceAll("[^0-9]", "").trim());
                                budgetMap.put(category, budget);
                            }
                        }
                    }

                    Report report = Report.builder()
                            .userId(user.getUserId())
                            .date(stringCurrentMonth)
                            .totalBudget(totalBudget)
                            .entertainmentBudget(budgetMap.getOrDefault("오락", 0L))
                            .cultureBudget(budgetMap.getOrDefault("문화", 0L))
                            .cafeBudget(budgetMap.getOrDefault("카페", 0L))
                            .sportsBudget(budgetMap.getOrDefault("스포츠", 0L))
                            .foodBudget(budgetMap.getOrDefault("음식점", 0L))
                            .accommodationBudget(budgetMap.getOrDefault("숙박비", 0L))
                            .retailBudget(budgetMap.getOrDefault("잡화소매", 0L))
                            .shoppingBudget(budgetMap.getOrDefault("쇼핑", 0L))
                            .transferBudget(budgetMap.getOrDefault("이체", 0L))
                            .transportationBudget(budgetMap.getOrDefault("교통비", 0L))
                            .medicalBudget(budgetMap.getOrDefault("의료비", 0L))
                            .insuranceBudget(budgetMap.getOrDefault("보험비", 0L))
                            .subBudget(budgetMap.getOrDefault("구독/정기결제", 0L))
                            .eduBudget(budgetMap.getOrDefault("교육비", 0L))
                            .mobileBudget(budgetMap.getOrDefault("모바일페이", 0L))
                            .othersBudget(budgetMap.getOrDefault("기타", 0L))
                            .build();

                    reportRepository.save(report);
                } else {
                    log.error("Flask 서버로부터 데이터를 가져오지 못했습니다: {}", response.body());
                }
            } catch (Exception e) {
                log.error("Flask 서버와의 통신 중 오류 발생: {}", e.getMessage());
            }
        }
    }



    private String getGptRequestDTO(MonthlyAggregation monthlyAggregation) {
        return "저번 달에 쓴 총 소비 금액은 " + monthlyAggregation.getMonthlyTotalConsumption() + "원, "
                + "오락 카테고리는 " + monthlyAggregation.getMonthlyTotalEntertainmentConsumption() + "원, "
                + "문화 카테고리는 " + monthlyAggregation.getMonthlyTotalCultureConsumption() + "원, "
                + "카페 카테고리는 " + monthlyAggregation.getMonthlyTotalCafeConsumption() + "원, "
                + "스포츠 카테고리는 " + monthlyAggregation.getMonthlyTotalSportsConsumption() + "원, "
                + "음식점 카테고리는 " + monthlyAggregation.getMonthlyTotalFoodConsumption() + "원, "
                + "숙박비 카테고리는 " + monthlyAggregation.getMonthlyTotalAccommodationConsumption() + "원, "
                + "잡화소매 카테고리는 " + monthlyAggregation.getMonthlyTotalRetailConsumption() + "원, "
                + "쇼핑 카테고리는 " + monthlyAggregation.getMonthlyTotalShoppingConsumption() + "원, "
                + "이체 카테고리는 " + monthlyAggregation.getMonthlyTotalTransferConsumption() + "원, "
                + "교통비 카테고리는 " + monthlyAggregation.getMonthlyTotalTransportationConsumption() + "원, "
                + "의료비 카테고리는 " + monthlyAggregation.getMonthlyTotalMedicalConsumption() + "원, "
                + "보험비 카테고리는 " + monthlyAggregation.getMonthlyTotalInsuranceConsumption() + "원, "
                + "구독/정기결제 카테고리는 " + monthlyAggregation.getMonthlyTotalSubConsumption() + "원, "
                + "교육비 카테고리는 " + monthlyAggregation.getMonthlyTotalEduConsumption() + "원, "
                + "모바일페이 카테고리는 " + monthlyAggregation.getMonthlyTotalMobileConsumption() + "원, "
                + "기타 카테고리는 " + monthlyAggregation.getMonthlyTotalOthersConsumption() + "원";
    }

    public String gptAdvice(Long nowTotalConsumption, int consumptionPercent, int consumptionChangePercentage, String highestCategoryKey, int highestCategoryPercent) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        String changeDescription = getChangeDescription(consumptionChangePercentage);

        String adviceDetails;
        if(highestCategoryPercent == 999999){
            adviceDetails = "이번 달의 현재까지 쓴 소비량은 " + nowTotalConsumption + "원이고, "
                    + "총 소비량에 대한 예산에 비해 이번 달의 현재까지 쓴 소비량은 " + consumptionPercent + "%입니다. "
                    + changeDescription + " "
                    + "그리고 가장 많은 소비 카테고리는 " + highestCategoryKey + "이고, "
                    + "그 카테고리의 소비량은 예산 대비 " + highestCategoryPercent + "%";
        } else{
            adviceDetails = "이번 달의 현재까지 쓴 소비량은 " + nowTotalConsumption + "원이고, "
                    + "총 소비량에 대한 예산에 비해 이번 달의 현재까지 쓴 소비량은 " + consumptionPercent + "%입니다. "
                    + changeDescription + " "
                    + "그리고 가장 많은 소비 카테고리는 " + highestCategoryKey + "이고, "
                    + "그 카테고리의 소비량은 예산 대비 " + highestCategoryPercent + "%";
        }
        log.info("adviceDetails : "+adviceDetails);

        try {
            String json = mapper.writeValueAsString(Map.of("advice_details", adviceDetails));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ADVICE_FLASK_SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            log.info("request : "+request);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // JSON 응답에서 "result" 필드를 추출
                JsonNode responseJson = mapper.readTree(response.body());
                log.info("responseJson : {}",responseJson);
                return responseJson.path("result").asText();
            } else {
                log.error("Flask 서버로부터 조언을 가져오지 못했습니다: {}", response.body());
                throw new RuntimeException("Flask 서버로부터 조언을 가져오는 데 실패했습니다: " + response.body());
            }
        } catch (Exception e) {
            log.error("Flask 서버와의 통신 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Flask 서버와의 통신 중 오류 발생", e);
        }
    }

    private static String getChangeDescription(int consumptionChangePercentage) {
        String changeDescription;
        // 소비 변화 비율에 따라 프롬프트를 다르게 설정
        if (consumptionChangePercentage > 0) {
            // 증가한 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량에 비해 이번 달의 현재까지 쓴 소비량이 "
                    + consumptionChangePercentage + "% 증가했습니다.";
        } else if (consumptionChangePercentage < 0) {
            // 감소한 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량에 비해 이번 달의 현재까지 쓴 소비량이 "
                    + Math.abs(consumptionChangePercentage) + "% 감소했습니다.";
        } else {
            // 변화가 없는 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량과 비교했을 때 이번 달의 현재까지 쓴 소비량이 변동이 없습니다.";
        }
        return changeDescription;
    }
}