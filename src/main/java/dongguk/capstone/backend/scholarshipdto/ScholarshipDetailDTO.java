package dongguk.capstone.backend.scholarshipdto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipDetailDTO {
    private List<ScholarshipDetailListDTO> scholarshipDetailList;
}
