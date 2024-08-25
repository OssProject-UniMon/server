package dongguk.capstone.backend.dailyconsumption.service;

import java.time.LocalDate;

public interface DailyConsumptionService {
    // 여기서는 매일마다 사용자의 일별 소비량 저장해야 한다
    void saveDailyConsumption(Long userId, String logDate);
}
