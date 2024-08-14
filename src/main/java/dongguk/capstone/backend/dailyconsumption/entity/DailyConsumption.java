package dongguk.capstone.backend.dailyconsumption.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
