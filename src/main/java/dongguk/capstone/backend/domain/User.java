package dongguk.capstone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 도메인 객체인 User에 JPA 엔티티 매핑을 하기 위해 @Entity 애노테이션 필요
@Getter
@Setter
@Table(name = "user") // 현재 MySQL의 table 이름이 user이므로 name을 설정해줘야 함
public class User {
    @Id // @Id 애노테이션이 붙은 바로 밑 필드가 pk가 되는 것
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(length = 100) // @Column은 DB의 컬럼(속성)과 필드의 변수가 매핑되게 해준다
    private String name;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 100)
    private String major;

    private double grade;

    private int gender;

    // MySQL에서는 칼럼명을 Snake Case를 사용한다.
    // => incomeBracket과 scholarshipStatus는 MySQL에서 income_bracket, scholarship_status로 변환된다.
    // 이 때, @Column의 name을 통해 속성 이름에 맞게 설정할 수 있다.
    @Column(name = "income_bracket")
    private int incomeBracket;

    @Column(name = "scholarship_status")
    private int scholarshipStatus;

    @Column(length = 50, name = "housing_housing_type")
    private String housingHousingType;

    @Column(length = 45)
    private String district;

    @Column(name = "schedule_hour")
    private int scheduleHour;

    @Column(name = "schedule_minute")
    private int scheduleMinute;
}
