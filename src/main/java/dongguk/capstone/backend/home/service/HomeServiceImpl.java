package dongguk.capstone.backend.home.service;

import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
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

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

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
                    .consumption(user.getDailyConsumption()) // 지금까지의 소비량
                    .build();
        } catch (Exception e) {
            log.error("[HomeService] home error : ", e);
            throw new RuntimeException(e);
        }
    }
}

