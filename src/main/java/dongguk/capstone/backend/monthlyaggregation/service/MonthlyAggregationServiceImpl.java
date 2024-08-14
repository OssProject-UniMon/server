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
    public void saveLastMonthConsumption(){
        try{
            // 지난 달을 yyyy-MM-dd 형식으로 변환
            LocalDate lastMonth = LocalDate.now().minusMonths(1); // 매 월 1일마다 진행하기 때문에, month에서 1을 빼줘야 함.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String lastMonthString = lastMonth.format(formatter);

            // 모든 유저에 대해 지난 달의 누적 소비량을 저장
            List<User> userList = userRepository.findAll();
            for(User user : userList){
                // 각 사용자의 nowTotalConsumption을 가져옴
                Long lastMonthTotalDailyConsumption = user.getNowTotalConsumption();

                // 각 사용자의 카테고리별 소비량을 가져옴
                CategoryConsumption categoryConsumption = categoryConsumptionRepository.findCategoryConsumptionByUserId(user.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("해당되는 유저가 없습니다."));

                // MonthlyAggregation 객체 생성 (달 마다 집계내기 위해)
                MonthlyAggregation monthlyAggregation = MonthlyAggregation.builder()
                        .userId(user.getUserId()) // 사용자 ID 설정
                        .date(lastMonthString) // 지난 달의 날짜 설정
                        .monthlyTotalConsumption(lastMonthTotalDailyConsumption) // 월별 총 소비량 설정
                        .monthlyTotalEntertainmentConsumption(categoryConsumption.getEntertainmentConsumption())
                        .monthlyTotalCultureConsumption(categoryConsumption.getCultureConsumption())
                        .monthlyTotalCafeConsumption(categoryConsumption.getCafeConsumption())
                        .monthlyTotalSportsConsumption(categoryConsumption.getSportsConsumption())
                        .monthlyTotalFoodConsumption(categoryConsumption.getFoodConsumption())
                        .monthlyTotalAccommodationConsumption(categoryConsumption.getAccommodationConsumption())
                        .monthlyTotalRetailConsumption(categoryConsumption.getRetailConsumption())
                        .monthlyTotalShoppingConsumption(categoryConsumption.getShoppingConsumption())
                        .monthlyTotalTransferConsumption(categoryConsumption.getTransferConsumption())
                        .monthlyTotalTransportationConsumption(categoryConsumption.getTransportationConsumption())
                        .monthlyTotalMedicalConsumption(categoryConsumption.getMedicalConsumption())
                        .monthlyTotalInsuranceConsumption(categoryConsumption.getInsuranceConsumption())
                        .monthlyTotalSubConsumption(categoryConsumption.getSubConsumption())
                        .monthlyTotalEduConsumption(categoryConsumption.getEduConsumption())
                        .monthlyTotalMobileConsumption(categoryConsumption.getMobileConsumption())
                        .build();

                // MonthlyAggregation 저장
                monthlyAggregationRepository.save(monthlyAggregation);

                // 각 사용자의 nowTotalConsumption을 0으로 초기화
                // => 달마다 바뀌어서 저장되어야 하기 때문
                user.setNowTotalConsumption(0L);
                userRepository.save(user);

                // 각 카테고리의 소비량을 0으로 초기화
                zeroCategoryConsumption(categoryConsumption);
            }
        } catch (Exception e){
            log.error("[UserService] saveLastMonthConsumption error : ",e);
        }
    }

    // 전달 받은 CategoryConsumption에 대해 각 카테고리의 소비량을 0으로 초기화
    @Transactional
    public void zeroCategoryConsumption(CategoryConsumption categoryConsumption){
        categoryConsumption.setEntertainmentConsumption(0L);
        categoryConsumption.setCultureConsumption(0L);
        categoryConsumption.setCafeConsumption(0L);
        categoryConsumption.setSportsConsumption(0L);
        categoryConsumption.setFoodConsumption(0L);
        categoryConsumption.setAccommodationConsumption(0L);
        categoryConsumption.setRetailConsumption(0L);
        categoryConsumption.setShoppingConsumption(0L);
        categoryConsumption.setTransferConsumption(0L);
        categoryConsumption.setTransportationConsumption(0L);
        categoryConsumption.setMedicalConsumption(0L);
        categoryConsumption.setInsuranceConsumption(0L);
        categoryConsumption.setSubConsumption(0L);
        categoryConsumption.setEduConsumption(0L);
        categoryConsumption.setMobileConsumption(0L);
        categoryConsumption.setOthersConsumption(0L);

        categoryConsumptionRepository.save(categoryConsumption); // 모든 변경 사항을 저장
    }
}
