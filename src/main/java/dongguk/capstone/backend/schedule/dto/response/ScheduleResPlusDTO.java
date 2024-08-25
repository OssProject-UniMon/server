package dongguk.capstone.backend.schedule.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class ScheduleResPlusDTO {
    private final int serverCode;
}
