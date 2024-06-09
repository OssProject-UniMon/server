package dongguk.capstone.backend.controller;

import dongguk.capstone.backend.homedto.SchedulePlusRequestDTO;
import dongguk.capstone.backend.homedto.SchedulePlusResponseDTO;
import dongguk.capstone.backend.homedto.ScheduleResponseDTO;
import dongguk.capstone.backend.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Slf4j
public class HomeController {
    private final HomeService homeService;

    @GetMapping("")
    public ScheduleResponseDTO home(@RequestParam("userId") Long userId){
        return homeService.home(userId);
    }

    @PostMapping("/plus")
    public SchedulePlusResponseDTO plus(@RequestParam("userId") Long userId, @RequestBody SchedulePlusRequestDTO schedulePlusRequestDTO){
        int result = homeService.plus(userId, schedulePlusRequestDTO);
        return new SchedulePlusResponseDTO(result);
    }
}
