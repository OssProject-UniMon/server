package dongguk.capstone.backend.home.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeResDTO home(Long userId) {
        HomeResDTO homeResDTO = new HomeResDTO();
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
        homeResDTO.setScheduleList(list);
        return homeResDTO;
    }
}

