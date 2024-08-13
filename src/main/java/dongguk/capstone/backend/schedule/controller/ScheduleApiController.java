package dongguk.capstone.backend.schedule.controller;

import dongguk.capstone.backend.schedule.dto.request.ScheduleReqPlusDTO;
import dongguk.capstone.backend.schedule.dto.response.ScheduleResPlusDTO;
import dongguk.capstone.backend.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/Schedule")
public class ScheduleApiController {
    private final ScheduleService scheduleService;

    @PostMapping("/plus")
    @Operation(summary = "스케쥴 추가", description = "스케쥴을 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ScheduleResPlusDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ScheduleResPlusDTO plus(@RequestParam("userId") Long userId, @RequestBody ScheduleReqPlusDTO scheduleReqPlusDTO){
        int result = scheduleService.plus(userId, scheduleReqPlusDTO);
        return new ScheduleResPlusDTO(result);
    }
}
