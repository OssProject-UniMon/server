package dongguk.capstone.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dongguk.capstone.backend.scholarshipdto.ScholarshipResponseDTO;
import dongguk.capstone.backend.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scholarship")
@Slf4j
public class ScholarshipController {
    private final ScholarshipService
            scholarshipService;

    @GetMapping("/scrape")
    private void test(){
        scholarshipService.scrape();
    }

//    @PostMapping("/recommend")
//    private ScholarshipResponseDTO recommend(@RequestParam("userId") Long userId){
//        return scholarshipService.recommend(userId);
//    }

    @PostMapping("/recommend")
    private ScholarshipResponseDTO recommend(@RequestParam("userId") Long userId) throws IOException, InterruptedException {
        return scholarshipService.recommend(userId);
    }
}
