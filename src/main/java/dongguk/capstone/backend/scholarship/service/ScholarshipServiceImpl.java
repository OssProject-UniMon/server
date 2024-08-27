package dongguk.capstone.backend.scholarship.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dongguk.capstone.backend.scholarship.dto.response.ScholarshipResRecommendDTO;
import dongguk.capstone.backend.scholarship.entity.Scholarship;
import dongguk.capstone.backend.scholarship.dto.*;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.scholarship.repository.ScholarshipRepository;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScholarshipServiceImpl implements ScholarshipService{
    private final UserRepository userRepository;
    private final ScholarshipRepository scholarshipRepository;
    private static final String SCRAPE_SCHOLASHIP_FLASK_SERVER_URL = "http://43.202.249.208:5000/scrape_scholarships";
    private static final String SCHOLARSHIP_FLASK_SERVER_URL = "http://43.202.249.208:5000/scholarship";


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void scrape() {
        List<Scholarship> scholarships = fetchScholarshipsFromUrl();
        scholarshipRepository.deleteAll();
        scholarshipRepository.saveAll(scholarships);
    }

    private List<Scholarship> fetchScholarshipsFromUrl() {
        List<Scholarship> scholarships = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(SCRAPE_SCHOLASHIP_FLASK_SERVER_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            scholarships = parseResponse(response.body());
        } catch (Exception e) {
            log.error("An error occurred while fetching scholarships from {}: {}", ScholarshipServiceImpl.SCRAPE_SCHOLASHIP_FLASK_SERVER_URL, e.getMessage());
        }

        return scholarships;
    }

    private List<Scholarship> parseResponse(String response) {
        List<Scholarship> scholarships = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    scholarships.add(createScholarshipFromNode(node));
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while parsing scholarships: {}", e.getMessage());
        }
        return scholarships;
    }

    private Scholarship createScholarshipFromNode(JsonNode node) {
        return Scholarship.builder()
                .name(node.get("name").asText())
                .amount(node.get("amount").asText())
                .target(node.get("target").asText())
                .due(node.get("due").asText())
                .url(node.get("url").asText())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipResRecommendDTO recommend(Long userId) throws IOException, InterruptedException {
        ScholarshipResRecommendDTO scholarshipResRecommendDTO = new ScholarshipResRecommendDTO();
        Optional<User> user = userRepository.findById(userId);
        List<Scholarship> scholarship = scholarshipRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = currentDate.format(formatter);
        if (user.isPresent()) {
            ScholarshipUserDetailDTO scholarshipUserDetailDTO = getScholarshipUserDetailDTO(user);
            if (scholarshipUserDetailDTO == null) {
                log.error("Failed to retrieve user details.");
                return scholarshipResRecommendDTO;
            }

            List<ScholarshipDetailListDTO> list = getScholarshipDetailListDTOS(scholarship);

            log.info("BeforeRecommendList: {}",list);
            ScholarshipDetailDTO scholarshipDetailDTO = new ScholarshipDetailDTO();
            scholarshipDetailDTO.setScholarshipDetailList(list);

            HttpClient client = HttpClient.newHttpClient();
            String requestBody = objectMapper.writeValueAsString(Map.of("scholarship_details", scholarshipDetailDTO,
                    "user_details", scholarshipUserDetailDTO,
                    "today", today));
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SCHOLARSHIP_FLASK_SERVER_URL))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(requestBodyBytes))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                log.info("responseJson: {}",responseJson);
                // responseJson.path("result").asText(); 이게 나중에 번호가 합쳐진 문자열로 올것임. "1 2 3 4 5" 이런 식으로!
                // list 여기서 인덱스해서 이용해야 됨
                String result = responseJson.path("result").asText();
                log.info("result: {}",result);
                // 여기서 result를 문자로 하나씩 나눈 뒤, 그 문자를 인덱스로 삼아서 기존 장학금 배열에서 인덱싱 하기!
                String[] index = result.split(" ");
                log.info("index[]: {}", (Object[]) index);
                List<Scholarship> scholarshipList = scholarshipRepository.findAll();
                List<ScholarshipResListDTO> resultList = getScholarshipResponseListDTOS(index, scholarshipList);
                scholarshipResRecommendDTO.setScholarshipList(resultList);
                return scholarshipResRecommendDTO;
            } else {
                log.error("Failed to classify transaction: {}", response.body());
                throw new RuntimeException("Failed to classify transaction: " + response.body());
            }
        }
        return scholarshipResRecommendDTO;
    }

    private static List<ScholarshipResListDTO> getScholarshipResponseListDTOS(String[] index, List<Scholarship> scholarshipList) {
        List<ScholarshipResListDTO> resultList = new ArrayList<>();
        for (String i : index) {
            Scholarship scholarshipResponse = scholarshipList.get(Integer.parseInt(i));
            ScholarshipResListDTO scholarshipResListDTO = new ScholarshipResListDTO();
            scholarshipResListDTO.setName(scholarshipResponse.getName());
            scholarshipResListDTO.setAmount(scholarshipResponse.getAmount());
            scholarshipResListDTO.setTarget(scholarshipResponse.getTarget());
            scholarshipResListDTO.setDue(scholarshipResponse.getDue());
            scholarshipResListDTO.setUrl(scholarshipResponse.getUrl());
            resultList.add(scholarshipResListDTO);
        }
        return resultList;
    }

    private static ScholarshipUserDetailDTO getScholarshipUserDetailDTO(Optional<User> user) {
        if (user.isPresent()) {
            User userInfo = user.get();
            return new ScholarshipUserDetailDTO(userInfo.getMajor(), userInfo.getGrade(), userInfo.getGender(),
                    userInfo.getIncomeBracket(), userInfo.getScholarshipStatus(), userInfo.getDistrict());
        }
        return null;
    }

    private static List<ScholarshipDetailListDTO> getScholarshipDetailListDTOS(List<Scholarship> scholarships) {
        List<ScholarshipDetailListDTO> list = new ArrayList<>();
        for (Scholarship scholarship : scholarships) {
            list.add(new ScholarshipDetailListDTO(scholarship.getName(), scholarship.getTarget(), scholarship.getDue()));
        }
        return list;
    }

}
