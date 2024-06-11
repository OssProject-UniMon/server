package dongguk.capstone.backend.jobdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JobReadyResponseDTO {
    private final int serverCode;
}
