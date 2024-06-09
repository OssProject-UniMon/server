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

    /**
     * 메인 화면
     * @param userId
     * @return ScheduleListDTO
     */
    public ScheduleResponseDTO home(Long userId) {
        // 여기서 로그인한 이메일과 scheduleRepository에서의 이메일이 같은 애들을 들고오기
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
        List<ScheduleListDTO> list = new ArrayList<>();
        Optional<User> user = userRepository.findById(userId); // User 엔티티 조회
        List<Schedule> schedules = scheduleRepository.findAll();
        if(user.isPresent()){
            for(Schedule schedule : schedules) {
                if(user.get().getEmail().equals(schedule.getEmail())){
                    ScheduleListDTO scheduleListDTO = new ScheduleListDTO();
                    scheduleListDTO.setTitle(schedule.getTitle());
                    scheduleListDTO.setStartTime(schedule.getStartTime());
                    scheduleListDTO.setEndTime(schedule.getEndTime());
                    scheduleListDTO.setDay(schedule.getDay());
                    list.add(scheduleListDTO);
                }
            }
        }
        scheduleResponseDTO.setScheduleList(list);
        return scheduleResponseDTO;
    }

    public int plus(SchedulePlusRequestDTO schedulePlusRequestDTO) {
        Schedule schedule = new Schedule();

        schedule.setEmail(schedulePlusRequestDTO.getEmail());
        schedule.setTitle(schedulePlusRequestDTO.getTitle());
        schedule.setStartTime(schedulePlusRequestDTO.getStartTime());
        schedule.setEndTime(schedulePlusRequestDTO.getEndTime());
        schedule.setDay(schedulePlusRequestDTO.getDay());
        scheduleRepository.save(schedule);

        return 1;
    }
}
