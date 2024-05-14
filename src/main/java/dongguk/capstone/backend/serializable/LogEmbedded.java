package dongguk.capstone.backend.serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEmbedded implements Serializable {
    @Column(name="log_id")
    private Long logId;

    @Column(name="user_id")
    private Long userId;
}
