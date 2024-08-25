package dongguk.capstone.backend.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long scheduleId;

    @Column(name="user_id")
    private Long userId;

    @Column(length = 100)
    private String title;

    @Column(name = "start_time", length = 100)
    private String startTime;

    @Column(name = "end_time", length = 100)
    private String endTime;

    @Column(length = 100)
    private String day;
}
