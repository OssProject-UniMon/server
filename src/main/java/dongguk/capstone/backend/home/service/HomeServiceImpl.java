package dongguk.capstone.backend.home.service;

import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import dongguk.capstone.backend.categoryconsumption.repository.CategoryConsumptionRepository;
import dongguk.capstone.backend.home.dto.response.MonitoringResDTO;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.report.entity.Report;
import dongguk.capstone.backend.report.repository.ReportRepository;
import dongguk.capstone.backend.report.service.ReportService;
import dongguk.capstone.backend.report.service.ReportServiceImpl;
import dongguk.capstone.backend.schedule.entity.Schedule;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.schedule.repository.ScheduleRepository;
import dongguk.capstone.backend.schedule.dto.ScheduleListDTO;
import dongguk.capstone.backend.home.dto.response.HomeResDTO;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService {
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReportRepository reportRepository;
    private final CategoryConsumptionRepository categoryConsumptionRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeResDTO home(Long userId) {
        try {
            List<ScheduleListDTO> list = new ArrayList<>();

            // 사용자의 스케쥴
            List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);
            for (Schedule schedule : schedules) {
                ScheduleListDTO scheduleListDTO = new ScheduleListDTO();
                scheduleListDTO.setTitle(schedule.getTitle());
                scheduleListDTO.setStartTime(schedule.getStartTime());
                scheduleListDTO.setEndTime(schedule.getEndTime());
                scheduleListDTO.setDay(schedule.getDay());
                list.add(scheduleListDTO);
            }

            return HomeResDTO.builder()
                    .scheduleList(list)
                    .build();
        } catch (Exception e) {
            log.error("[HomeService] home error : ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MonitoringResDTO monitoring(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        LocalDate currentMonth = LocalDate.now();
        String stringCurrentMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        Report report = reportRepository.findReportByUserIdAndDate(userId,stringCurrentMonth)
                .orElse(null);

        // gpt가 짜준 총 예산에 비해 소비량이 몇 %인지 계산한 결과 % (이번 달)
        int totalConsumptionPercent = reportService.totalConsumptionPercent(userId, stringCurrentMonth);

        // 현재 달의 모든 소비량 데이터 조회
        List<CategoryConsumption> currentMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, stringCurrentMonth);

        // 현재 달의 각 카테고리별 총 소비량 계산
        Map<String, Long> nowCategoryTotals = new HashMap<>();
        for (CategoryConsumption consumption : currentMonthConsumptions) {
            nowCategoryTotals.merge("오락", consumption.getEntertainmentConsumption(), Long::sum);
            nowCategoryTotals.merge("문화", consumption.getCultureConsumption(), Long::sum);
            nowCategoryTotals.merge("카페", consumption.getCafeConsumption(), Long::sum);
            nowCategoryTotals.merge("스포츠", consumption.getSportsConsumption(), Long::sum);
            nowCategoryTotals.merge("음식점", consumption.getFoodConsumption(), Long::sum);
            nowCategoryTotals.merge("숙박비", consumption.getAccommodationConsumption(), Long::sum);
            nowCategoryTotals.merge("잡화소매", consumption.getRetailConsumption(), Long::sum);
            nowCategoryTotals.merge("쇼핑", consumption.getShoppingConsumption(), Long::sum);
            nowCategoryTotals.merge("개인이체", consumption.getTransferConsumption(), Long::sum);
            nowCategoryTotals.merge("교통비", consumption.getTransportationConsumption(), Long::sum);
            nowCategoryTotals.merge("의료비", consumption.getMedicalConsumption(), Long::sum);
            nowCategoryTotals.merge("보험비", consumption.getInsuranceConsumption(), Long::sum);
            nowCategoryTotals.merge("구독/정기결제", consumption.getSubConsumption(), Long::sum);
            nowCategoryTotals.merge("교육비", consumption.getEduConsumption(), Long::sum);
            nowCategoryTotals.merge("모바일페이", consumption.getMobileConsumption(), Long::sum);
            nowCategoryTotals.merge("기타", consumption.getOthersConsumption(), Long::sum);
        }
        log.info("nowCategoryTotals : " + nowCategoryTotals);

        // 최고 소비량 카테고리 찾기
        Map.Entry<String, Long> highestCategory = nowCategoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("No consumption data available"));
        log.info("highestCategory : " + highestCategory);

        // 최고 소비 카테고리의 이번 달 예산 가져오기
        Long highestCategoryBudget = getBudgetByCategoryName(report, highestCategory.getKey());

        // 최고 소비 카테고리의 이번 달 소비 총액
        Long highestCategoryConsumption = highestCategory.getValue();

        int highestCategoryPercent;
        if (highestCategoryBudget != null && highestCategoryBudget > 0) {
            highestCategoryPercent = (int) Math.round(((double) highestCategoryConsumption / highestCategoryBudget) * 100);
        } else {
            highestCategoryPercent = 0;
        }

        return MonitoringResDTO.builder()
                .consumption(user.getNowTotalConsumption()) // 지금까지의 소비량
                .totalConsumptionPercent(totalConsumptionPercent)
                .highestCategory(highestCategory.getKey())
                .highestCategoryPercent(highestCategoryPercent)
                .build();
    }

    private Long getBudgetByCategoryName(Report report, String categoryName) {
        if(report == null){
            return null;
        }
        switch (categoryName) {
            case "오락":
                return report.getEntertainmentBudget();
            case "문화":
                return report.getCultureBudget();
            case "카페":
                return report.getCafeBudget();
            case "스포츠":
                return report.getSportsBudget();
            case "음식점":
                return report.getFoodBudget();
            case "숙박비":
                return report.getAccommodationBudget();
            case "잡화소매":
                return report.getRetailBudget();
            case "쇼핑":
                return report.getShoppingBudget();
            case "개인이체":
                return report.getTransferBudget();
            case "교통비":
                return report.getTransportationBudget();
            case "의료비":
                return report.getMedicalBudget();
            case "보험비":
                return report.getInsuranceBudget();
            case "구독/정기결제":
                return report.getSubBudget();
            case "교육비":
                return report.getEduBudget();
            case "모바일페이":
                return report.getMobileBudget();
            default:
                return report.getOthersBudget();
        }
    }
}

