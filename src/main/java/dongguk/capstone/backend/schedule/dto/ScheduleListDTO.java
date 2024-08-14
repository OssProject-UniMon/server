package dongguk.capstone.backend.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String day;
}
