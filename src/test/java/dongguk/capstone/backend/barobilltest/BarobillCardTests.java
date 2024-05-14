package dongguk.capstone.backend.barobilltest;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.*;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 바로빌 카드조회 API
 */
public class BarobillCardTests {

    /**
     * 바로빌 API 정의 클래스
     * <p>
     * 환경에 따라 BarobillApiProfile 를 지정해주세요.
     * </p>
     */
    private final BarobillApiService barobillApiService;

    public BarobillCardTests() throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
    }

    /*
     * 서비스 신청 및 해지
     */

    /**
     * RegistCard - 카드 등록
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#RegistCard
     * </p>
     */

    public void registCard() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String cardCompany = "SHINHAN";
        String cardType = "P";
        String cardNum = "5107376798062092";
        String webId = "woalsdl7399";
        String webPwd = "driermine7399!";
        String alias = "";
        String usage = "";

        int result = barobillApiService.card.registCard(certKey, corpNum, cardCompany, cardType, cardNum, webId, webPwd, alias, usage);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공 => result == 1
            System.out.println(result);
        }
    }

    /**
     * UpdateCard - 카드 수정
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#UpdateCard
     * </p>
     */
    @Test
    public void updateCard() {
        String certKey = "";
        String corpNum = "";
        String cardNum = "";
        String webId = "";
        String webPwd = "";
        String alias = "";
        String usage = "";

        int result = barobillApiService.card.updateCard(certKey, corpNum, cardNum, webId, webPwd, alias, usage);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * StopCard - 카드 해지
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#StopCard
     * </p>
     */
    @Test
    public void stopCard() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String cardNum = "4890230019175114";

        int result = barobillApiService.card.stopCard(certKey, corpNum, cardNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * CancelStopCard - 카드 해지취소
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#CancelStopCard
     * </p>
     */
    @Test
    public void cancelStopCard() {
        String certKey = "";
        String corpNum = "";
        String cardNum = "";

        int result = barobillApiService.card.cancelStopCard(certKey, corpNum, cardNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * ReRegistCard - 카드 재신청 (해지 월이 지난 후)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#ReRegistCard
     * </p>
     */
    @Test
    public void reRegistCard() {
        String certKey = "";
        String corpNum = "";
        String cardNum = "";

        int result = barobillApiService.card.reRegistCard(certKey, corpNum, cardNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * RegistCardLogMemo - 카드 내역 메모등록
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#RegistCardLogMemo
     * </p>
     */
    @Test
    public void registCardLogMemo() {
        String certKey = "";
        String corpNum = "";
        String cardNum = "";
        String useKey = "";
        String memo = "";

        int result = barobillApiService.card.registCardLogMemo(certKey, corpNum, cardNum, useKey, memo);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * GetCardManagementURL - 카드 관리 URL
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetCardManagementURL
     * </p>
     */
    @Test
    public void getCardManagementURL() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String pwd = "";

        String result = barobillApiService.card.getCardManagementURL(certKey, corpNum, id, pwd);

        if (Pattern.compile("^-[0-9]{5}").matcher(result).matches()) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /*
     * 카드 목록 조회
     */

    /**
     * GetCard - 등록된 카드번호 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetCard
     * </p>
     */
    @Test
    public void getCardEx() {
        String certKey = "";
        String corpNum = "";
        int availOnly = 1;

        List<Card> result = barobillApiService.card.getCardEx(certKey, corpNum, availOnly).getCard();

        if (result.size() == 1 && Pattern.compile("^-[0-9]{5}$").matcher(result.get(0).getCardNum()).matches()) {
            System.out.println(result.get(0).getCardNum());
        } else {
            for (Card card : result) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(card.getCardNum());
            }
        }
    }

    /*
     * 사용내역 조회
     */

    /**
     * GetPeriodCardLogEx - 카드 사용내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetPeriodCardLogEx
     * </p>
     */
    @Test
    public void getPeriodCardLogEx() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String id = "capstone11";
        String cardNum = "5107376798062092";
        String startDate = "20240405";
        String endDate = "20240408";
        int countPerPage = 10;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx result = barobillApiService.card.getPeriodCardLogEx(certKey, corpNum, id, cardNum, startDate, endDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
//            System.out.println(result.getCurrentPage());
//            System.out.println(result.getCountPerPage());
//            System.out.println(result.getMaxPageNum());
//            System.out.println(result.getMaxIndex());

            for (CardLogEx cardLogEx : result.getCardLogList().getCardLogEx()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx.getCardNum());
                System.out.println(cardLogEx.getUseStoreName());
                System.out.println(cardLogEx.getUseStoreCorpNum());
                System.out.println(cardLogEx.getUseStoreBizType());
            }
        }
    }

    /**
     * GetPeriodCardLogEx2 - 카드 사용내역 조회 (일시불/할부 여부, 할부개월수, 통화코드 추가)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetPeriodCardLogEx2
     * </p>
     */
    @Test
    public void getPeriodCardLogEx2() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String startDate = "";
        String endDate = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx2 result = barobillApiService.card.getPeriodCardLogEx2(certKey, corpNum, id, cardNum, startDate, endDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx2 cardLogEx2 : result.getCardLogList().getCardLogEx2()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx2.getCardNum());
            }
        }
    }

    /**
     * GetPeriodCardLogEx3 - 카드 사용내역 조회 (일시불/할부 여부, 할부개월수, 통화코드, 메모 추가)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetPeriodCardLogEx3
     * </p>
     */
    @Test
    public void getPeriodCardLogEx3() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String id = "capstone11";
        String cardNum = "5107376798062092";
        String startDate = "20240501";
        String endDate = "20240504";
        int countPerPage = 100;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx3 result = barobillApiService.card.getPeriodCardLogEx3(certKey, corpNum, id, cardNum, startDate, endDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx3 cardLogEx3 : result.getCardLogList().getCardLogEx3()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx3.getUseStoreName());
                System.out.println(cardLogEx3.getUseStoreCorpNum());
                System.out.println(cardLogEx3.getUseStoreBizType());
            }
        }
    }

    /**
     * GetDailyCardLogEx - 일별 카드 사용내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetDailyCardLogEx
     * </p>
     */
    @Test
    public void getDailyCardLogEx() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseDate = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx result = barobillApiService.card.getDailyCardLogEx(certKey, corpNum, id, cardNum, baseDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx cardLogEx : result.getCardLogList().getCardLogEx()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx.getCardNum());
            }
        }
    }

    /**
     * GetDailyCardLogEx2 - 일별 카드 사용내역 조회 (일시불/할부 여부, 할부개월수, 통화코드 추가)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetDailyCardLogEx2
     * </p>
     */
    @Test
    public void getDailyCardLogEx2() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseDate = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx2 result = barobillApiService.card.getDailyCardLogEx2(certKey, corpNum, id, cardNum, baseDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx2 cardLogEx2 : result.getCardLogList().getCardLogEx2()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx2.getCardNum());
            }
        }
    }

    /**
     * GetDailyCardLogEx3 - 일별 카드 사용내역 조회 (일시불/할부 여부, 할부개월수, 통화코드, 메모 추가)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetDailyCardLogEx3
     * </p>
     */
    @Test
    public void getDailyCardLogEx3() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseDate = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx3 result = barobillApiService.card.getDailyCardLogEx3(certKey, corpNum, id, cardNum, baseDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx3 cardLogEx3 : result.getCardLogList().getCardLogEx3()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx3.getCardNum());
            }
        }
    }

    /**
     * GetMonthlyCardLogEx - 월별 카드 사용내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetMonthlyCardLogEx
     * </p>
     */
    @Test
    public void getMonthlyCardLogEx() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseMonth = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx result = barobillApiService.card.getMonthlyCardLogEx(certKey, corpNum, id, cardNum, baseMonth, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx cardLogEx : result.getCardLogList().getCardLogEx()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx.getCardNum());
            }
        }
    }

    /**
     * GetMonthlyCardLogEx2 - 월별 카드 사용내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetMonthlyCardLogEx2
     * </p>
     */
    @Test
    public void getMonthlyCardLogEx2() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseMonth = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx2 result = barobillApiService.card.getMonthlyCardLogEx2(certKey, corpNum, id, cardNum, baseMonth, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx2 cardLogEx2 : result.getCardLogList().getCardLogEx2()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx2.getCardNum());
            }
        }
    }

    /**
     * GetMonthlyCardLogEx3 - 월별 카드 사용내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetMonthlyCardLogEx3
     * </p>
     */
    @Test
    public void getMonthlyCardLogEx3() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String cardNum = "";
        String baseMonth = "";
        int countPerPage = 1;
        int currentPage = 1;
        int orderDirection = 2;

        PagedCardLogEx3 result = barobillApiService.card.getMonthlyCardLogEx3(certKey, corpNum, id, cardNum, baseMonth, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (CardLogEx3 cardLogEx3 : result.getCardLogList().getCardLogEx3()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(cardLogEx3.getCardNum());
            }
        }
    }

    /**
     * GetCardLogURL - 카드 사용내역 URL
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/카드조회-API#GetCardLogURL
     * </p>
     */
    @Test
    public void getCardLogURL() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String pwd = "";

        String result = barobillApiService.card.getCardLogURL(certKey, corpNum, id, pwd);

        if (Pattern.compile("^-[0-9]{5}").matcher(result).matches()) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

}

