package dongguk.capstone.backend.scholarship.dto.response;

import dongguk.capstone.backend.scholarship.dto.ScholarshipResListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipResRecommendDTO {
    private List<ScholarshipResListDTO> scholarshipList;
}
