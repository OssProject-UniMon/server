package dongguk.capstone.backend.log.controller;

import dongguk.capstone.backend.account.dto.request.AccountReqRegistDTO;
import dongguk.capstone.backend.card.dto.request.CardReqRegistDTO;
import dongguk.capstone.backend.log.dto.response.LogsResDTO;
import dongguk.capstone.backend.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
@Slf4j
public class LogApiController {
    private final LogService logService;
    private AccountReqRegistDTO accountReqRegistDTO; // 필드변수는 인스턴스 변수(클래스의 각 인스턴스마다 다른 값을 가지는 변수)이며, 그 클래스의 모든 메소드에서 사용 가능하다
    private CardReqRegistDTO cardReqRegistDTO;
    // 거래 내역 조회를 위해 클라이언트에서 뭘 줘야할지를 논의해보자
    // API 명세서 부터 barobillservice 를 참고해서 작성하자

    // API EndPoint를 어떻게 설정해야 할까...
    // 무조건 바로빌에서 지정한대로 해야할까?
    // 아니면 우리 맘대로 해도 되나..?

    /**
     * 거래 내역 조회
     * @param userId
     * @return
     */
    @GetMapping("/logs")
    @Operation(summary = "거래 내역 조회", description = "거래 내역을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "SUCCESS", content = @Content(schema = @Schema(implementation = LogsResDTO.class)))
            // @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LogsResDTO log(@RequestParam("userId") Long userId) {
        // !!!!!!!!!!!!!!! 고치자 !!!!!!!!!!!!!!!
        // 내가 보기엔 accountRegist 메소드를 호출할 당시에는 accountRegistRequestDTO에 값이 있지만,
        // 그 이후에 log 메소드에서 사용할 때는 값이 적용되지 않아서 null 값이다.
        // ※ accountRegistRequestDTO와 cardRegistRequestDTO는 각각 계좌 및 카드를 등록할 때 사용되는 요청 DTO이며, 메소드 호출 사이에 상태가 유지되지 않습니다. ※
        // => 그래서 nullPointerException이 발생하는 듯..
        // 값을 유지할 수 있는 방법이 없나? 아닌가? 동시성 문제가 있으려나?
//        log.info("logs", accountRegistRequestDTO); // null
//        log.info("logs", cardRegistRequestDTO); // null
        // 아 여기서 accountRegistRequestDTO 이거랑 cardRegistRequestDTO 이거 쓰면 안되는건가?
        // 만약 accountLogResponseDTO가 존재한다면, 200 ok 와 body로 accountLogsResponseDTO 전해주기
//        if(accountLogsResponseDTO!=null)
//            return ResponseEntity.ok().body(accountLogsResponseDTO);
//        else
//            return ResponseEntity.badRequest().build();
        return logService.log(userId);

        // 테스트 진행
        // => 일단 계좌 정보가 들어간 Account가 있어야 함.
        // 근데 일단 실패를 봐야 하니까 아무 Account나 임의로 안에 넣어두고
        // logs 테스트 메소드 안에서만
    }

    @PostMapping("/test")
    public LogsResDTO test(@RequestParam("userId") Long userId){
        log.info("userId : "+userId);
        logService.fetchAndSaveLogs(); // 데베에 거래 내역 들어감
        return logService.log(userId);
    }
}
