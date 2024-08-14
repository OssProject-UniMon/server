package dongguk.capstone.backend.job.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "job")
public class Job {
    @Id
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 45)
    private String district;

    @Column(name = "withdraw_sum") // length는 255가 디폴트값이라 varchar(255)면 굳이 length 지정 안해줘도 된다.
    private String withdrawSum;

    @Column(name = "common_time_weekday")
    private String commonTimeWeekday;

    @Column(name = "common_time_weekend")
    private String commonTimeWeekend;
}
