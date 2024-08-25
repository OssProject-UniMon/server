package dongguk.capstone.backend.home.service;

import dongguk.capstone.backend.home.dto.response.HomeResDTO;
import dongguk.capstone.backend.home.dto.response.MonitoringResDTO;

public interface HomeService {
    HomeResDTO home(Long userId);

    MonitoringResDTO monitoring(Long userId);
}
