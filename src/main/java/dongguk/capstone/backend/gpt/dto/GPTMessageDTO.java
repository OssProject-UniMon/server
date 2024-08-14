package dongguk.capstone.backend.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// swagger 작성 필요 x
public class GPTMessageDTO {
    private String role;
    private String content;
}
