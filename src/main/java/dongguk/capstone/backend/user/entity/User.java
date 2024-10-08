package dongguk.capstone.backend.user.entity;

import dongguk.capstone.backend.account.entity.Account;
import dongguk.capstone.backend.card.entity.Card;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity // 도메인 객체인 User에 JPA 엔티티 매핑을 하기 위해 @Entity 애노테이션 필요
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user") // 현재 MySQL의 table 이름이 user이므로 name을 설정해줘야 함
public class User {
    @Id // @Id 애노테이션이 붙은 바로 밑 필드가 pk가 되는 것
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA에서 엔티티의 기본 키를 자동으로 생성하는 전략 - 엔티티의 기본 키를 자동으로 생성하고 싶을 때 사용하는 설정
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 100) // @Column은 DB의 컬럼(속성)과 필드의 변수가 매핑되게 해준다
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 100)
    private String major;

    private double grade;

    @Column(length = 50)
    private String gender;

    // MySQL에서는 칼럼명을 Snake Case를 사용한다.
    // => incomeBracket과 scholarshipStatus는 MySQL에서 income_bracket, scholarship_status로 변환된다.
    // 이 때, @Column의 name을 통해 속성 이름에 맞게 설정할 수 있다.
    @Column(name = "income_bracket")
    private String incomeBracket;

    @Column(name = "scholarship_status")
    private int scholarshipStatus;

    @Column(length = 45)
    private String district;

    @Column(name = "account_status")
    private int accountStatus;

    @Column(name = "card_status")
    private int cardStatus;

    @Column(name = "now_total_consumption")
    private Long nowTotalConsumption;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Card> cards = new ArrayList<>();
}
