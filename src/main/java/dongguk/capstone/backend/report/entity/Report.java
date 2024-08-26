package dongguk.capstone.backend.report.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "report")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date", columnDefinition = "varchar(25)")
    private String date;

    @Lob
    @Column(name = "advice", columnDefinition = "text")
    private String advice;

    @Column(name = "total_budget")
    private Long totalBudget;

    @Column(name = "entertainment_budget")
    private Long entertainmentBudget;

    @Column(name = "culture_budget")
    private Long cultureBudget;

    @Column(name = "cafe_budget")
    private Long cafeBudget;

    @Column(name = "sports_budget")
    private Long sportsBudget;

    @Column(name = "food_budget")
    private Long foodBudget;

    @Column(name = "accommodation_budget")
    private Long accommodationBudget;

    @Column(name = "retail_budget")
    private Long retailBudget;

    @Column(name = "shopping_budget")
    private Long shoppingBudget;

    @Column(name = "transfer_budget")
    private Long transferBudget;

    @Column(name = "transportation_budget")
    private Long transportationBudget;

    @Column(name = "medical_budget")
    private Long medicalBudget;

    @Column(name = "insurance_budget")
    private Long insuranceBudget;

    @Column(name = "sub_budget")
    private Long subBudget;

    @Column(name = "edu_budget")
    private Long eduBudget;

    @Column(name = "mobile_budget")
    private Long mobileBudget;

    @Column(name = "others_budget")
    private Long othersBudget;
}
