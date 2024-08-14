package dongguk.capstone.backend.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleReqPlusDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String day;
}
