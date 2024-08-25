package dongguk.capstone.backend.home.service;

import dongguk.capstone.backend.categoryconsumption.repository.CategoryConsumptionRepository;
import dongguk.capstone.backend.home.dto.response.MonitoringResDTO;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.report.repository.ReportRepository;
import dongguk.capstone.backend.report.service.ReportService;
import dongguk.capstone.backend.schedule.entity.Schedule;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.schedule.repository.ScheduleRepository;
import dongguk.capstone.backend.schedule.dto.ScheduleListDTO;
import dongguk.capstone.backend.home.dto.response.HomeResDTO;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService {
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeResDTO home(Long userId) {
        try {
            List<ScheduleListDTO> list = new ArrayList<>();

            // 사용자의 스케쥴
            List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);
            for (Schedule schedule : schedules) {
                ScheduleListDTO scheduleListDTO = new ScheduleListDTO();
                scheduleListDTO.setTitle(schedule.getTitle());
                scheduleListDTO.setStartTime(schedule.getStartTime());
                scheduleListDTO.setEndTime(schedule.getEndTime());
                scheduleListDTO.setDay(schedule.getDay());
                list.add(scheduleListDTO);
            }

            return HomeResDTO.builder()
                    .scheduleList(list)
                    .build();
        } catch (Exception e) {
            log.error("[HomeService] home error : ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MonitoringResDTO monitoring(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        LocalDate currentMonth = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");
        String stringCurrentMonth = currentMonth.format(dateTimeFormatter);

        // gpt가 짜준 총 예산에 비해 소비량이 몇 %인지 계산한 결과 % (이번 달)
        int totalConsumptionPercent = reportService.totalConsumptionPercent(userId, stringCurrentMonth);

        // gpt가 짜준 카테고리 예산에 비해 몇 %인지 계산한 결과 % (이번 달)
        Map<String, Double> categoryConsumptionPercent = reportService.categoryConsumptionPercent(userId, stringCurrentMonth);

        // 최대 값 찾기
        Map.Entry<String, Double> maxEntry = null;
        for (Map.Entry<String, Double> entry : categoryConsumptionPercent.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        // 최대 값을 가지는 카테고리
        String highestCategory = (maxEntry != null) ? maxEntry.getKey() : null;

        int highestCategoryPercent = (maxEntry != null) ? (int) Math.round(maxEntry.getValue()) : 0;

        return MonitoringResDTO.builder()
                .consumption(user.getNowTotalConsumption()) // 지금까지의 소비량
                .totalConsumptionPercent(totalConsumptionPercent)
                .highestCategory(highestCategory)
                .highestCategoryPercent(highestCategoryPercent)
                .build();
    }
}

