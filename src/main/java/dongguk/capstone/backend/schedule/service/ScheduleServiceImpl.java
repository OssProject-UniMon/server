package dongguk.capstone.backend.schedule.service;

import dongguk.capstone.backend.schedule.dto.request.ScheduleReqPlusDTO;
import dongguk.capstone.backend.schedule.entity.Schedule;
import dongguk.capstone.backend.schedule.repository.ScheduleRepository;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public int plus(Long userId, ScheduleReqPlusDTO scheduleReqPlusDTO) {
        Schedule schedule = new Schedule();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            schedule.setUserId(userId);
            schedule.setTitle(scheduleReqPlusDTO.getTitle());
            schedule.setStartTime(scheduleReqPlusDTO.getStartTime());
            schedule.setEndTime(scheduleReqPlusDTO.getEndTime());
            schedule.setDay(scheduleReqPlusDTO.getDay());
            scheduleRepository.save(schedule);
            return 1;
        }
        return 0;
    }
}
