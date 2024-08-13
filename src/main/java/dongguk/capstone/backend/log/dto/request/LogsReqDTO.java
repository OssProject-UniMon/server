package dongguk.capstone.backend.log.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsReqDTO {
    private String startDate;
    private String endDate;
//    private String bankAccountNum;
//    private String cardNum;
}
