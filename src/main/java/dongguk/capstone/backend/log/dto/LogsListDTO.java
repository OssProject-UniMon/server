package dongguk.capstone.backend.log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogsListDTO {
    private String deposit;
    private String withdraw;
    private String balance;
    private String date;
    private String useStoreName;
    private String useStoreCorpNum;
    private String category;
}
