package dongguk.capstone.backend.scholarship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipResListDTO {
    private String name;
    private String amount;
    private String target;
    private String due;
    private String url;
}
