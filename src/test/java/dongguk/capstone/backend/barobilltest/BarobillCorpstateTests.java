package dongguk.capstone.backend.barobilltest;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.ArrayOfString;
import com.baroservice.ws.CorpState;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

/**
 * 바로빌 사업자등록 상태조회 API
 */
public class BarobillCorpstateTests {

    /**
     * 바로빌 API 정의 클래스
     * <p>
     * 환경에 따라 BarobillApiProfile 를 지정해주세요.
     * </p>
     */
    private final BarobillApiService barobillApiService;

    public BarobillCorpstateTests() throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
    }

    /*
     * 사업자등록 상태조회
     */

    /**
     * GetCorpState - 단건 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/사업자등록-상태조회-API#GetCorpState
     * </p>
     */
    @Test
    public void getCorpState() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String checkCorpNum = "2018204468";

        CorpState result = barobillApiService.corpState.getCorpState(certKey, corpNum, checkCorpNum);

        if (result.getState() < 0) { // 호출 실패
            System.out.println(result.getState());
        } else { // 호출 성공
            // 필드정보는 레퍼런스를 참고해주세요.
            System.out.println(result.getStateDate());
//            System.out.println(result.getBaseDate());
        }
    }

    /**
     * GetCorpStates - 대량 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/사업자등록-상태조회-API#GetCorpStates
     * </p>
     */
    @Test
    public void getCorpStates() {
        String certKey = "";
        String corpNum = "";

        ArrayOfString checkCorpNumList = new ArrayOfString();
        checkCorpNumList.getString().add("");
        checkCorpNumList.getString().add("");

        List<CorpState> result = barobillApiService.corpState.getCorpStates(certKey, corpNum, checkCorpNumList).getCorpState();

        if (result.size() == 1 && result.get(0).getCorpNum().isEmpty() && result.get(0).getState() < 0) { // 호출 실패
            System.out.println(result.get(0).getState());
        } else { // 호출 성공
            for (CorpState corpState : result) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(corpState.getState());
            }
        }
    }

}

