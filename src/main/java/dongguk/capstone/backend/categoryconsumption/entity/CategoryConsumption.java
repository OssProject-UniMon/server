package dongguk.capstone.backend.categoryconsumption.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "category_consumption")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_consumption_id")
    private Long categoryConsumptionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private String date;

    @Column(name = "is_last_category_consumption", columnDefinition = "TINYINT(1)")
    private Boolean isLastCategoryConsumption;

    @Column(name = "entertainment_consumption")
    private Long entertainmentConsumption;

    @Column(name = "culture_consumption")
    private Long cultureConsumption;

    @Column(name = "cafe_consumption")
    private Long cafeConsumption;

    @Column(name = "sports_consumption")
    private Long sportsConsumption;

    @Column(name = "food_consumption")
    private Long foodConsumption;

    @Column(name = "accommodation_consumption")
    private Long accommodationConsumption;

    @Column(name = "retail_consumption")
    private Long retailConsumption;

    @Column(name = "shopping_consumption")
    private Long shoppingConsumption;

    @Column(name = "transfer_consumption")
    private Long transferConsumption;

    @Column(name = "transportation_consumption")
    private Long transportationConsumption;

    @Column(name = "medical_consumption")
    private Long medicalConsumption;

    @Column(name = "insurance_consumption")
    private Long insuranceConsumption;

    @Column(name = "sub_consumption")
    private Long subConsumption;

    @Column(name = "edu_consumption")
    private Long eduConsumption;

    @Column(name = "mobile_consumption")
    private Long mobileConsumption;

    @Column(name = "others_consumption")
    private Long othersConsumption;
}
