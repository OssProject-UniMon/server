package dongguk.capstone.backend.service;

import dongguk.capstone.backend.domain.Schedule;
import dongguk.capstone.backend.domain.User;
import dongguk.capstone.backend.homedto.SchedulePlusRequestDTO;
import dongguk.capstone.backend.repository.ScheduleRepository;
import dongguk.capstone.backend.homedto.ScheduleListDTO;
import dongguk.capstone.backend.homedto.ScheduleResponseDTO;
import dongguk.capstone.backend.repository.UserRepository;
import dongguk.capstone.backend.serializable.LogEmbedded;
import dongguk.capstone.backend.serializable.ScheduleEmbedded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HomeService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    /**
     * 메인 화면
     * @param userId
     * @return ScheduleListDTO
     */
    public ScheduleResponseDTO home(Long userId) {
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
        List<ScheduleListDTO> list = new ArrayList<>();
        Optional<User> user = userRepository.findById(userId); // User 엔티티 조회
        if(user.isPresent()){
            List<Schedule> schedules = scheduleRepository.findByScheduleEmbeddedUserId(userId); // User ID를 기준으로 스케줄 조회
            for(Schedule schedule : schedules) {
                ScheduleListDTO scheduleListDTO = new ScheduleListDTO();
                scheduleListDTO.setTitle(schedule.getTitle());
                scheduleListDTO.setStartTime(schedule.getStartTime());
                scheduleListDTO.setEndTime(schedule.getEndTime());
                scheduleListDTO.setDay(schedule.getDay());
                list.add(scheduleListDTO);
            }
        }
        scheduleResponseDTO.setScheduleList(list);
        return scheduleResponseDTO;
    }


    public int plus(Long userId, SchedulePlusRequestDTO schedulePlusRequestDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ScheduleEmbedded scheduleEmbedded = new ScheduleEmbedded();
            // userId에 따라 scheduleId 값을 설정
            Long scheduleId = scheduleRepository.findMaxScheduleIdByUserId(userId); // 해당 userId의 최대 scheduleId를 가져옴
            if (scheduleId == null) {
                scheduleId = 1L; // 최대 scheduleId가 없으면 1로 초기화
            } else {
                scheduleId++; // 최대 scheduleId가 있으면 1 증가
            }
            scheduleEmbedded.setScheduleId(scheduleId);
            scheduleEmbedded.setUserId(userId);
            Schedule schedule = new Schedule();
            schedule.setScheduleEmbedded(scheduleEmbedded);
            schedule.setUser(user);
            schedule.setTitle(schedulePlusRequestDTO.getTitle());
            schedule.setStartTime(schedulePlusRequestDTO.getStartTime());
            schedule.setEndTime(schedulePlusRequestDTO.getEndTime());
            schedule.setDay(schedulePlusRequestDTO.getDay());
            scheduleRepository.save(schedule);
            return 1;
        }
        return 0;
    }

}
