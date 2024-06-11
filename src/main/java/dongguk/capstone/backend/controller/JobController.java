package dongguk.capstone.backend.controller;

import dongguk.capstone.backend.jobdto.JobReadyResponseDTO;
import dongguk.capstone.backend.jobdto.JobRequestDTO;
import dongguk.capstone.backend.jobdto.JobResponseDTO;
import dongguk.capstone.backend.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/job")
@Slf4j
public class JobController {
    private final JobService jobService;

    @GetMapping("/ready")
    public JobReadyResponseDTO ready(){
        jobService.recommendReady();
        return new JobReadyResponseDTO(1);
    }

    @PostMapping("/recommend")
    public JobResponseDTO recommend(@RequestParam("userId") Long userId, @RequestBody JobRequestDTO jobRequestDTO){
        return jobService.recommend(userId, jobRequestDTO);
    }
}
