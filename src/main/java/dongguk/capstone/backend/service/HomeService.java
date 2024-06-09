package dongguk.capstone.backend.service;

import dongguk.capstone.backend.domain.Schedule;
import dongguk.capstone.backend.domain.User;
import dongguk.capstone.backend.homedto.SchedulePlusRequestDTO;
import dongguk.capstone.backend.repository.ScheduleRepository;
import dongguk.capstone.backend.homedto.ScheduleListDTO;
import dongguk.capstone.backend.homedto.ScheduleResponseDTO;
import dongguk.capstone.backend.repository.UserRepository;
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

    public ScheduleResponseDTO home(Long userId) {
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
        List<ScheduleListDTO> list = new ArrayList<>();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);
            for (Schedule schedule : schedules) {
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
        Schedule schedule = new Schedule();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
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

