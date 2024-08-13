package dongguk.capstone.backend.account.controller;

import dongguk.capstone.backend.account.dto.request.AccountReqRegistDTO;
import dongguk.capstone.backend.account.dto.response.AccountResDTO;
import dongguk.capstone.backend.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountApiController {
    private final AccountService accountService;

    /**
     * 계좌 등록
     * @param userId
     * @param accountReqRegistDTO
     * @return
     */
    @PostMapping("/account-regist")
    @Operation(summary = "계좌 등록", description = "계좌를 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = AccountResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AccountResDTO accountRegist(@RequestParam("userId") Long userId, @RequestBody AccountReqRegistDTO accountReqRegistDTO) {
        // 여기서 받은 userId를 어떻게 할 수 있을까?
        // 그리고 Account 도메인 내용 DB 스키마 만들어지면 채우기
//        this.accountRegistRequestDTO = accountRegistRequestDTO;
        int result = accountService.accountRegist(userId, accountReqRegistDTO); // 새로운 계좌여야 테스트 가능할 듯
        if (result < 0) // 계좌 등록 실패
            return new AccountResDTO(0);
        return new AccountResDTO(1); // 계좌 등록 성공
    }
}
