package dongguk.capstone.backend.gpt.service;

import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import dongguk.capstone.backend.monthlyaggregation.repository.MonthlyAggregationRepository;
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
    private final MonthlyAggregationRepository monthlyAggregationRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public GptServiceImpl(@Qualifier("gptWebClient") WebClient gptWebClient,
                          UserRepository userRepository,
                          MonthlyAggregationRepository monthlyAggregationRepository) {
        this.gptWebClient = gptWebClient;
        this.userRepository = userRepository;
        this.monthlyAggregationRepository = monthlyAggregationRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void gptBudget(){
        List<User> userList = userRepository.findAll();
        for(User user : userList){
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
                // 응답 텍스트에서 카테고리별 예산을 파싱
                Map<String, Integer> budgetMap = new HashMap<>();
                String[] lines = responseText.split("\n");

                for (String line : lines) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            String category = parts[0].trim();
                            int budget = Integer.parseInt(parts[1].replaceAll("[^0-9]", "").trim());
                            budgetMap.put(category, budget);
                        }
                    }
                }

                // gptResponse로부터 총예산과 카테고리별 예산을 파싱한 뒤에
                // Report에 넣어서  build 하고 repository에 save하기
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
                + "모바일페이 카테고리는 " + monthlyAggregation.getMonthlyTotalMobileConsumption() + "원 입니다. "
                + "이 때, 이번 달의 총 예산과 각 카테고리에 대한 예산을 정하려고 하는데 "
                + "다른 설명 하지 말고 딱 적절한 총 예산과 각 카테고리에 대한 적절한 예산만 추천해줘";

        return new GPTRequestDTO(model, prompt);
    }

    public Mono<String> gptAdvice(Long nowTotalConsumption, double consumptionPercent, BigDecimal consumptionChangePercentage){
        // 소비 변화 비율에 따라 프롬프트를 다르게 설정
        String changeDescription;
        if (consumptionChangePercentage.compareTo(BigDecimal.ZERO) > 0) {
            // 증가한 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량에 비해 이번 달의 현재까지 쓴 소비량이 "
                    + consumptionChangePercentage + "% 증가했습니다.";
        } else if (consumptionChangePercentage.compareTo(BigDecimal.ZERO) < 0) {
            // 감소한 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량에 비해 이번 달의 현재까지 쓴 소비량이 "
                    + consumptionChangePercentage.abs() + "% 감소했습니다.";
        } else {
            // 변화가 없는 경우
            changeDescription = "저번 달의 동일한 날짜의 소비량과 비교했을 때 이번 달의 현재까지 쓴 소비량이 변동이 없습니다.";
        }

        String prompt = "이번 달의 현재까지 쓴 소비량은 " + nowTotalConsumption + "원이고, "
                + "총 소비량에 대한 예산에 비해 이번 달의 현재까지 쓴 소비량은 " + consumptionPercent + "%입니다. "
                + changeDescription + " "
                + "위 정보를 바탕으로, 예산 관리에 대한 적절한 조언을 제공해 주세요. "
                + "예산을 초과하거나 부족한 경우, 어떻게 조정할 수 있는지 구체적인 제안을 부탁드립니다.";

        GPTRequestDTO gptRequestDTO = new GPTRequestDTO(model, prompt);

        return gptWebClient.post()
                .uri(apiUrl)
                .bodyValue(gptRequestDTO)
                .retrieve()
                .bodyToMono(GPTResponseDTO.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent().trim());
    }
}