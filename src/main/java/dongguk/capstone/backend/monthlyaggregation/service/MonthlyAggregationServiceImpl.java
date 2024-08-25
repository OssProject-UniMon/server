package dongguk.capstone.backend.monthlyaggregation.service;

import dongguk.capstone.backend.categoryconsumption.entity.CategoryConsumption;
import dongguk.capstone.backend.categoryconsumption.repository.CategoryConsumptionRepository;
import dongguk.capstone.backend.dailyconsumption.repository.DailyConsumptionRepository;
import dongguk.capstone.backend.monthlyaggregation.entity.MonthlyAggregation;
import dongguk.capstone.backend.monthlyaggregation.repository.MonthlyAggregationRepository;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonthlyAggregationServiceImpl implements MonthlyAggregationService{
    private final UserRepository userRepository;
    private final CategoryConsumptionRepository categoryConsumptionRepository;
    private final MonthlyAggregationRepository monthlyAggregationRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;

    // 한 달이 지나면 월별 집계 테이블에 지난 달의 누적 소비량 저장
    // 1. 한 달 마다 마지막 날까지의 누적 총 소비량을 MonthlyAggregation으로 보내고, 0원으로 초기화 하는 로직
    // 2. 한 달마다 모든 카테고리의 소비량을 MonthlyAggregation으로 보내고, 0원으로 초기화 하는 로직
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void saveLastMonthConsumption() {
        try {
            // 지난 달을 yyyyMM 형식으로 변환
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String lastMonthString = lastMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

            // 모든 유저에 대해 지난 달의 누적 소비량을 저장
            List<User> userList = userRepository.findAll();
            for (User user : userList) {
                // 각 사용자의 nowTotalConsumption을 가져옴
                Long lastMonthTotalDailyConsumption = user.getNowTotalConsumption();

                // 지난 달 해당 사용자에 대한 모든 CategoryConsumption 항목 가져오기
                List<CategoryConsumption> categoryConsumptions = categoryConsumptionRepository
                        .findCategoryConsumptionsByUserIdAndMonth(user.getUserId(), lastMonthString);

                // 카테고리별 소비량 합산
                long totalEntertainmentConsumption = 0;
                long totalCultureConsumption = 0;
                long totalCafeConsumption = 0;
                long totalSportsConsumption = 0;
                long totalFoodConsumption = 0;
                long totalAccommodationConsumption = 0;
                long totalRetailConsumption = 0;
                long totalShoppingConsumption = 0;
                long totalTransferConsumption = 0;
                long totalTransportationConsumption = 0;
                long totalMedicalConsumption = 0;
                long totalInsuranceConsumption = 0;
                long totalSubConsumption = 0;
                long totalEduConsumption = 0;
                long totalMobileConsumption = 0;
                long totalOthersConsumption = 0;

                // 카테고리별로 합계 계산
                for (CategoryConsumption consumption : categoryConsumptions) {
                    totalEntertainmentConsumption += consumption.getEntertainmentConsumption();
                    totalCultureConsumption += consumption.getCultureConsumption();
                    totalCafeConsumption += consumption.getCafeConsumption();
                    totalSportsConsumption += consumption.getSportsConsumption();
                    totalFoodConsumption += consumption.getFoodConsumption();
                    totalAccommodationConsumption += consumption.getAccommodationConsumption();
                    totalRetailConsumption += consumption.getRetailConsumption();
                    totalShoppingConsumption += consumption.getShoppingConsumption();
                    totalTransferConsumption += consumption.getTransferConsumption();
                    totalTransportationConsumption += consumption.getTransportationConsumption();
                    totalMedicalConsumption += consumption.getMedicalConsumption();
                    totalInsuranceConsumption += consumption.getInsuranceConsumption();
                    totalSubConsumption += consumption.getSubConsumption();
                    totalEduConsumption += consumption.getEduConsumption();
                    totalMobileConsumption += consumption.getMobileConsumption();
                    totalOthersConsumption += consumption.getOthersConsumption();
                }

                // MonthlyAggregation 객체 생성 (달마다 집계내기 위해)
                MonthlyAggregation monthlyAggregation = MonthlyAggregation.builder()
                        .userId(user.getUserId()) // 사용자 ID 설정
                        .date(lastMonthString) // 지난 달의 날짜 설정
                        .monthlyTotalConsumption(lastMonthTotalDailyConsumption) // 월별 총 소비량 설정
                        .monthlyTotalEntertainmentConsumption(totalEntertainmentConsumption)
                        .monthlyTotalCultureConsumption(totalCultureConsumption)
                        .monthlyTotalCafeConsumption(totalCafeConsumption)
                        .monthlyTotalSportsConsumption(totalSportsConsumption)
                        .monthlyTotalFoodConsumption(totalFoodConsumption)
                        .monthlyTotalAccommodationConsumption(totalAccommodationConsumption)
                        .monthlyTotalRetailConsumption(totalRetailConsumption)
                        .monthlyTotalShoppingConsumption(totalShoppingConsumption)
                        .monthlyTotalTransferConsumption(totalTransferConsumption)
                        .monthlyTotalTransportationConsumption(totalTransportationConsumption)
                        .monthlyTotalMedicalConsumption(totalMedicalConsumption)
                        .monthlyTotalInsuranceConsumption(totalInsuranceConsumption)
                        .monthlyTotalSubConsumption(totalSubConsumption)
                        .monthlyTotalEduConsumption(totalEduConsumption)
                        .monthlyTotalMobileConsumption(totalMobileConsumption)
                        .monthlyTotalOthersConsumption(totalOthersConsumption)
                        .build();

                // MonthlyAggregation 저장
                monthlyAggregationRepository.save(monthlyAggregation);

                // 각 사용자의 nowTotalConsumption을 0으로 초기화
                user.setNowTotalConsumption(0L);
                userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("[UserService] saveLastMonthConsumption error : ", e);
        }
    }
}
