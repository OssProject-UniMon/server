package dongguk.capstone.backend.report.service;

import dongguk.capstone.backend.report.dto.response.ReportResDetailDTO;
import dongguk.capstone.backend.report.dto.response.ReportResSummaryDTO;

import java.util.Map;

public interface ReportService {
    // report(보고서)에서 해야 할 것

    // 현재의 전체 소비량 총합 구하기 (이번 달)
    void currentConsumption();

    // 요약 보고서 페이지
    ReportResSummaryDTO reportSummaryPage(Long userId, String date);

    // 상세 보고서 페이지
    ReportResDetailDTO reportDetailPage(Long userId, String date);

    // 총 예산과 비교해서 소비량 % 비교
    double totalConsumptionPercent(Long userId, String date);

    // 전달받은 Month(yyyyMM)가 이번 달인 경우에, gpt가 짜준 총 예산과 현재 사용중인 nowTotalConsumption 비교해서
    // nowTotalConsumption가 몇 %인지 알려주기
    double nowTotalConsumptionPercent(Long userId, String date);

    // 전달받은 Month(yyyyMM)가 지난 달인 경우에, 그 달의 gpt가 짜준 총 예산과 MonthlyAggregation의 monthlyTotalConsumption 비교해서
    // monthlyTotalConsumption가 몇 %인지 알려주기
    double pastTotalConsumptionPercent(Long userId, String date);

    // 전달받은 Month에 맞는 달의 gpt가 짜준 각 카테고리의 예산과 현재 사용중인 각 카테고리의 소비량을 비교해서
    // 각 카테고리별로 소비량이 몇 %인지 알려주기
    Map<String, Double> categoryConsumptionPercent(Long userId, String date);

    // yyyyMMdd를 전달받아 그 저번 달의 동일한 날짜와 소비량 비교하여 몇 %인지 알려주기


    // 조언은 어디에?
}
