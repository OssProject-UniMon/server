package dongguk.capstone.backend.report.service;

import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import dongguk.capstone.backend.categoryconsumption.repository.CategoryConsumptionRepository;
import dongguk.capstone.backend.dailyconsumption.entity.DailyConsumption;
import dongguk.capstone.backend.dailyconsumption.repository.DailyConsumptionRepository;
import dongguk.capstone.backend.gpt.service.GptService;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import dongguk.capstone.backend.monthlyaggregation.repository.MonthlyAggregationRepository;
import dongguk.capstone.backend.report.dto.response.ReportResDetailDTO;
import dongguk.capstone.backend.report.dto.response.ReportResSummaryDTO;
import dongguk.capstone.backend.report.entity.Report;
import dongguk.capstone.backend.report.repository.ReportRepository;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final GptService gptService;
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final ReportRepository reportRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;
    private final CategoryConsumptionRepository categoryConsumptionRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;

    // 자동으로 이번 달에 대해서 현재까지의 소비량 총합을 계산하여 저장하는 메소드, 매일마다 진행
    @Override
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * ?")
    public void currentConsumption(){
        // 모니터링 화면에서는 현재 달에 대해서 보여줘야 함
        // String 형식의 date에 맞추기 위해 LocalDate로 구한 현재 달을 변환
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String currentMonth = currentDate.format(formatter);

        // 이렇게 findAll로 얻은 userList를 가지고 logList를 만들어야 함
        List<User> userList = userRepository.findAll();

        // 각 사용자에 대한 소비량 계산 및 저장
        for (User user : userList) {
            // 현재 달에 대한 로그 조회
            List<Log> logList = logRepository.findLogsByUserIdAndMonth(user.getUserId(), currentMonth);

            // 로그의 withdraw들의 총합 계산
            Long sum = logList.stream()
                    .map(Log::getWithdraw)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::parseLong) // Long::parseLong 대신 Long::longValue 사용
                    .sum();

            user.setNowTotalConsumption(sum);
            userRepository.save(user); // 사용자 정보 저장
        }
    }

    /*-------------------------------- 요약 페이지 -----------------------------------*/

    @Override
    @Transactional
    public ReportResSummaryDTO reportSummaryPage(Long userId, String date){
        // 요약 보고서 페이지 처음에는 이번 달에 대해 보여줘야 하니까 이번 달을 넘겨주고,
        // 이후에 달 이동할 때는 이동한 달을 넘겨주면 될 것 같다. => 프론트한테 말하기
        // 현재 달 계산
        LocalDate currentMonth = LocalDate.now();
        String stringCurrentMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String dateMonth = date.substring(0, 6); // date에서 yyyyMM 부분만 추출

        if (stringCurrentMonth.equals(dateMonth)) {
            return nowSummaryPage(userId, date);
        } else {
            return pastSummaryPage(userId, date);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    // 모든 유저에 대해 GPT 조언 업데이트
    public void updateGptAdviceForAllUsers() {
        log.info("[ReportServiceImpl] updateGptAdviceForAllUsers");
        try {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                log.info("userId : "+user.getUserId());

                LocalDate today = LocalDate.now();
                String stringToday= today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                // 어제의 날짜를 구함
                LocalDate yesterday = today.minusDays(1);
                String stringYesterday = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                Report report;
                try {
                    report = reportRepository.findReportByUserIdAndDate(user.getUserId(), stringToday)
                            .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 이번 달 레포트가 없습니다."));
                } catch (IllegalArgumentException e) {
                    log.warn("해당되는 유저의 이번 달 레포트가 없습니다. userId: " + user.getUserId());
                    continue;
                }
                log.info("report : " + report);

                DailyConsumption dailyConsumption;
                try {
                    dailyConsumption = dailyConsumptionRepository.findDailyConsumptionByUserIdAndDate(user.getUserId(), stringYesterday)
                            .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 하루 전의 일별 소비량이 없습니다."));
                } catch (IllegalArgumentException e) {
                    log.warn("해당되는 유저의 하루 전의 일별 소비량이 없습니다. userId: " + user.getUserId());
                    continue;
                }
                // 현재 달의 카테고리 소비 데이터 가져오기
                List<CategoryConsumption> currentMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(user.getUserId(), stringToday.substring(0, 6));

                // 현재 달의 카테고리별 소비 총액 계산
                Map<String, Long> categoryTotals = new HashMap<>();
                for (CategoryConsumption consumption : currentMonthConsumptions) {
                    categoryTotals.merge("오락", consumption.getEntertainmentConsumption(), Long::sum);
                    categoryTotals.merge("문화", consumption.getCultureConsumption(), Long::sum);
                    categoryTotals.merge("카페", consumption.getCafeConsumption(), Long::sum);
                    categoryTotals.merge("스포츠", consumption.getSportsConsumption(), Long::sum);
                    categoryTotals.merge("음식점", consumption.getFoodConsumption(), Long::sum);
                    categoryTotals.merge("숙박비", consumption.getAccommodationConsumption(), Long::sum);
                    categoryTotals.merge("잡화소매", consumption.getRetailConsumption(), Long::sum);
                    categoryTotals.merge("쇼핑", consumption.getShoppingConsumption(), Long::sum);
                    categoryTotals.merge("개인이체", consumption.getTransferConsumption(), Long::sum);
                    categoryTotals.merge("교통비", consumption.getTransportationConsumption(), Long::sum);
                    categoryTotals.merge("의료비", consumption.getMedicalConsumption(), Long::sum);
                    categoryTotals.merge("보험비", consumption.getInsuranceConsumption(), Long::sum);
                    categoryTotals.merge("구독/정기결제", consumption.getSubConsumption(), Long::sum);
                    categoryTotals.merge("교육비", consumption.getEduConsumption(), Long::sum);
                    categoryTotals.merge("모바일페이", consumption.getMobileConsumption(), Long::sum);
                    categoryTotals.merge("기타", consumption.getOthersConsumption(), Long::sum);
                }

                // 최고 소비 카테고리 찾기
                Map.Entry<String, Long> highestCategory = categoryTotals.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow(() -> new RuntimeException("소비 데이터가 없습니다."));

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

                log.info("highestCategoryChange : "+highestCategoryPercent);

                // GPT 조언 생성
                String gptAdvice = gptService.gptAdvice(
                        user.getNowTotalConsumption(),
                        nowTotalConsumptionPercent(user.getUserId(), stringToday),
                        dailyConsumption.getConsumptionChangePercentage(),
                        highestCategory.getKey(),
                        highestCategoryPercent);

                log.info("gptAdvice : "+gptAdvice);

                log.info("user.getNowTotalConsumption() : "+user.getNowTotalConsumption());

                // 레포트에 GPT 조언 저장
                report.setAdvice(gptAdvice);
                reportRepository.save(report);
            }
        } catch (Exception e) {
            log.error("[ReportService] updateGptAdviceForAllUsers error : ", e);
        }
    }

    // 카테고리 이름에 따라 Report에서 예산 값을 가져오는 헬퍼 메소드
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

    // 기존 nowSummaryPage 메소드
    @Transactional
    public ReportResSummaryDTO nowSummaryPage(Long userId, String date) {
        log.info("[ReportService] nowSummaryPage");
        try {
            log.info("date : " + date);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저가 없습니다."));

            DailyConsumption dailyConsumption = dailyConsumptionRepository.findDailyConsumptionByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 일별 소비량이 없습니다."));

            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 해당 월의 레포트가 없습니다."));

            log.info("report : " + report);

            // 현재 달의 모든 소비량 데이터 조회
            List<CategoryConsumption> currentMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionByUserIdAndDay(userId, date);

            log.info("currentMonthConsumptions " + currentMonthConsumptions);

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

            log.info("nowCategoryTotals " + nowCategoryTotals);

            // 최고 소비량 카테고리 찾기
            Map.Entry<String, Long> highestCategory = nowCategoryTotals.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));
            log.info("highestCategory : " + highestCategory);

            // 최저 소비량 카테고리 찾기
            Map.Entry<String, Long> lowestCategory = nowCategoryTotals.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));
            log.info("lowestCategory  : " + lowestCategory);

            // 저번 달 같은 날짜 구하기
            String lastMonthDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("lastMonthDate  : " + lastMonthDate);

            // 저번 달 같은 날짜의 CategoryConsumption 조회
            List<CategoryConsumption> lastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionByUserIdAndDay(userId, lastMonthDate);

            // 최고/최저 소비 카테고리에 대해 저번 달 같은 날짜의 CategoryConsumption의 소비량 조회
            Long lastMonthHighestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, highestCategory.getKey());
            Long lastMonthLowestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, lowestCategory.getKey());
            log.info("lastMonthHighestCategoryAmount  : " + lastMonthHighestCategoryAmount);
            log.info("lastMonthLowestCategoryAmount  : " + lastMonthLowestCategoryAmount);

            // 최고/최저 소비 카테고리 몇 % 증가/감소 계산
            Integer highestCategoryPercentageChange = calculatePercentageChange(highestCategory.getValue(), lastMonthHighestCategoryAmount);
            Integer lowestCategoryPercentageChange = calculatePercentageChange(lowestCategory.getValue(), lastMonthLowestCategoryAmount);
            log.info("highestCategoryPercentageChange  : " + highestCategoryPercentageChange);
            log.info("lowestCategoryPercentageChange  : " + lowestCategoryPercentageChange);

            return ReportResSummaryDTO.builder()
                    .totalConsumption(user.getNowTotalConsumption()) // 이번 달의 현재까지 쓴 소비량
                    .percentageOfBudget(nowTotalConsumptionPercent(userId, date)) // 예산에 비해 소비량이 몇 %인지 계산한 결과 %
                    .lastConsumption(dailyConsumption.getIsLastConsumption()) // 저번 달의 소비량이 있는지 boolean
                    .percentageChange(dailyConsumption.getConsumptionChangePercentage()) // 저번 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %
                    .gptAdvice(report.getAdvice())
                    .highestCategory(highestCategory.getKey())
                    .highestCategoryPercent((highestCategoryPercentageChange != null) ? highestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .lowestCategory(lowestCategory.getKey())
                    .lowestCategoryPercent((lowestCategoryPercentageChange != null) ? lowestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .build();

        } catch (Exception e) {
            log.error("[ReportService] nowSummaryPage error : ", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ReportResSummaryDTO pastSummaryPage(Long userId, String date) {
        try {
            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserIdAndMonth(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 월간 집계 데이터가 없습니다."));

            DailyConsumption dailyConsumption = dailyConsumptionRepository.findDailyConsumptionByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 일별 소비량이 없습니다."));

            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 해당 월의 레포트가 없습니다."));

            // 현재 달의 모든 소비량 데이터 조회
            List<CategoryConsumption> pastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, date);

            // 현재 달의 각 카테고리별 총 소비량 계산
            Map<String, Long> pastCategoryTotals = new HashMap<>();
            for (CategoryConsumption consumption : pastMonthConsumptions) {
                pastCategoryTotals.merge("오락", consumption.getEntertainmentConsumption(), Long::sum);
                pastCategoryTotals.merge("문화", consumption.getCultureConsumption(), Long::sum);
                pastCategoryTotals.merge("카페", consumption.getCafeConsumption(), Long::sum);
                pastCategoryTotals.merge("스포츠", consumption.getSportsConsumption(), Long::sum);
                pastCategoryTotals.merge("음식점", consumption.getFoodConsumption(), Long::sum);
                pastCategoryTotals.merge("숙박비", consumption.getAccommodationConsumption(), Long::sum);
                pastCategoryTotals.merge("잡화소매", consumption.getRetailConsumption(), Long::sum);
                pastCategoryTotals.merge("쇼핑", consumption.getShoppingConsumption(), Long::sum);
                pastCategoryTotals.merge("개인이체", consumption.getTransferConsumption(), Long::sum);
                pastCategoryTotals.merge("교통비", consumption.getTransportationConsumption(), Long::sum);
                pastCategoryTotals.merge("의료비", consumption.getMedicalConsumption(), Long::sum);
                pastCategoryTotals.merge("보험비", consumption.getInsuranceConsumption(), Long::sum);
                pastCategoryTotals.merge("구독/정기결제", consumption.getSubConsumption(), Long::sum);
                pastCategoryTotals.merge("교육비", consumption.getEduConsumption(), Long::sum);
                pastCategoryTotals.merge("모바일페이", consumption.getMobileConsumption(), Long::sum);
                pastCategoryTotals.merge("기타", consumption.getOthersConsumption(), Long::sum);
            }

            // 최고 소비량 카테고리 찾기
            Map.Entry<String, Long> highestCategory = pastCategoryTotals.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));

            // 최저 소비량 카테고리 찾기
            Map.Entry<String, Long> lowestCategory = pastCategoryTotals.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));

            // 저번 달 같은 날짜 구하기
            String lastMonthDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 저번 달 같은 날짜의 CategoryConsumption 조회
            List<CategoryConsumption> lastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, lastMonthDate);

            // 최고/최저 소비 카테고리에 대해 저번 달 같은 날짜의 CategoryConsumption의 소비량 조회
            Long lastMonthHighestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, highestCategory.getKey());
            Long lastMonthLowestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, lowestCategory.getKey());

            // 최고/최저 소비 카테고리 몇 % 증가/감소 계산
            Integer highestCategoryPercentageChange = calculatePercentageChange(highestCategory.getValue(), lastMonthHighestCategoryAmount);
            Integer lowestCategoryPercentageChange = calculatePercentageChange(lowestCategory.getValue(), lastMonthLowestCategoryAmount);

            return ReportResSummaryDTO.builder()
                    .totalConsumption(monthlyAggregation.getMonthlyTotalConsumption()) // 지난 달의 현재까지 쓴 소비량
                    .percentageOfBudget(pastTotalConsumptionPercent(userId, date)) // 예산에 비해 소비량이 몇 %인지 계산한 결과 %
                    .lastConsumption(dailyConsumption.getIsLastConsumption()) // 저번 달의 소비량이 있는지 boolean
                    .percentageChange(dailyConsumption.getConsumptionChangePercentage()) // 저번 달의 동일한 날짜에 저장된 총 소비량과 비교하여 몇 % 증가/감소했는지에 대한 %
                    .gptAdvice(report.getAdvice())
                    .highestCategory(highestCategory.getKey())
                    .highestCategoryPercent((highestCategoryPercentageChange != null) ? highestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .lowestCategory(lowestCategory.getKey())
                    .lowestCategoryPercent((lowestCategoryPercentageChange != null) ? lowestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .build();
        } catch (Exception e) {
            log.error("[ReportService] pastSummaryPage error : ", e);
            throw new RuntimeException(e);
        }
    }

    private Integer calculatePercentageChange(Long currentAmount, Long lastMonthAmount) {
        if (lastMonthAmount == null || lastMonthAmount == 0) {
            return currentAmount == null ? 0 : 9999999; // 0% 또는 9999999%로 반환
        }
        long difference = (currentAmount != null ? currentAmount : 0) - lastMonthAmount;
        return (int) Math.round((double) difference / lastMonthAmount * 100);
    }



    /*-------------------------------- 상세 페이지 -----------------------------------*/

    @Override
    @Transactional
    public ReportResDetailDTO reportDetailPage(Long userId, String date){
        // 상세 페이지에서도 요약 페이지와 비슷하게 진행
        LocalDate currentMonth = LocalDate.now();
        String stringCurrentMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String dateMonth = date.substring(0, 6); // date에서 yyyyMM 부분만 추출

        if (stringCurrentMonth.equals(dateMonth)) {
            return nowDetailPage(userId, date);
        } else {
            return pastDetailPage(userId, date);
        }
    }

    @Transactional
    public ReportResDetailDTO nowDetailPage(Long userId, String date) {
        try {
            log.info("date : " + date);

            // 현재 달의 모든 소비량 데이터 조회
            List<CategoryConsumption> currentMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, date.substring(0, 6));

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

            // 최저 소비량 카테고리 찾기
            Map.Entry<String, Long> lowestCategory = nowCategoryTotals.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));
            log.info("lowestCategory : " + lowestCategory);

            // 저번 달 같은 날짜 구하기
            String lastMonthDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("lastMonthDate : " + lastMonthDate);

            // 저번 달 같은 날짜의 CategoryConsumption 조회
            List<CategoryConsumption> lastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, lastMonthDate.substring(0, 6));

            // 최고/최저 소비 카테고리에 대해 저번 달 같은 날짜의 CategoryConsumption의 소비량 조회
            Long lastMonthHighestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, highestCategory.getKey());
            Long lastMonthLowestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, lowestCategory.getKey());
            log.info("lastMonthHighestCategoryAmount  : " + lastMonthHighestCategoryAmount);
            log.info("lastMonthLowestCategoryAmount  : " + lastMonthLowestCategoryAmount);

            // 최고/최저 소비 카테고리 몇 % 증가/감소 계산
            Integer highestCategoryPercentageChange = calculatePercentageChange(highestCategory.getValue(), lastMonthHighestCategoryAmount);
            Integer lowestCategoryPercentageChange = calculatePercentageChange(lowestCategory.getValue(), lastMonthLowestCategoryAmount);
            log.info("highestCategoryPercentageChange  : " + highestCategoryPercentageChange);
            log.info("lowestCategoryPercentageChange  : " + lowestCategoryPercentageChange);

            // 카테고리별 변동 % 계산
            Map<String, Integer> categoryChangePercentMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : nowCategoryTotals.entrySet()) {
                Long lastMonthAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, entry.getKey());
                Integer changePercent = calculatePercentageChange(entry.getValue(), lastMonthAmount);
                categoryChangePercentMap.put(entry.getKey(), changePercent);
            }
            log.info("categoryChangePercentMap : " + categoryChangePercentMap);

            // 카테고리 예산 조회를 위한 Report
            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 대한 예산 정보가 없습니다."));
            log.info("report : " + report);

            // 카테고리 예산 조회
            Map<String, Integer> categoryBudgetMap = new HashMap<>();
            categoryBudgetMap.put("오락", report.getEntertainmentBudget().intValue());
            categoryBudgetMap.put("문화", report.getCultureBudget().intValue());
            categoryBudgetMap.put("카페", report.getCafeBudget().intValue());
            categoryBudgetMap.put("스포츠", report.getSportsBudget().intValue());
            categoryBudgetMap.put("음식점", report.getFoodBudget().intValue());
            categoryBudgetMap.put("숙박비", report.getAccommodationBudget().intValue());
            categoryBudgetMap.put("잡화소매", report.getRetailBudget().intValue());
            categoryBudgetMap.put("쇼핑", report.getShoppingBudget().intValue());
            categoryBudgetMap.put("개인이체", report.getTransferBudget().intValue());
            categoryBudgetMap.put("교통비", report.getTransportationBudget().intValue());
            categoryBudgetMap.put("의료비", report.getMedicalBudget().intValue());
            categoryBudgetMap.put("보험비", report.getInsuranceBudget().intValue());
            categoryBudgetMap.put("구독/정기결제", report.getSubBudget().intValue());
            categoryBudgetMap.put("교육비", report.getEduBudget().intValue());
            categoryBudgetMap.put("모바일페이", report.getMobileBudget().intValue());
            categoryBudgetMap.put("기타", report.getOthersBudget().intValue());
            log.info("categoryBudgetMap : " + categoryBudgetMap);

            // 카테고리 사용률 계산
            Map<String, Integer> categoryUsePercentMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : nowCategoryTotals.entrySet()) {
                Integer budget = categoryBudgetMap.get(entry.getKey());
                if (budget == null || budget == 0) {
                    budget = 1; // 제로 디비전 방지
                }
                int usePercent = (int)(entry.getValue() / (double) budget * 100);
                categoryUsePercentMap.put(entry.getKey(), usePercent);
            }
            log.info("categoryUsePercentMap : " + categoryUsePercentMap);

            // ReportResDetailDTO 생성 및 반환
            return ReportResDetailDTO.builder()
                    .highestCategory(highestCategory.getKey())
                    .highestCategoryPercent((highestCategoryPercentageChange != null) ? highestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .lowestCategory(lowestCategory.getKey())
                    .lowestCategoryPercent((lowestCategoryPercentageChange != null) ? lowestCategoryPercentageChange : 999999) // null 체크 후 기본값 999999 할당
                    .entertainmentCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "오락"))
                    .entertainmentCategoryBudget(categoryBudgetMap.get("오락"))
                    .entertainmentCategoryUsePercent(categoryUsePercentMap.get("오락"))
                    .cultureCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "문화"))
                    .cultureCategoryBudget(categoryBudgetMap.get("문화"))
                    .cultureCategoryUsePercent(categoryUsePercentMap.get("문화"))
                    .cafeCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "카페"))
                    .cafeCategoryBudget(categoryBudgetMap.get("카페"))
                    .cafeCategoryUsePercent(categoryUsePercentMap.get("카페"))
                    .sportsCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "스포츠"))
                    .sportsCategoryBudget(categoryBudgetMap.get("스포츠"))
                    .sportsCategoryUsePercent(categoryUsePercentMap.get("스포츠"))
                    .foodCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "음식점"))
                    .foodCategoryBudget(categoryBudgetMap.get("음식점"))
                    .foodCategoryUsePercent(categoryUsePercentMap.get("음식점"))
                    .accommodationCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "숙박비"))
                    .accommodationCategoryBudget(categoryBudgetMap.get("숙박비"))
                    .accommodationCategoryUsePercent(categoryUsePercentMap.get("숙박비"))
                    .retailCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "잡화소매"))
                    .retailCategoryBudget(categoryBudgetMap.get("잡화소매"))
                    .retailCategoryUsePercent(categoryUsePercentMap.get("잡화소매"))
                    .shoppingCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "쇼핑"))
                    .shoppingCategoryBudget(categoryBudgetMap.get("쇼핑"))
                    .shoppingCategoryUsePercent(categoryUsePercentMap.get("쇼핑"))
                    .transferCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "개인이체"))
                    .transferCategoryBudget(categoryBudgetMap.get("개인이체"))
                    .transferCategoryUsePercent(categoryUsePercentMap.get("개인이체"))
                    .transportationCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "교통비"))
                    .transportationCategoryBudget(categoryBudgetMap.get("교통비"))
                    .transportationCategoryUsePercent(categoryUsePercentMap.get("교통비"))
                    .medicalCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "의료비"))
                    .medicalCategoryBudget(categoryBudgetMap.get("의료비"))
                    .medicalCategoryUsePercent(categoryUsePercentMap.get("의료비"))
                    .insuranceCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "보험비"))
                    .insuranceCategoryBudget(categoryBudgetMap.get("보험비"))
                    .insuranceCategoryUsePercent(categoryUsePercentMap.get("보험비"))
                    .subCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "구독/정기결제"))
                    .subCategoryBudget(categoryBudgetMap.get("구독/정기결제"))
                    .subCategoryUsePercent(categoryUsePercentMap.get("구독/정기결제"))
                    .eduCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "교육비"))
                    .eduCategoryBudget(categoryBudgetMap.get("교육비"))
                    .eduCategoryUsePercent(categoryUsePercentMap.get("교육비"))
                    .mobileCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "모바일페이"))
                    .mobileCategoryBudget(categoryBudgetMap.get("모바일페이"))
                    .mobileCategoryUsePercent(categoryUsePercentMap.get("모바일페이"))
                    .othersCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "기타"))
                    .othersCategoryBudget(categoryBudgetMap.get("기타"))
                    .othersCategoryUsePercent(categoryUsePercentMap.get("기타"))
                    .build();
        } catch (Exception e) {
            log.error("Error generating report detail page", e);
            throw new RuntimeException("Error generating report detail page", e);
        }
    }

    private Long getCategoryAmountByCategoryName(List<CategoryConsumption> consumptions, String categoryName) {
        return consumptions.stream()
                .mapToLong(consumption -> {
                    switch (categoryName) {
                        case "오락":
                            return consumption.getEntertainmentConsumption();
                        case "문화":
                            return consumption.getCultureConsumption();
                        case "카페":
                            return consumption.getCafeConsumption();
                        case "스포츠":
                            return consumption.getSportsConsumption();
                        case "음식점":
                            return consumption.getFoodConsumption();
                        case "숙박비":
                            return consumption.getAccommodationConsumption();
                        case "잡화소매":
                            return consumption.getRetailConsumption();
                        case "쇼핑":
                            return consumption.getShoppingConsumption();
                        case "개인이체":
                            return consumption.getTransferConsumption();
                        case "교통비":
                            return consumption.getTransportationConsumption();
                        case "의료비":
                            return consumption.getMedicalConsumption();
                        case "보험비":
                            return consumption.getInsuranceConsumption();
                        case "구독/정기결제":
                            return consumption.getSubConsumption();
                        case "교육비":
                            return consumption.getEduConsumption();
                        case "모바일페이":
                            return consumption.getMobileConsumption();
                        default:
                            return consumption.getOthersConsumption();
                    }
                }).sum();
    }




    @Transactional
    public ReportResDetailDTO pastDetailPage(Long userId, String date) {
        try {
            // 지난 달의 월간 소비량 데이터 조회
            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserIdAndMonth(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저가 없습니다."));

            // 지난 달의 모든 소비량 데이터 조회
            List<CategoryConsumption> pastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, date);

            // 지난 달의 각 카테고리별 총 소비량 계산
            Map<String, Long> pastCategoryTotals = new HashMap<>();
            for (CategoryConsumption consumption : pastMonthConsumptions) {
                pastCategoryTotals.merge("오락", consumption.getEntertainmentConsumption(), Long::sum);
                pastCategoryTotals.merge("문화", consumption.getCultureConsumption(), Long::sum);
                pastCategoryTotals.merge("카페", consumption.getCafeConsumption(), Long::sum);
                pastCategoryTotals.merge("스포츠", consumption.getSportsConsumption(), Long::sum);
                pastCategoryTotals.merge("음식점", consumption.getFoodConsumption(), Long::sum);
                pastCategoryTotals.merge("숙박비", consumption.getAccommodationConsumption(), Long::sum);
                pastCategoryTotals.merge("잡화소매", consumption.getRetailConsumption(), Long::sum);
                pastCategoryTotals.merge("쇼핑", consumption.getShoppingConsumption(), Long::sum);
                pastCategoryTotals.merge("개인이체", consumption.getTransferConsumption(), Long::sum);
                pastCategoryTotals.merge("교통비", consumption.getTransportationConsumption(), Long::sum);
                pastCategoryTotals.merge("의료비", consumption.getMedicalConsumption(), Long::sum);
                pastCategoryTotals.merge("보험비", consumption.getInsuranceConsumption(), Long::sum);
                pastCategoryTotals.merge("구독/정기결제", consumption.getSubConsumption(), Long::sum);
                pastCategoryTotals.merge("교육비", consumption.getEduConsumption(), Long::sum);
                pastCategoryTotals.merge("모바일페이", consumption.getMobileConsumption(), Long::sum);
                pastCategoryTotals.merge("기타", consumption.getOthersConsumption(), Long::sum);
            }

            // 최고 소비량 카테고리 찾기
            Map.Entry<String, Long> highestCategory = pastCategoryTotals.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));

            // 최저 소비량 카테고리 찾기
            Map.Entry<String, Long> lowestCategory = pastCategoryTotals.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new RuntimeException("No consumption data available"));

            // 저번 달 같은 날짜 구하기
            String lastMonthDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 저번 달 같은 날짜의 CategoryConsumption 조회
            List<CategoryConsumption> lastMonthConsumptions = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, lastMonthDate.substring(0, 6));


            // 최고/최저 소비 카테고리에 대해 저번 달 같은 날짜의 CategoryConsumption의 소비량 조회
            Long lastMonthHighestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, highestCategory.getKey());
            Long lastMonthLowestCategoryAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, lowestCategory.getKey());

            // 최고/최저 소비 카테고리 몇 % 증가/감소 계산
            Integer highestCategoryPercentageChange = calculatePercentageChange(highestCategory.getValue(), lastMonthHighestCategoryAmount);
            Integer lowestCategoryPercentageChange = calculatePercentageChange(lowestCategory.getValue(), lastMonthLowestCategoryAmount);

            // 카테고리별 변동 % 계산
            Map<String, Integer> categoryChangePercentMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : pastCategoryTotals.entrySet()) {
                Long lastMonthAmount = getCategoryAmountByCategoryName(lastMonthConsumptions, entry.getKey());
                Integer changePercent = calculatePercentageChange(entry.getValue(), lastMonthAmount);
                categoryChangePercentMap.put(entry.getKey(), changePercent);
            }

            // 카테고리 예산 조회를 위한 Report
            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 대한 예산 정보가 없습니다."));

            // 카테고리 예산 조회
            Map<String, Integer> categoryBudgetMap = new HashMap<>();
            categoryBudgetMap.put("오락", report.getEntertainmentBudget().intValue());
            categoryBudgetMap.put("문화", report.getCultureBudget().intValue());
            categoryBudgetMap.put("카페", report.getCafeBudget().intValue());
            categoryBudgetMap.put("스포츠", report.getSportsBudget().intValue());
            categoryBudgetMap.put("음식점", report.getFoodBudget().intValue());
            categoryBudgetMap.put("숙박비", report.getAccommodationBudget().intValue());
            categoryBudgetMap.put("잡화소매", report.getRetailBudget().intValue());
            categoryBudgetMap.put("쇼핑", report.getShoppingBudget().intValue());
            categoryBudgetMap.put("개인이체", report.getTransferBudget().intValue());
            categoryBudgetMap.put("교통비", report.getTransportationBudget().intValue());
            categoryBudgetMap.put("의료비", report.getMedicalBudget().intValue());
            categoryBudgetMap.put("보험비", report.getInsuranceBudget().intValue());
            categoryBudgetMap.put("구독/정기결제", report.getSubBudget().intValue());
            categoryBudgetMap.put("교육비", report.getEduBudget().intValue());
            categoryBudgetMap.put("모바일페이", report.getMobileBudget().intValue());
            categoryBudgetMap.put("기타", report.getOthersBudget().intValue());
            log.info("categoryBudgetMap : " + categoryBudgetMap);

            // 카테고리 사용률 계산
            Map<String, Integer> categoryUsePercentMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : pastCategoryTotals.entrySet()) {
                Integer budget = categoryBudgetMap.get(entry.getKey());
                int usePercent = (int)(entry.getValue() / (double) budget) * 100;
                categoryUsePercentMap.put(entry.getKey(), usePercent);
            }

            // ReportResDetailDTO 생성 및 반환
            return ReportResDetailDTO.builder()
                    .highestCategory(highestCategory.getKey())
                    .highestCategoryPercent((highestCategoryPercentageChange != null) ? highestCategoryPercentageChange : 999999)
                    .lowestCategory(lowestCategory.getKey())
                    .lowestCategoryPercent((lowestCategoryPercentageChange != null) ? lowestCategoryPercentageChange : 999999)
                    .entertainmentCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "오락"))
                    .entertainmentCategoryBudget(categoryBudgetMap.get("오락"))
                    .entertainmentCategoryUsePercent(categoryUsePercentMap.get("오락"))
                    .cultureCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "문화"))
                    .cultureCategoryBudget(categoryBudgetMap.get("문화"))
                    .cultureCategoryUsePercent(categoryUsePercentMap.get("문화"))
                    .cafeCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "카페"))
                    .cafeCategoryBudget(categoryBudgetMap.get("카페"))
                    .cafeCategoryUsePercent(categoryUsePercentMap.get("카페"))
                    .sportsCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "스포츠"))
                    .sportsCategoryBudget(categoryBudgetMap.get("스포츠"))
                    .sportsCategoryUsePercent(categoryUsePercentMap.get("스포츠"))
                    .foodCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "음식점"))
                    .foodCategoryBudget(categoryBudgetMap.get("음식점"))
                    .foodCategoryUsePercent(categoryUsePercentMap.get("음식점"))
                    .accommodationCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "숙박비"))
                    .accommodationCategoryBudget(categoryBudgetMap.get("숙박비"))
                    .accommodationCategoryUsePercent(categoryUsePercentMap.get("숙박비"))
                    .retailCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "잡화소매"))
                    .retailCategoryBudget(categoryBudgetMap.get("잡화소매"))
                    .retailCategoryUsePercent(categoryUsePercentMap.get("잡화소매"))
                    .shoppingCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "쇼핑"))
                    .shoppingCategoryBudget(categoryBudgetMap.get("쇼핑"))
                    .shoppingCategoryUsePercent(categoryUsePercentMap.get("쇼핑"))
                    .transferCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "개인이체"))
                    .transferCategoryBudget(categoryBudgetMap.get("개인이체"))
                    .transferCategoryUsePercent(categoryUsePercentMap.get("개인이체"))
                    .transportationCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "교통비"))
                    .transportationCategoryBudget(categoryBudgetMap.get("교통비"))
                    .transportationCategoryUsePercent(categoryUsePercentMap.get("교통비"))
                    .medicalCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "의료비"))
                    .medicalCategoryBudget(categoryBudgetMap.get("의료비"))
                    .medicalCategoryUsePercent(categoryUsePercentMap.get("의료비"))
                    .insuranceCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "보험비"))
                    .insuranceCategoryBudget(categoryBudgetMap.get("보험비"))
                    .insuranceCategoryUsePercent(categoryUsePercentMap.get("보험비"))
                    .subCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "구독/정기결제"))
                    .subCategoryBudget(categoryBudgetMap.get("구독/정기결제"))
                    .subCategoryUsePercent(categoryUsePercentMap.get("구독/정기결제"))
                    .eduCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "교육비"))
                    .eduCategoryBudget(categoryBudgetMap.get("교육비"))
                    .eduCategoryUsePercent(categoryUsePercentMap.get("교육비"))
                    .mobileCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "모바일페이"))
                    .mobileCategoryBudget(categoryBudgetMap.get("모바일페이"))
                    .mobileCategoryUsePercent(categoryUsePercentMap.get("모바일페이"))
                    .othersCategoryChangePercent(getCategoryChangePercent(categoryChangePercentMap, "기타"))
                    .othersCategoryBudget(categoryBudgetMap.get("기타"))
                    .othersCategoryUsePercent(categoryUsePercentMap.get("기타"))
                    .build();
        } catch (Exception e) {
            log.error("[ReportService] pastDetailPage error : ", e);
            throw new RuntimeException(e);
        }
    }

    private Integer getCategoryChangePercent(Map<String, Integer> categoryChangePercentMap, String category) {
        return categoryChangePercentMap.getOrDefault(category, 999999);
    }

    /*-------------------------------- 소비 비율 계산 -----------------------------------*/
    @Override
    @Transactional
    public int totalConsumptionPercent(Long userId, String date){
        // 현재 달 계산
        LocalDate currentMonth = LocalDate.now();
        String stringCurrentMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String dateMonth = date.substring(0, 6); // date에서 yyyyMM 부분만 추출

        // 현재 달과 비교해서 date가 같으면 nowTotalConsumptionPercent,
        // date가 다르면 pastTotalConsumptionPercent
        if (stringCurrentMonth.equals(dateMonth)) {
            // 현재 달의 소비 비율 로직
            return nowTotalConsumptionPercent(userId, date);
        } else {
            // 과거 달의 소비 비율 로직
            return pastTotalConsumptionPercent(userId, date);
        }
    }

    // 전달받은 Month(yyyyMM)가 이번 달인 경우에, gpt가 짜준 총 예산과 현재 사용중인 nowTotalConsumption 비교해서
    // nowTotalConsumption가 몇 %인지 알려주기
    @Transactional
    public int nowTotalConsumptionPercent(Long userId, String date){
        log.info("[ReportService] nowTotalConsumptionPercent");
        try{
            log.info("userId : "+userId);
            log.info("date : "+date);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저가 없습니다."));

            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 레포트가 없습니다."));
            log.info("report : "+report);

            Long nowTotalConsumption = user.getNowTotalConsumption(); // 이번 달의 현재까지 쓴
            Long totalBudget = report.getTotalBudget();
            log.info("nowTotalConsumption : "+nowTotalConsumption);
            log.info("totalBudget : "+totalBudget);

            if (totalBudget == null || totalBudget == 0) {
                // 첫 달은 예산이 짜여지지 않아서 프론트에서 처리해줘야 할 듯
                return 0;
            }

            return (int) ((double) nowTotalConsumption / totalBudget * 100.0);
        } catch (Exception e){
            log.error("[ReportService] nowTotalConsumptionPercent error : ",e);
            throw new RuntimeException(e);
        }
    }

    // 전달받은 Month(yyyyMM)가 지난 달인 경우에, 그 달의 gpt가 짜준 총 예산과 MonthlyAggregation의 monthlyTotalConsumption 비교해서
    // monthlyTotalConsumption가 몇 %인지 알려주기
    @Transactional
    public int pastTotalConsumptionPercent(Long userId, String date){
        try{
            log.info("userId : "+userId);
            log.info("date : "+date);
            MonthlyAggregation monthlyAggregation = monthlyAggregationRepository.findMonthlyAggregationByUserIdAndMonth(userId,date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 월별 집계가 없습니다."));

            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 레포트가 없습니다."));

            Long monthlyTotalConsumption = monthlyAggregation.getMonthlyTotalConsumption();
            Long totalBudget = report.getTotalBudget();

            if (totalBudget == null || totalBudget == 0) {
                // 첫 달은 예산이 짜여지지 않아서 프론트에서 처리해줘야 할 듯
                return 0;
            }

            return (int) ((double) monthlyTotalConsumption / totalBudget * 100.0);
        } catch (Exception e){
            log.error("[ReportService] pastTotalConsumptionPercent error : ",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Map<String, Double> categoryConsumptionPercent(Long userId, String date) {
        log.info("[ReportService] categoryConsumptionPercent");
        try {
            log.info("userId : " + userId);
            log.info("date : " + date);

            // 유저의 해당 월 카테고리 소비 내역 가져오기
            List<CategoryConsumption> categoryConsumptionList = categoryConsumptionRepository.findCategoryConsumptionsByUserIdAndMonth(userId, date);

            // 유저의 해당 날짜의 레포트 가져오기
            Report report = reportRepository.findReportByUserIdAndDate(userId, date)
                    .orElseThrow(() -> new IllegalArgumentException("해당되는 유저의 레포트가 없습니다."));

            // 결과를 저장할 Map 생성
            Map<String, Double> result = new HashMap<>();

            // 각 카테고리별로 예산 대비 소비 비율 계산
            for (CategoryConsumption categoryConsumption : categoryConsumptionList) {
                calculateAndPutPercent(result, "오락", categoryConsumption.getEntertainmentConsumption(), report.getEntertainmentBudget());
                calculateAndPutPercent(result, "문화", categoryConsumption.getCultureConsumption(), report.getCultureBudget());
                calculateAndPutPercent(result, "카페", categoryConsumption.getCafeConsumption(), report.getCafeBudget());
                calculateAndPutPercent(result, "스포츠", categoryConsumption.getSportsConsumption(), report.getSportsBudget());
                calculateAndPutPercent(result, "음식점", categoryConsumption.getFoodConsumption(), report.getFoodBudget());
                calculateAndPutPercent(result, "숙박비", categoryConsumption.getAccommodationConsumption(), report.getAccommodationBudget());
                calculateAndPutPercent(result, "잡화소매", categoryConsumption.getRetailConsumption(), report.getRetailBudget());
                calculateAndPutPercent(result, "쇼핑", categoryConsumption.getShoppingConsumption(), report.getShoppingBudget());
                calculateAndPutPercent(result, "개인이체", categoryConsumption.getTransferConsumption(), report.getTransferBudget());
                calculateAndPutPercent(result, "교통비", categoryConsumption.getTransportationConsumption(), report.getTransportationBudget());
                calculateAndPutPercent(result, "의료비", categoryConsumption.getMedicalConsumption(), report.getMedicalBudget());
                calculateAndPutPercent(result, "보험비", categoryConsumption.getInsuranceConsumption(), report.getInsuranceBudget());
                calculateAndPutPercent(result, "구독/정기결제", categoryConsumption.getSubConsumption(), report.getSubBudget());
                calculateAndPutPercent(result, "교육비", categoryConsumption.getEduConsumption(), report.getEduBudget());
                calculateAndPutPercent(result, "모바일페이", categoryConsumption.getMobileConsumption(), report.getMobileBudget());
                calculateAndPutPercent(result, "기타", categoryConsumption.getOthersConsumption(), report.getOthersBudget());
            }

            log.info("result : " + result);
            return result;


        } catch (Exception e) {
            log.error("[ReportService] categoryConsumptionPercent error : ", e);
            throw new RuntimeException(e);
        }
    }

    // 소비 비율을 계산하고 결과 map에 추가하는 메서드
    private void calculateAndPutPercent(Map<String, Double> result, String category, Long consumption, Long budget) {
        if (budget != null && budget > 0) {
            double percent = (double) consumption / budget * 100.0;
            result.put(category, percent);
        } else {
            result.put(category, 0.0);
        }
    }
}