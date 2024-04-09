package dongguk.capstone.backend.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLogsRequestDTO {
    private String startDate;
    private String endDate;
}
