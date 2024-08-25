package dongguk.capstone.backend.gpt.service;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
@Slf4j
public class GptServiceImpl implements GptService{
    private final WebClient gptWebClient;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public GptServiceImpl(@Qualifier("gptWebClient") WebClient gptWebClient,
                          UserRepository userRepository, ReportRepository reportRepository,
                          MonthlyAggregationRepository monthlyAggregationRepository) {
        this.gptWebClient = gptWebClient;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.monthlyAggregationRepository = monthlyAggregationRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void gptBudget() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String stringLastMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserIdAndMonth(user.getUserId(), stringLastMonth)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 월별 집계가 없습니다."));

            GPTRequestDTO gptRequestDTO = getGptRequestDTO(monthlyAggregation);

            Mono<String> gptResponse = gptWebClient.post()
                    .uri(apiUrl)
                    .bodyValue(gptRequestDTO)
                    .retrieve()
                    .bodyToMono(GPTResponseDTO.class)
                    .map(response -> response.getChoices().get(0).getMessage().getContent().trim());

            gptResponse.subscribe(responseText -> {
                Long totalBudget = null;
                Map<String, Long> budgetMap = new HashMap<>();

                String[] lines = responseText.split("\n");
                for (String line : lines) {
                    if (line.contains("적절한 총 예산")) {
                        totalBudget = Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                    } else if (line.contains("각 카테고리별 적절한 예산")) {
                        // 이 줄은 무시하고 계속 진행
                        continue;
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
                        .date(stringLastMonth)
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
            });
        }
    }


    private GPTRequestDTO getGptRequestDTO(MonthlyAggregation monthlyAggregation) {
        String prompt = "저번 달에 쓴 총 소비 금액은 " + monthlyAggregation.getMonthlyTotalConsumption() + "원이고, "
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
                + "기타 카테고리는 " + monthlyAggregation.getMonthlyTotalOthersConsumption() + "원 입니다. "
                + "이 때, 이번 달의 총 예산과 각 카테고리에 대한 예산을 정하려고 하는데 "
                + "다른 설명 하지 말고 딱 적절한 총 예산과 각 카테고리에 대한 적절한 예산만 추천해주세요";

        return new GPTRequestDTO(model, prompt);
    }

    public Mono<String> gptAdvice(Long nowTotalConsumption, int consumptionPercent, int consumptionChangePercentage) {
        String changeDescription = getChangeDescription(consumptionChangePercentage);

        String prompt = "이번 달의 현재까지 쓴 소비량은 " + nowTotalConsumption + "원이고, "
                + "총 소비량에 대한 예산에 비해 이번 달의 현재까지 쓴 소비량은 " + consumptionPercent + "%입니다. "
                + changeDescription + " "
                + "위 정보를 바탕으로, 예산 관리에 대한 적절한 조언을 제공해 주세요. "
                + "예산을 초과하거나 부족한 경우, 어떻게 조정할 수 있는지 구체적인 조언을 부탁드립니다.";

        GPTRequestDTO gptRequestDTO = new GPTRequestDTO(model, prompt);

        return gptWebClient.post()
                .uri(apiUrl)
                .bodyValue(gptRequestDTO)
                .retrieve()
                .bodyToMono(GPTResponseDTO.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent().trim());
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