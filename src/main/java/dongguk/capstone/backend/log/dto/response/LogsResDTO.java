package dongguk.capstone.backend.log.dto.response;

import dongguk.capstone.backend.log.dto.LogsListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsResDTO {
    private List<LogsListDTO> logList;
}
