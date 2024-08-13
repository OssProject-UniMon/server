package dongguk.capstone.backend.scholarship.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipUserDetailDTO {
    private String major;
    private double grade;
    private String gender;
    private String incomeBracket;
    private int scholarshipStatus;
    private String district;
}
