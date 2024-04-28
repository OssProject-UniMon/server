package dongguk.capstone.backend.controller;

import dongguk.capstone.backend.accountdto.AccountLogsRequestDTO;
import dongguk.capstone.backend.accountdto.AccountLogsResponseDTO;
import dongguk.capstone.backend.accountdto.AccountRegistRequestDTO;
import dongguk.capstone.backend.accountdto.AccountResponseDTO;
import dongguk.capstone.backend.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    // 거래 내역 조회를 위해 클라이언트에서 뭘 줘야할지를 논의해보자
    // API 명세서 부터 barobillservice 를 참고해서 작성하자

    // API EndPoint를 어떻게 설정해야 할까...
    // 무조건 바로빌에서 지정한대로 해야할까?
    // 아니면 우리 맘대로 해도 되나..?


    @PostMapping("/regist")
    public AccountResponseDTO accountRegist(@RequestParam("userId") Long userId, @RequestBody AccountRegistRequestDTO accountRegistRequestDTO){
        // 여기서 받은 userId를 어떻게 할 수 있을까?
        // 그리고 Account 도메인 내용 DB 스키마 만들어지면 채우기
        log.info("dto : {}" , accountRegistRequestDTO);
        int result = accountService.regist(accountRegistRequestDTO, userId); // 새로운 계좌여야 테스트 가능할 듯
        if(result < 0) // 계좌 등록 실패
            return new AccountResponseDTO(0);
        return new AccountResponseDTO(1); // 계좌 등록 성공
    }

    @GetMapping("/logs")
    public ResponseEntity<AccountLogsResponseDTO> accountLogs(@Valid @RequestParam("userId") Long userId, @RequestBody AccountLogsRequestDTO accountLogsRequestDTO){
        AccountLogsResponseDTO accountLogsResponseDTO = accountService.log(userId, accountLogsRequestDTO);
        // 만약 accountLogResponseDTO가 존재한다면, 200 ok 와 body로 accountLogsResponseDTO 전해주기
//        if(accountLogsResponseDTO!=null)
//            return ResponseEntity.ok().body(accountLogsResponseDTO);
//        else
//            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(accountLogsResponseDTO);

        // 테스트 진행
        // => 일단 계좌 정보가 들어간 Account가 있어야 함.
        // 근데 일단 실패를 봐야 하니까 아무 Account나 임의로 안에 넣어두고
        // logs 테스트 메소드 안에서만
    }

    
}





