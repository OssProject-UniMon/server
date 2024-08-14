package dongguk.capstone.backend.monthlyaggregation.service;

public interface MonthlyAggregationService {
    // 1. 한 달 마다 마지막 날까지의 누적 총 소비량을 MonthlyAggregation으로 보내고, 0원으로 초기화 하는 로직
    // 2. 한 달마다 모든 카테고리의 소비량을 MonthlyAggregation으로 보내고, 0원으로 초기화 하는 로직
    void saveLastMonthConsumption();
}
