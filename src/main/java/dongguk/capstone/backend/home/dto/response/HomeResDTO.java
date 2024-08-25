package dongguk.capstone.backend.home.dto.response;

import dongguk.capstone.backend.schedule.dto.ScheduleListDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeResDTO {
    @Schema(description = "사용자의 스케쥴 리스트")
    private List<ScheduleListDTO> scheduleList;
}
