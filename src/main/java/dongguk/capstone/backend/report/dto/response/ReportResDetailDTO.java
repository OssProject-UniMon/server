package dongguk.capstone.backend.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResDetailDTO {
    @Schema(description = "요청 Month의 최고 소비 카테고리")
    private String highestCategory;

    @Schema(description = "요청 Month의 최고 소비 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private double highestCategoryPercent;

    @Schema(description = "요청 Month의 최저 소비 카테고리")
    private String lowestCategory;

    @Schema(description = "요청 Month의 최저 소비 카테고리의 전 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %")
    private double lowestCategoryPercent;

    @Schema(description = "오락 카테고리의 변동 %")
    private double entertainmentCategoryChangePercent;

    @Schema(description = "오락 카테고리의 예산 (목표 금액)")
    private long entertainmentCategoryBudget;

    @Schema(description = "오락 카테고리의 소비량 (예산과 비교한 %)")
    private double entertainmentCategoryUsePercent;

    // Culture Category
    @Schema(description = "문화 카테고리의 변동 %")
    private double cultureCategoryChangePercent;

    @Schema(description = "문화 카테고리의 예산 (목표 금액)")
    private long cultureCategoryBudget;

    @Schema(description = "문화 카테고리의 소비량 (예산과 비교한 %)")
    private double cultureCategoryUsePercent;

    // Cafe Category
    @Schema(description = "카페 카테고리의 변동 %")
    private double cafeCategoryChangePercent;

    @Schema(description = "카페 카테고리의 예산 (목표 금액)")
    private long cafeCategoryBudget;

    @Schema(description = "카페 카테고리의 소비량 (예산과 비교한 %)")
    private double cafeCategoryUsePercent;

    // Sports Category
    @Schema(description = "스포츠 카테고리의 변동 %")
    private double sportsCategoryChangePercent;

    @Schema(description = "스포츠 카테고리의 예산 (목표 금액)")
    private long sportsCategoryBudget;

    @Schema(description = "스포츠 카테고리의 소비량 (예산과 비교한 %)")
    private double sportsCategoryUsePercent;

    // Food Category
    @Schema(description = "음식점 카테고리의 변동 %")
    private double foodCategoryChangePercent;

    @Schema(description = "음식점 카테고리의 예산 (목표 금액)")
    private long foodCategoryBudget;

    @Schema(description = "음식점 카테고리의 소비량 (예산과 비교한 %)")
    private double foodCategoryUsePercent;

    // Accommodation Category
    @Schema(description = "숙박비 카테고리의 변동 %")
    private double accommodationCategoryChangePercent;

    @Schema(description = "숙박비 카테고리의 예산 (목표 금액)")
    private long accommodationCategoryBudget;

    @Schema(description = "숙박비 카테고리의 소비량 (예산과 비교한 %)")
    private double accommodationCategoryUsePercent;

    // Retail Category
    @Schema(description = "잡화소매 카테고리의 변동 %")
    private double retailCategoryChangePercent;

    @Schema(description = "잡화소매 카테고리의 예산 (목표 금액)")
    private long retailCategoryBudget;

    @Schema(description = "잡화소매 카테고리의 소비량 (예산과 비교한 %)")
    private double retailCategoryUsePercent;

    // Shopping Category
    @Schema(description = "쇼핑 카테고리의 변동 %")
    private double shoppingCategoryChangePercent;

    @Schema(description = "쇼핑 카테고리의 예산 (목표 금액)")
    private long shoppingCategoryBudget;

    @Schema(description = "쇼핑 카테고리의 소비량 (예산과 비교한 %)")
    private double shoppingCategoryUsePercent;

    // Transfer Category
    @Schema(description = "이체 카테고리의 변동 %")
    private double transferCategoryChangePercent;

    @Schema(description = "이체 카테고리의 예산 (목표 금액)")
    private long transferCategoryBudget;

    @Schema(description = "이체 카테고리의 소비량 (예산과 비교한 %)")
    private double transferCategoryUsePercent;

    // Transportation Category
    @Schema(description = "교통비 카테고리의 변동 %")
    private double transportationCategoryChangePercent;

    @Schema(description = "교통비 카테고리의 예산 (목표 금액)")
    private long transportationCategoryBudget;

    @Schema(description = "교통비 카테고리의 소비량 (예산과 비교한 %)")
    private double transportationCategoryUsePercent;

    // Medical Category
    @Schema(description = "의료비 카테고리의 변동 %")
    private double medicalCategoryChangePercent;

    @Schema(description = "의료비 카테고리의 예산 (목표 금액)")
    private long medicalCategoryBudget;

    @Schema(description = "의료비 카테고리의 소비량 (예산과 비교한 %)")
    private double medicalCategoryUsePercent;

    // Insurance Category
    @Schema(description = "보험비 카테고리의 변동 %")
    private double insuranceCategoryChangePercent;

    @Schema(description = "보험비 카테고리의 예산 (목표 금액)")
    private long insuranceCategoryBudget;

    @Schema(description = "보험비 카테고리의 소비량 (예산과 비교한 %)")
    private double insuranceCategoryUsePercent;

    // Subscriptions Category
    @Schema(description = "구독/정기결제 카테고리의 변동 %")
    private double subCategoryChangePercent;

    @Schema(description = "구독/정기결제 카테고리의 예산 (목표 금액)")
    private long subCategoryBudget;

    @Schema(description = "구독/정기결제 카테고리의 소비량 (예산과 비교한 %)")
    private double subCategoryUsePercent;

    // Education Category
    @Schema(description = "교육비 카테고리의 변동 %")
    private double eduCategoryChangePercent;

    @Schema(description = "교육비 카테고리의 예산 (목표 금액)")
    private long eduCategoryBudget;

    @Schema(description = "교육비 카테고리의 소비량 (예산과 비교한 %)")
    private double eduCategoryUsePercent;

    // Mobile Category
    @Schema(description = "모바일페이 카테고리의 변동 %")
    private double mobileCategoryChangePercent;

    @Schema(description = "모바일페이 카테고리의 예산 (목표 금액)")
    private long mobileCategoryBudget;

    @Schema(description = "모바일페이 카테고리의 소비량 (예산과 비교한 %)")
    private double mobileCategoryUsePercent;

    // Others Category
    @Schema(description = "기타 카테고리의 변동 %")
    private double othersCategoryChangePercent;

    @Schema(description = "기타 카테고리의 예산 (목표 금액)")
    private long othersCategoryBudget;

    @Schema(description = "기타 카테고리의 소비량 (예산과 비교한 %)")
    private double othersCategoryUsePercent;
}
