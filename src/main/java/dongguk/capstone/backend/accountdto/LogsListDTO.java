package dongguk.capstone.backend.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogsListDTO {
    private String deposit;
    private String withdraw;
    private String balance;
    private String transDt;
    private String transType;
    private String transOffice;
    private String transRemark;
}
