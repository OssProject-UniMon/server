package dongguk.capstone.backend.log.entity;

import dongguk.capstone.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 거래 내역을 저장하는 엔티티 (API 계속해서 호출하는 문제를 막기 위해)
 *ㅋ
 * 나중에는 거래 내역이 추가되면(또는 계좌 내역 조회를 진행하면) LedgerStorage 업데이트
 * => 업데이트 해서 DB에 넣는 로직 생각해보자
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="log")
public class Log {
    @EmbeddedId
    private LogEmbedded logEmbedded;

    @Column(name="card_num")
    private String cardNum;

    @Column(name="deposit")
    private String deposit;

    @Column(name="withdraw")
    private String withdraw;

    @Column(name="balance")
    private String balance;

    @Column(name="date")
    private String date;

    @Column(name="use_store_name")
    private String useStoreName;

    @Column(name="category")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
