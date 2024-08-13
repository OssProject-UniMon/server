package dongguk.capstone.backend.home.service;

import dongguk.capstone.backend.home.dto.response.HomeResDTO;

public interface HomeService {
    HomeResDTO home(Long userId);
}
