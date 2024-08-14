package dongguk.capstone.backend.scholarship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipDetailListDTO {
    private String name;
    private String target;
    private String due;
}
