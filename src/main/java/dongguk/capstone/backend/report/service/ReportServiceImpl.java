package dongguk.capstone.backend.report.service;

import dongguk.capstone.backend.gpt.service.GptService;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final GptService gptService;

    // 자동으로 이번 달에 대해서 현재까지의 소비량 총합을 계산하여 저장하는 메소드, 매일마다 진행
    @Override
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * ?")
    public void currentConsumption(){
        // 모니터링 화면에서는 현재 달에 대해서 보여줘야 함
        // String 형식의 date에 맞추기 위해 LocalDate로 구한 현재 달을 변환
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentMonth = currentDate.format(formatter);

        // 이렇게 findAll로 얻은 userList를 가지고 logList를 만들어야 함
        List<User> userList = userRepository.findAll();

        // 각 사용자에 대한 소비량 계산 및 저장
        for (User user : userList) {
            // 현재 달에 대한 로그 조회
            List<Log> logList = logRepository.findLogsByUserIdAndMonth(user.getUserId(), currentMonth);

            // 로그의 withdraw들의 총합 계산
            Long sum = logList.stream()
                    .map(Log::getWithdraw)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::parseLong) // Long::parseLong 대신 Long::longValue 사용
                    .sum();

            // 이 매일매일의 소비량 총 합을 어떻게 저장할 것이지? => user의 now_total_consumption
            user.setNowTotalConsumption(sum);
            userRepository.save(user); // 사용자 정보 저장
        }
    }

}
