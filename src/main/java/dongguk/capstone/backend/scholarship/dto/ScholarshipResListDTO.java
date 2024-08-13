package dongguk.capstone.backend.scholarship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipResListDTO {
    private String name;
    private String amount;
    private String target;
    private String due;
    private String url;
}
