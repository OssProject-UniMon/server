package dongguk.capstone.backend.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsResponseDTO {
    private List<LogsListDTO> logList;
}
