package dongguk.capstone.backend.report.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResSummaryDTO {
    @Schema(description = "요청 Month의 소비량")
    private Long totalConsumption;

    @Schema(description = "gpt가 짜준 예산에 비해 몇 %인지 계산한 결과 %")
    private double percentageOfBudget;

    @Schema(description = "저번 달의 소비량이 있는지 여부")
    private boolean lastConsumption;

    @Schema(description = "저번 달(요청 Month - 1)의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private BigDecimal percentageChange;

    @Schema(description = "gpt 조언")
    private String gptAdvice;

    @Schema(description = "요청 Month의 최고 소비 카테고리")
    private String highestCategory;

    @Schema(description = "요청 Month의 최고 소비 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private double highestCategoryPercent;

    @Schema(description = "요청 Month의 최저 소비 카테고리")
    private String lowestCategory;

    @Schema(description = "요청 Month의 최저 소비 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private double lowestCategoryPercent;
}
