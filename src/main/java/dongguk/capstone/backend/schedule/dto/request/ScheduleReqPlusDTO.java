package dongguk.capstone.backend.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleReqPlusDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String day;
}
