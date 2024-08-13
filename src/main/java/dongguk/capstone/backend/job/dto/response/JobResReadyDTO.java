package dongguk.capstone.backend.job.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JobResReadyDTO {
    private final int serverCode;
}
