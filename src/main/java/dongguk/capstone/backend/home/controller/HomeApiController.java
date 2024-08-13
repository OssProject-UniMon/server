package dongguk.capstone.backend.home.controller;

import dongguk.capstone.backend.home.dto.response.HomeResDTO;
import dongguk.capstone.backend.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Slf4j
public class HomeApiController {
    private final HomeService homeService;

    @GetMapping("")
    @Operation(summary = "메인 화면", description = "메인 화면으로 이동합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = HomeResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public HomeResDTO home(@RequestParam("userId") Long userId){
        return homeService.home(userId);
    }
}
