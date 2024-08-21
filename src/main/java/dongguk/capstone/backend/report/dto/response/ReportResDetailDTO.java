package dongguk.capstone.backend.report.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResDetailDTO {
    @Schema(description = "요청 Month의 최고 소비 카테고리와 그 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private Map<String, Double> highestCategory;

    @Schema(description = "요청 Month의 최저 소비 카테고리와 그 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private Map<String, Double> lowestCategory;

    @Schema(description = "최고 및 최저 소비 카테고리를 제외한 전체 소비 카테고리의 변동 %")
    private Map<String, Double> categoryChangePercent;

    @Schema(description = "각 카테고리 별 예산 (목표 금액)")
    private Map<String, Long> categoryBudget;

    @Schema(description = "각 카테고리별 소비량 (예산과 비교한 %)")
    private Map<String, Double> categoryUsePercent;
}
