package dongguk.capstone.backend.home.dto.response;

import dongguk.capstone.backend.schedule.dto.ScheduleListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeResDTO {
    private List<ScheduleListDTO> scheduleList;
}
