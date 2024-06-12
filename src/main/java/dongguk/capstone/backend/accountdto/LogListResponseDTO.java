package dongguk.capstone.backend.accountdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogListResponseDTO {
    private String deposit;
    private String withdraw;
    private String balance;
    private String date;
    private String useStoreName;
    private String category;
}
