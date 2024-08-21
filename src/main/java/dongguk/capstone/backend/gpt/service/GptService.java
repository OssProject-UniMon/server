package dongguk.capstone.backend.gpt.service;

import dongguk.capstone.backend.gpt.dto.GPTResponseDTO;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.ForkJoinPool;

public interface GptService {
    // 1. 달마다 gpt에게 프롬프트 보내서 예산짜는 로직
    // => 저번 달의 총 소비량, CategoryConsumption의 각 카테고리 별 소비량 값들을 들고와서
    // => gpt에게 이에 맞게 총 예산과, 각각의 카테고리에 맞는 예산을 달라고 하기
    void gptBudget();

    Mono<String> gptAdvice(Long nowTotalConsumption, double consumptionPercent, BigDecimal consumptionChangePercentage);
}
