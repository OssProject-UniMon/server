package dongguk.capstone.backend.schedule.service;

import dongguk.capstone.backend.schedule.dto.request.ScheduleReqPlusDTO;

public interface ScheduleService {
    int plus(Long userId, ScheduleReqPlusDTO scheduleReqPlusDTO);
}
