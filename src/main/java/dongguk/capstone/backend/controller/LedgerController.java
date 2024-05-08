package dongguk.capstone.backend.controller;

import dongguk.capstone.backend.accountdto.*;
import dongguk.capstone.backend.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class LedgerController {

    private final LedgerService ledgerService;
    private AccountRegistRequestDTO accountRegistRequestDTO; // 필드변수는 인스턴스 변수(클래스의 각 인스턴스마다 다른 값을 가지는 변수)이며, 그 클래스의 모든 메소드에서 사용 가능하다
    private CardRegistRequestDTO cardRegistRequestDTO;
    // 거래 내역 조회를 위해 클라이언트에서 뭘 줘야할지를 논의해보자
    // API 명세서 부터 barobillservice 를 참고해서 작성하자

    // API EndPoint를 어떻게 설정해야 할까...
    // 무조건 바로빌에서 지정한대로 해야할까?
    // 아니면 우리 맘대로 해도 되나..?


    /**
     * 계좌 등록
     * @param userId
     * @param accountRegistRequestDTO
     * @return
     */
    @PostMapping("/account-regist")
    public AccountResponseDTO accountRegist(@RequestParam("userId") Long userId, @RequestBody AccountRegistRequestDTO accountRegistRequestDTO) {
        // 여기서 받은 userId를 어떻게 할 수 있을까?
        // 그리고 Account 도메인 내용 DB 스키마 만들어지면 채우기
        log.info("dto : {}", accountRegistRequestDTO);
        this.accountRegistRequestDTO = accountRegistRequestDTO;
        int result = ledgerService.accountRegist(userId, accountRegistRequestDTO); // 새로운 계좌여야 테스트 가능할 듯
        if (result < 0) // 계좌 등록 실패
            return new AccountResponseDTO(0);
        return new AccountResponseDTO(1); // 계좌 등록 성공
    }

    /**
     * 카드 등록
     * @param userId
     * @param cardRegistRequestDTO
     * @return
     */
    @PostMapping("/card-regist")
    public CardRegistResponseDTO cardRegist(@RequestParam("userId") Long userId, @RequestBody CardRegistRequestDTO cardRegistRequestDTO) {
        this.cardRegistRequestDTO = cardRegistRequestDTO;
        int result = ledgerService.cardRegist(userId, cardRegistRequestDTO);
        if(result < 0)
            return new CardRegistResponseDTO(0);
        return new CardRegistResponseDTO(1);
    }

    /**
     * 거래 내역 조회
     * @param userId
     * @param logsRequestDTO
     * @return
     */
    @PostMapping("/logs")
    public ResponseEntity<LogsResponseDTO> log(@RequestParam("userId") Long userId, @RequestBody LogsRequestDTO logsRequestDTO) {
        // !!!!!!!!!!!!!!! 고치자 !!!!!!!!!!!!!!!
        // 내가 보기엔 accountRegist 메소드를 호출할 당시에는 accountRegistRequestDTO에 값이 있지만,
        // 그 이후에 log 메소드에서 사용할 때는 값이 적용되지 않아서 null 값이다.
        // ※ accountRegistRequestDTO와 cardRegistRequestDTO는 각각 계좌 및 카드를 등록할 때 사용되는 요청 DTO이며, 메소드 호출 사이에 상태가 유지되지 않습니다. ※
        // => 그래서 nullPointerException이 발생하는 듯..
        // 값을 유지할 수 있는 방법이 없나? 아닌가? 동시성 문제가 있으려나?
//        log.info("logs", accountRegistRequestDTO); // null
//        log.info("logs", cardRegistRequestDTO); // null
        // 아 여기서 accountRegistRequestDTO 이거랑 cardRegistRequestDTO 이거 쓰면 안되는건가?
        log.info("logsRequestDTO : {}", logsRequestDTO);
        LogsResponseDTO logsResponseDTO = ledgerService.log(userId, logsRequestDTO);
        // 만약 accountLogResponseDTO가 존재한다면, 200 ok 와 body로 accountLogsResponseDTO 전해주기
//        if(accountLogsResponseDTO!=null)
//            return ResponseEntity.ok().body(accountLogsResponseDTO);
//        else
//            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(logsResponseDTO);

        // 테스트 진행
        // => 일단 계좌 정보가 들어간 Account가 있어야 함.
        // 근데 일단 실패를 봐야 하니까 아무 Account나 임의로 안에 넣어두고
        // logs 테스트 메소드 안에서만
    }
}