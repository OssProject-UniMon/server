package dongguk.capstone.backend.job.controller;

import dongguk.capstone.backend.job.dto.response.JobResReadyDTO;
import dongguk.capstone.backend.job.dto.request.JobReqDTO;
import dongguk.capstone.backend.job.dto.response.JobResDTO;
import dongguk.capstone.backend.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
@Slf4j
public class JobApiController {
    private final JobService jobService;

    @GetMapping("/ready")
    @Operation(summary = "아르바이트 내역 준비", description = "아르바이트 내역을 준비합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = JobResReadyDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public JobResReadyDTO ready(){
        jobService.fetchJobsAndRecommend();
        return new JobResReadyDTO(1);
    }

    @PostMapping("/recommend")
    @Operation(summary = "아르바이트 추천", description = "아르바이트를 추천합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = JobResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public JobResDTO recommend(@RequestParam("userId") Long userId, @RequestBody JobReqDTO jobReqDTO){
        return jobService.recommend(userId, jobReqDTO);
    }
}
