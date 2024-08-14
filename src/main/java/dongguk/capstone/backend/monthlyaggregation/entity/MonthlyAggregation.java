package dongguk.capstone.backend.monthlyaggregation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "monthly_aggregation")
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_aggregation_id")
    private Long monthlyAggregationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private String date;

    @Column(name = "monthly_total_consumption")
    private Long monthlyTotalConsumption;

    @Column(name = "monthly_total_entertainment_consumption")
    private Long monthlyTotalEntertainmentConsumption;

    @Column(name = "monthly_total_culture_consumption")
    private Long monthlyTotalCultureConsumption;

    @Column(name = "monthly_total_cafe_consumption")
    private Long monthlyTotalCafeConsumption;

    @Column(name = "monthly_total_sports_consumption")
    private Long monthlyTotalSportsConsumption;

    @Column(name = "monthly_total_food_consumption")
    private Long monthlyTotalFoodConsumption;

    @Column(name = "monthly_total_accommodation_consumption")
    private Long monthlyTotalAccommodationConsumption;

    @Column(name = "monthly_total_retail_consumption")
    private Long monthlyTotalRetailConsumption;

    @Column(name = "monthly_total_shopping_consumption")
    private Long monthlyTotalShoppingConsumption;

    @Column(name = "monthly_total_transfer_consumption")
    private Long monthlyTotalTransferConsumption;

    @Column(name = "monthly_total_transportation_consumption")
    private Long monthlyTotalTransportationConsumption;

    @Column(name = "monthly_total_medical_consumption")
    private Long monthlyTotalMedicalConsumption;

    @Column(name = "monthly_total_insurance_consumption")
    private Long monthlyTotalInsuranceConsumption;

    @Column(name = "monthly_total_sub_consumption")
    private Long monthlyTotalSubConsumption;

    @Column(name = "monthly_total_edu_consumption")
    private Long monthlyTotalEduConsumption;

    @Column(name = "monthly_total_mobile_consumption")
    private Long monthlyTotalMobileConsumption;
}