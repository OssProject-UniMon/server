package dongguk.capstone.backend.homedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String day;
}
