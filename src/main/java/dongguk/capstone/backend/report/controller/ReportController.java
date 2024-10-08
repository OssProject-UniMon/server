package dongguk.capstone.backend.report.controller;

import dongguk.capstone.backend.gpt.service.GptService;
import dongguk.capstone.backend.home.dto.response.HomeResDTO;
import dongguk.capstone.backend.log.dto.response.LogsResDTO;
import dongguk.capstone.backend.report.dto.request.ReportReqDTO;
import dongguk.capstone.backend.report.dto.response.ReportResDetailDTO;
import dongguk.capstone.backend.report.dto.response.ReportResSummaryDTO;
import dongguk.capstone.backend.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    private final ReportService reportService;
    private final GptService gptService;

    @PostMapping("/summary")
    @Operation(summary = "보고서 요약", description = "보고서 요약 화면을 보여줍니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ReportResSummaryDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> summaryReport(@RequestBody ReportReqDTO reportReqDTO){
        // 좀 더 추가해야 할 것 같다
        return ResponseEntity.ok(reportService.reportSummaryPage(reportReqDTO.getUserId(), reportReqDTO.getDate()));
    }

    @PostMapping("/detail")
    @Operation(summary = "보고서 상세", description = "보고서 상세 화면을 보여줍니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ReportResDetailDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> detailReport(@RequestBody ReportReqDTO reportReqDTO){
        // 요약 페이지에서 자세히보기 하면 이동한 달을 yyyyMMdd 형식의 date로 보내게 하기 (지난 달이면 그 달의 마지막 날로)
        // 좀 더 추가해야 할 것 같다
        return ResponseEntity.ok(reportService.reportDetailPage(reportReqDTO.getUserId(), reportReqDTO.getDate()));
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(){
        gptService.gptBudget(); // 데베에 예산 들어감
        reportService.updateGptAdviceForAllUsers();
        // 현재 gptAdvice에 설정된 값들은 임시 값이고, 작동이 잘 된다면 test api에서는 gpt 조언이 나와야 함
        return ResponseEntity.ok(gptService.gptAdvice(200000L, 60, 30, "카페", 70));
    }
}
