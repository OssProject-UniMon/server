package dongguk.capstone.backend.scholarship.service;

import dongguk.capstone.backend.scholarship.dto.response.ScholarshipResRecommendDTO;

import java.io.IOException;

public interface ScholarshipService {
    void scrape();
    ScholarshipResRecommendDTO recommend(Long userId) throws IOException, InterruptedException;
}
