package dongguk.capstone.backend.dailyconsumption.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@Table(name = "daily_consumption")
@AllArgsConstructor
@NoArgsConstructor
public class DailyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_consumption_id")
    private Long dailyConsumptionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private String date;

    @Column(name = "consumption")
    private Long consumption;

    @Column(name = "consumption_change_percentage")
    private int consumptionChangePercentage;

    @Column(name = "is_last_consumption")
    private Boolean isLastConsumption;
}
