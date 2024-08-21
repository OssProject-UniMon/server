package dongguk.capstone.backend.report.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportReqDTO {
    @Schema(description = "사용자의 userId")
    private Long userId;
    @Schema(description = "보고서를 보여줄 날짜, yyyyMMdd 형식", example = "20240801")
    private String date; // yyyyMMdd 형식
}
