package dongguk.capstone.backend.home.dto.response;

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
public class MonitoringResDTO {
    @Schema(description = "사용자의 이번 달 소비량")
    private Long consumption;
    @Schema(description = "사용자의 이번 달 소비량의 예산 대비 %")
    private int totalConsumptionPercent;

    @Schema(description = "사용자의 이번 달 최고 소비 카테고리")
    private String highestCategory;

    @Schema(description = "사용자의 이번 달 최고 소비 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private int highestCategoryPercent;
}
