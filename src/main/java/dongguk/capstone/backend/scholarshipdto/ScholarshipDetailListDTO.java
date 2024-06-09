package dongguk.capstone.backend.scholarshipdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipDetailListDTO {
    private String name;
    private String target;
    private String due;
}
