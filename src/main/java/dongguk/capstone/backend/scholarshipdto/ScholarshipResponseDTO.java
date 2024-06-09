package dongguk.capstone.backend.scholarshipdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipResponseDTO {
    private List<ScholarshipDetailListDTO> scholarshipList;
}
