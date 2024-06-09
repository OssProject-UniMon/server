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
    @Column(name = "user_id", nullable = false)
    private Long userId; // 스케쥴에서 fk인 user_id를 사용하면 NOT NULL인데, 회원가입을 해서 user를 만들면 기본값이 없다는 오류가 발생한다.
    // schedule 테이블에 데이터를 삽입할 때 발생하는 오류는 기본 키 제약 조건과 외래 키 참조 때문에 발생할 가능성이 큽니다. schedule 테이블의 user_id 열은 user 테이블의 user_id 열을 참조하고 있으며, user 테이블의 user_id 열은 NOT NULL 및 AUTO_INCREMENT로 정의되어 있습니다. 이는 user 테이블에 새로운 사용자가 추가될 때 데이터베이스에 의해 자동으로 생성되며, NULL일 수 없음을 의미합니다.

    @Column(length = 100)
    private String title;

    @Column(name = "start_time", length = 100)
    private String startTime;

    @Column(name = "end_time", length = 100)
    private String endTime;

    @Column(length = 100)
    private String day;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
