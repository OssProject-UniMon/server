package dongguk.capstone.backend.dailyconsumption.service;

import dongguk.capstone.backend.dailyconsumption.entity.DailyConsumption;
import dongguk.capstone.backend.dailyconsumption.repository.DailyConsumptionRepository;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DailyConsumptionServiceImpl implements DailyConsumptionService{
    private final LogRepository logRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;

    @Override
    public void saveDailyConsumption(Long userId, String logDate) {
        try {
            LocalDate logLocalDate = LocalDate.parse(logDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 하루를 뺀 날짜 계산
            LocalDate previousDay = logLocalDate.minusDays(1);
            // 저번 달의 같은 날짜 계산
            LocalDate lastMonthSameDay = previousDay.minusMonths(1);

            // 하루를 뺀 날짜를 다시 String으로 변환
            String previousDayString = previousDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String lastMonthSameDayString = lastMonthSameDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 현재 날짜의 소비량을 가져오기
            List<Log> logList = logRepository.findLogsByUserIdAndDay(userId, previousDayString);
            long totalWithdraw = logList.stream()
                    .mapToLong(log -> {
                        String withdraw = log.getWithdraw();
                        return withdraw != null && !withdraw.isEmpty() ? Long.parseLong(withdraw) : 0L;
                    })
                    .sum();

            // 저번 달의 같은 날짜 소비량 가져오기
            DailyConsumption lastMonthConsumption = dailyConsumptionRepository.findDailyConsumptionByUserIdAndDate(userId, lastMonthSameDayString)
                    .orElse(null);

            // 소비 변화 비율 계산
            int consumptionChangePercentage = calculatePercentageChange(totalWithdraw, lastMonthConsumption != null ? lastMonthConsumption.getConsumption() : 0);

            // DailyConsumption 객체 생성 및 저장
            DailyConsumption dailyConsumption = DailyConsumption.builder()
                    .userId(userId)
                    .date(previousDayString)
                    .consumption(totalWithdraw)
                    .consumptionChangePercentage(consumptionChangePercentage)
                    .isLastConsumption(lastMonthConsumption != null)
                    .build();

            dailyConsumptionRepository.save(dailyConsumption);
        } catch (Exception e) {
            log.error("[DailyConsumptionService] saveDailyConsumption error : ", e);
        }
    }

    private Integer calculatePercentageChange(Long currentAmount, Long lastMonthAmount) {
        if (lastMonthAmount == null || lastMonthAmount == 0) {
            return currentAmount == null ? 0 : 9999999; // 0% 또는 9999999%로 반환
        }
        long difference = (currentAmount != null ? currentAmount : 0) - lastMonthAmount;
        return (int) Math.round((double) difference / lastMonthAmount * 100);
    }
}
