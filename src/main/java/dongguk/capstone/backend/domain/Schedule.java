package dongguk.capstone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long scheduleId;

    // Removed this field as it is not needed for FK mapping when using @ManyToOne
    // @Column(name="user_id", insertable = false, updatable = false)
    // private Long userId;

    @Column(length = 100)
    private String title;

    @Column(name = "start_time", length = 100)
    private String startTime;

    @Column(name = "end_time", length = 100)
    private String endTime;

    @Column(length = 100)
    private String day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
