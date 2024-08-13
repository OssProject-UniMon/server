package dongguk.capstone.backend.job.service;

import dongguk.capstone.backend.job.dto.request.JobReqDTO;
import dongguk.capstone.backend.job.dto.response.JobResDTO;

public interface JobService {
    void fetchJobsAndRecommend();
    JobResDTO recommend(Long userId, JobReqDTO jobReqDTO);
}
