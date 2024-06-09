package dongguk.capstone.backend.domain;

import dongguk.capstone.backend.serializable.ScheduleEmbedded;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class Schedule {

<<<<<<< Updated upstream
    @EmbeddedId // 복합키 설정
=======
    @EmbeddedId
>>>>>>> Stashed changes
    private ScheduleEmbedded scheduleEmbedded;

    @Column(length = 100)
    private String title;

    @Column(name = "start_time", length = 100)
    private String startTime;

    @Column(name = "end_time", length = 100)
    private String endTime;

    @Column(length = 100)
    private String day;

<<<<<<< Updated upstream
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
=======
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
>>>>>>> Stashed changes
    private User user;
}
