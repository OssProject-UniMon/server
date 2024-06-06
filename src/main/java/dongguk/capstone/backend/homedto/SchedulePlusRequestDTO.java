package dongguk.capstone.backend.homedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulePlusRequestDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String day;
}
