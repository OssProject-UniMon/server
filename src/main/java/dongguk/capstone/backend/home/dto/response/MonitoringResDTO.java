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
    private double totalConsumptionPercent;
    @Schema(description = "사용자의 이번 달 최고 소비 카테고리와 그 카테고리 소비량의 예산 대비 %")
    private Map<String, Double> highestCategory;
}
