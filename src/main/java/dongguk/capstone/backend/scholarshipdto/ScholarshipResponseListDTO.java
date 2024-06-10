package dongguk.capstone.backend.scholarshipdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipResponseListDTO {
    private String name;
    private String amount;
    private String target;
    private String due;
    private String url;
}
