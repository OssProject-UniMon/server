package dongguk.capstone.backend.card.controller;

import dongguk.capstone.backend.card.dto.request.CardReqRegistDTO;
import dongguk.capstone.backend.card.dto.response.CardResRegistDTO;
import dongguk.capstone.backend.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/card")
public class CardApiController {
    private final CardService cardService;

    /**
     * 카드 등록
     * @param userId
     * @param cardReqRegistDTO
     * @return
     */
    @PostMapping("/card-regist")
    @Operation(summary = "카드 등록", description = "카드를 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = CardResRegistDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CardResRegistDTO cardRegist(@RequestParam("userId") Long userId, @RequestBody CardReqRegistDTO cardReqRegistDTO) {
//        this.cardRegistRequestDTO = cardRegistRequestDTO;
        int result = cardService.cardRegist(userId, cardReqRegistDTO);
        if(result < 0)
            return new CardResRegistDTO(0);
        return new CardResRegistDTO(1);
    }
}
