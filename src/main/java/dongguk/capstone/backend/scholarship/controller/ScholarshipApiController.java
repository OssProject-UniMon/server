package dongguk.capstone.backend.scholarship.controller;

import dongguk.capstone.backend.schedule.repository.ScheduleRepository;
import dongguk.capstone.backend.scholarship.dto.response.ScholarshipResRecommendDTO;
import dongguk.capstone.backend.scholarship.dto.response.ScholarshipResDTO;
import dongguk.capstone.backend.scholarship.service.ScholarshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scholarship")
@Slf4j
public class ScholarshipApiController {
    private final ScholarshipService scholarshipService;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/scrape")
    @Operation(summary = "장학금 크롤링", description = "장학금 크롤링을 진행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ScholarshipResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    private ScholarshipResDTO test(){
        scholarshipService.scrape();
        if(!scheduleRepository.findAll().isEmpty()){
            return new ScholarshipResDTO(1);
        }
        return new ScholarshipResDTO(0);
    }

//    @PostMapping("/recommend")
//    private ScholarshipResponseDTO recommend(@RequestParam("userId") Long userId){
//        return scholarshipService.recommend(userId);
//    }

    @PostMapping("/recommend")
    @Operation(summary = "장학금 추천", description = "장학금을 추천합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ScholarshipResRecommendDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    private ScholarshipResRecommendDTO recommend(@RequestParam("userId") Long userId) throws IOException, InterruptedException {
        return scholarshipService.recommend(userId);
    }
}
