package dongguk.capstone.backend.log.service;

import dongguk.capstone.backend.log.dto.response.LogsResDTO;

public interface LogService {
    LogsResDTO log(Long userId);
    void fetchAndSaveLogs();
}
