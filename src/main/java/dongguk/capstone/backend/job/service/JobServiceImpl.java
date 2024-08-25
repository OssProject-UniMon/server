package dongguk.capstone.backend.job.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dongguk.capstone.backend.job.entity.Job;
import dongguk.capstone.backend.job.dto.request.JobReqDTO;
import dongguk.capstone.backend.job.dto.response.JobResDTO;
import dongguk.capstone.backend.job.repository.JobRepository;
import dongguk.capstone.backend.log.entity.Log;
import dongguk.capstone.backend.log.repository.LogRepository;
import dongguk.capstone.backend.schedule.entity.Schedule;
import dongguk.capstone.backend.schedule.repository.ScheduleRepository;
import dongguk.capstone.backend.user.repository.UserRepository;
import dongguk.capstone.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService{
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;

    private static final String PARTTIME_SCRAPE_FLASK_SERVER_URL = "http://13.124.16.179:5000/scrape_partTime";
    private static final String PARTTIME_FLASK_SERVER_URL = "http://13.124.16.179:5000/part_time";

    private static final List<Integer> DAWN_LIST = Arrays.asList(1, 2, 3, 4, 5, 6);
    private static final List<Integer> MORNING_LIST = Arrays.asList(7, 8, 9, 10, 11, 12);
    private static final List<Integer> AFTERNOON_LIST = Arrays.asList(13, 14, 15, 16, 17, 18);
    private static final List<Integer> EVENING_LIST = Arrays.asList(19, 20, 21, 22, 23, 24);

    private static final Map<List<Integer>, String> TIME_LIST_NAMES = new HashMap<>();
    static {
        TIME_LIST_NAMES.put(DAWN_LIST, "DAWN_LIST");
        TIME_LIST_NAMES.put(MORNING_LIST, "MORNING_LIST");
        TIME_LIST_NAMES.put(AFTERNOON_LIST, "AFTERNOON_LIST");
        TIME_LIST_NAMES.put(EVENING_LIST, "EVENING_LIST");
    }

    private static final List<List<Integer>> DAY_SCHEDULE = Arrays.asList(DAWN_LIST, MORNING_LIST, AFTERNOON_LIST, EVENING_LIST);
    private static final List<List<List<Integer>>> WEEK_SCHEDULE = new ArrayList<>(Arrays.asList(
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE),
            new ArrayList<>(DAY_SCHEDULE)
    ));

    @Override
    @Transactional(readOnly = true)
    public JobResDTO recommend(Long userId, JobReqDTO jobReqDTO) {
        JobResDTO jobResDTO = new JobResDTO();
        Optional<Job> job = jobRepository.findJobByUserId(userId);
        String requestMessage = jobReqDTO.getRequestMessage();

        if (job.isPresent()) {
            Job jobDetails = job.get();
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> commonTimeWeekday = mapper.readValue(jobDetails.getCommonTimeWeekday(), new TypeReference<List<String>>() {});
                List<String> commonTimeWeekend = mapper.readValue(jobDetails.getCommonTimeWeekend(), new TypeReference<List<String>>() {});

                String responseMessage = getPartTimeFlaskServerUrl(requestMessage, jobDetails.getDistrict(),
                        Integer.parseInt(jobDetails.getWithdrawSum()),
                        commonTimeWeekday, commonTimeWeekend);
                jobResDTO.setResponseMessage(responseMessage);
            } catch (Exception e) {
                log.error("Error deserializing common times", e);
            }
        }
        return jobResDTO;
    }

    private void fetchJobsFromUrl() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(PARTTIME_SCRAPE_FLASK_SERVER_URL))
                    .GET()
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
            log.info("Successfully sent request for scholarships data.");
        } catch (Exception e) {
            log.error("An error occurred while fetching scholarships from the following URL: {}: {}", PARTTIME_SCRAPE_FLASK_SERVER_URL, e.getMessage());
        }
    }

    // 스케줄링된 작업을 직접 호출하는 메서드
    @Override
    public void fetchJobsAndRecommend() {
        fetchJobsFromUrl();
        recommendReady();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void recommendReady() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Log> logList = logRepository.findLogsByUserId(user.getUserId());
            List<Schedule> scheduleList = scheduleRepository.findSchedulesByUserId(user.getUserId());

            for (Schedule s : scheduleList) {
                int dayIndex = Integer.parseInt(s.getDay()) - 1;
                List<List<Integer>> daySchedule = WEEK_SCHEDULE.get(dayIndex);

                int startTime = Integer.parseInt(s.getStartTime());
                int endTime = Integer.parseInt(s.getEndTime());

                List<List<Integer>> listsToRemove = new ArrayList<>();
                for (List<Integer> timeList : daySchedule) {
                    if (timeList.contains(startTime) || timeList.contains(endTime)) {
                        listsToRemove.add(timeList);
                    }
                }
                daySchedule.removeAll(listsToRemove);
            }

            List<String> commonTimeWeekday = new ArrayList<>();
            List<String> commonTimeWeekend = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                List<List<Integer>> weekDaySchedule = WEEK_SCHEDULE.get(i);
                for (List<Integer> timeList : weekDaySchedule) {
                    if (isWeekDayCommonTime(timeList)) {
                        String timeListName = TIME_LIST_NAMES.get(timeList);
                        timeListName = switch (timeListName) {
                            case "DAWN_LIST" -> "새벽";
                            case "MORNING_LIST" -> "오전";
                            case "AFTERNOON_LIST" -> "오후";
                            case "EVENING_LIST" -> "저녁";
                            default -> "근무 불가능";
                        };
                        if (!commonTimeWeekday.contains(timeListName)) {
                            commonTimeWeekday.add(timeListName);
                        }
                    }
                }
            }

            for (int i = 5; i < WEEK_SCHEDULE.size(); i++) {
                List<List<Integer>> weekEndSchedule = WEEK_SCHEDULE.get(i);
                for (List<Integer> timeList : weekEndSchedule) {
                    if (isWeekEndCommonTime(timeList)) {
                        String timeListName = TIME_LIST_NAMES.get(timeList);
                        timeListName = switch (timeListName) {
                            case "DAWN_LIST" -> "새벽";
                            case "MORNING_LIST" -> "오전";
                            case "AFTERNOON_LIST" -> "오후";
                            case "EVENING_LIST" -> "저녁";
                            default -> "근무 불가능";
                        };
                        if (!commonTimeWeekend.contains(timeListName)) {
                            commonTimeWeekend.add(timeListName);
                        }
                    }
                }
            }

            log.info("Common Time on Weekday: {}", commonTimeWeekday);
            log.info("Common Time on Weekend: {}", commonTimeWeekend);

            String userDistrict = user.getDistrict();
            int withdrawSum = 0;
            for (Log l : logList) {
                withdrawSum += Integer.parseInt(l.getWithdraw());
            }
            String withdrawSumResult = String.valueOf(withdrawSum);

            // Job ID 설정
            Long jobId = jobRepository.findJobIdByUserId(user.getUserId());
            if (jobId == null) {
                jobId = 1L; // 최대 logId가 없으면 1로 초기화
            } else {
                jobId++; // 최대 logId가 있으면 1 증가
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                String commonTimeWeekdayStr = mapper.writeValueAsString(commonTimeWeekday);
                String commonTimeWeekendStr = mapper.writeValueAsString(commonTimeWeekend);

                // 빌더를 사용하여 Job 객체 생성
                Job job = Job.builder()
                        .jobId(jobId)
                        .userId(user.getUserId())
                        .district(userDistrict)
                        .withdrawSum(withdrawSumResult)
                        .commonTimeWeekday(commonTimeWeekdayStr)
                        .commonTimeWeekend(commonTimeWeekendStr)
                        .build();

                // 기존 Job 객체 삭제
                Optional<Job> existingJob = jobRepository.findJobByUserId(user.getUserId());
                existingJob.ifPresent(jobRepository::delete);

                // 새로운 Job 객체 저장
                jobRepository.save(job);

            } catch (Exception e) {
                log.error("Error serializing common times", e);
            }
        }
    }


    private boolean isWeekDayCommonTime(List<Integer> timeList) {
        for (int i = 0; i < 5; i++) {
            List<List<Integer>> daySchedule = WEEK_SCHEDULE.get(i);
            if (!daySchedule.contains(timeList)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWeekEndCommonTime(List<Integer> timeList) {
        for (int i = 5; i < WEEK_SCHEDULE.size(); i++) {
            List<List<Integer>> daySchedule = WEEK_SCHEDULE.get(i);
            if (!daySchedule.contains(timeList)) {
                return false;
            }
        }
        return true;
    }

    public static String getPartTimeFlaskServerUrl(String requestMessage, String userDistrict, int withdrawSum, List<String> commonTimeWeekday,
                                                   List<String> commonTimeWeekend) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String partTimeDetails = "사용자가 거주하는 서울특별시 내의 구 : " + userDistrict + " 사용자의 가계부의 출금 데이터의 합 : " + withdrawSum +
                    " 사용자의 평일 아르바이트 근무 가능 시간대 : " + commonTimeWeekday + " 사용자의 주말 아르바이트 근무 가능 시간대 : " + commonTimeWeekend;
            log.info("partTimeDetails : {}", partTimeDetails);

            String requestMessageDetails = "사용자의 알바 추천 요청 메시지 : " + requestMessage;
            log.info("requestMessageDetails : {}", requestMessageDetails);

            // Replace 함수 대신 String.format을 사용하여 문자열을 포맷팅합니다.
            String json = mapper.writeValueAsString(Map.of("partTimeDetails", partTimeDetails, "requestMessageDetails", requestMessageDetails));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(PARTTIME_FLASK_SERVER_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = mapper.readTree(response.body());
                return responseJson.path("result").asText();
            } else {
                log.error("Failed to classify transaction: {}", response.body());
                throw new RuntimeException("Failed to classify transaction: " + response.body());
            }
        } catch (Exception e) {
            log.error("Exception occurred while classifying transaction: {}", e.getMessage());
            throw new RuntimeException("Exception occurred while classifying transaction", e);
        }
    }
}