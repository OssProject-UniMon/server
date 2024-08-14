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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GptServiceImpl implements GptService{
    private final WebClient gptWebClient;
    private final UserRepository userRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public GptServiceImpl(@Qualifier("gptWebClient") WebClient gptWebClient, UserRepository userRepository, MonthlyAggregationRepository monthlyAggregationRepository) {
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
            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserId(user.getUserId())
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

                // 파싱한 예산을 필요에 따라 처리 (예: 데이터베이스에 저장)
                // 예시: budgetMap.forEach((category, budget) -> System.out.println(category + ": " + budget));
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
                + "다른 설명 하지 말고 딱 적절한 총 예산과 각 카테고리에 대한 적절한 예산만 추천해주세요";

        return new GPTRequestDTO(model, prompt);
    }
}