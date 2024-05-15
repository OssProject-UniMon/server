package dongguk.capstone.backend.barobilltest;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.BankAccount;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 바로빌 계좌조회 API
 */
public class BarobillAccountTests {

    /**
     * 바로빌 API 정의 클래스
     * <p>
     * 환경에 따라 BarobillApiProfile 를 지정해주세요.
     * </p>
     */
    private final BarobillApiService barobillApiService;

    public BarobillAccountTests() throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
    }

    /*
     * 서비스 신청 및 해지
     */

    /**
     * RegistBankAccount - 계좌 등록
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#RegistBankAccount
     * </p>
     */
    @Test
    public void registBankAccount() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String collectCycle = "MINUTE10";
        String bank = "SHINHAN";
        String bankAccountType = "P";
        String bankAccountNum = "110500411959";
        String bankAccountPwd = "4625";
        String webId = "JKM2731";
        String webPwd = "driermine4625.";
        String identityNum = "";
        String alias = "";
        String usage = "";

        int result = barobillApiService.bankAccount.registBankAccount(certKey, corpNum, collectCycle, bank, bankAccountType, bankAccountNum, bankAccountPwd, webId, webPwd, identityNum, alias, usage);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * UpdateBankAccount - 계좌 수정
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#UpdateBankAccount
     * </p>
     */
    @Test
    public void updateBankAccount() {
        String certKey = "";
        String corpNum = "";
        String bankAccountNum = "";
        String bankAccountPwd = "";
        String webId = "";
        String webPwd = "";
        String identityNum = "";
        String alias = "";
        String usage = "";

        int result = barobillApiService.bankAccount.updateBankAccount(certKey, corpNum, bankAccountNum, bankAccountPwd, webId, webPwd, identityNum, alias, usage);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * StopBankAccount - 계좌 해지
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#StopBankAccount
     * </p>
     */
    @Test
    public void stopBankAccount() {
        String certKey = "";
        String corpNum = "";
        String bankAccountNum = "";

        int result = barobillApiService.bankAccount.stopBankAccount(certKey, corpNum, bankAccountNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * CancelStopBankAccount - 계좌 해지취소
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#CancelStopBankAccount
     * </p>
     */
    @Test
    public void cancelStopBankAccount() {
        String certKey = "";
        String corpNum = "";
        String bankAccountNum = "";

        int result = barobillApiService.bankAccount.cancelStopBankAccount(certKey, corpNum, bankAccountNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * ReRegistBankAccount - 계좌 재신청 (해지 월이 지난 후)
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#ReRegistBankAccount
     * </p>
     */
    @Test
    public void reRegistBankAccount() {
        String certKey = "";
        String corpNum = "";
        String bankAccountNum = "";

        int result = barobillApiService.bankAccount.reRegistBankAccount(certKey, corpNum, bankAccountNum);

        if (result < 0) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /**
     * GetBankAccountManagementURL - 계좌 관리 URL
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetBankAccountManagementURL
     * </p>
     */
    @Test
    public void getBankAccountManagementURL() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String id = "capstone11";
        String pwd = "zoqtmxhselwkdls11!";

        String result = barobillApiService.bankAccount.getBankAccountManagementURL(certKey, corpNum, id, pwd);

        if (Pattern.compile("^-[0-9]{5}").matcher(result).matches()) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }

    /*
     * 계좌 목록 조회
     */

    /**
     * getBankAccountEx - 등록된 계좌번호 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetBankAccount
     * </p>
     */
    @Test
    public void getBankAccountEx() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        int availOnly = 1;

        List<BankAccount> result = barobillApiService.bankAccount.getBankAccountEx(certKey, corpNum, availOnly).getBankAccount();

        if (result.size() == 1 && Pattern.compile("^-[0-9]{5}$").matcher(result.get(0).getBankAccountNum()).matches()) {
            System.out.println(result.get(0).getBankAccountNum());
        } else {
            for (BankAccount bankAccount : result) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(bankAccount.getBankAccountNum());
            }
        }
    }

    /*
     * 입출금내역 조회
     */

    /**
     * getPeriodBankAccountLogEx - 계좌 입출금내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetDailyBankAccountLogEx
     * </p>
     */
    @Test
    public void getPeriodBankAccountLogEx() {
        String certKey = "3C2AF900-24FC-4DAF-8169-58E8B7F4AD03";
        String corpNum = "2018204468";
        String id = "capstone11";
        String bankAccountNum = "110500411959";
        String startDate = "20240426";
        String endDate = "20240430";
        int countPerPage = 20;
        int currentPage = 1;
        int orderDirection = 2;

        PagedBankAccountLogEx result = barobillApiService.bankAccount.getPeriodBankAccountLogEx(certKey, corpNum, id, bankAccountNum, startDate, endDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공

            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());
            System.out.println();

            for (BankAccountLogEx bankAccountLogEx : result.getBankAccountLogList().getBankAccountLogEx()) {
                // 여기에 get 받을 요소들 추가해야될 듯
                System.out.println(bankAccountLogEx.getDeposit());
                System.out.println(bankAccountLogEx.getTransType());
                System.out.println(bankAccountLogEx.getTransOffice());
                System.out.println(bankAccountLogEx.getTransRemark());
                System.out.println();
            }
        }
    }

    /**
     * GetDailyBankAccountLogEx - 일별 계좌 입출금내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetDailyBankAccountLogEx
     * </p>
     */
    @Test
    public void getDailyBankAccountLogEx() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String bankAccountNum = "";
        String baseDate = "";
        int countPerPage = 10;
        int currentPage = 1;
        int orderDirection = 2;

        PagedBankAccountLogEx result = barobillApiService.bankAccount.getDailyBankAccountLogEx(certKey, corpNum, id, bankAccountNum, baseDate, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (BankAccountLogEx bankAccountLogEx : result.getBankAccountLogList().getBankAccountLogEx()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(bankAccountLogEx.getBankAccountNum());
            }
        }

    }

    /**
     * GetMonthlyBankAccountLogEx - 월별 계좌 입출금내역 조회
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetMonthlyBankAccountLogEx
     * </p>
     */
    @Test
    public void getMonthlyBankAccountLogEx() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String bankAccountNum = "";
        String baseMonth = "";
        int countPerPage = 10;
        int currentPage = 1;
        int orderDirection = 2;

        PagedBankAccountLogEx result = barobillApiService.bankAccount.getMonthlyBankAccountLogEx(certKey, corpNum, id, bankAccountNum, baseMonth, countPerPage, currentPage, orderDirection);

        if (result.getCurrentPage() < 0) { // 호출 실패
            System.out.println(result.getCurrentPage());
        } else { // 호출 성공
            System.out.println(result.getCurrentPage());
            System.out.println(result.getCountPerPage());
            System.out.println(result.getMaxPageNum());
            System.out.println(result.getMaxIndex());

            for (BankAccountLogEx bankAccountLogEx : result.getBankAccountLogList().getBankAccountLogEx()) {
                // 필드정보는 레퍼런스를 참고해주세요.
                System.out.println(bankAccountLogEx.getBankAccountNum());
            }
        }

    }

    /**
     * GetBankAccountLogURL - 계좌 입출금내역 URL
     * <p>
     * API 레퍼런스 : https://dev.barobill.co.kr/docs/references/계좌조회-API#GetBankAccountLogURL
     * </p>
     */
    @Test
    public void getBankAccountLogURL() {
        String certKey = "";
        String corpNum = "";
        String id = "";
        String pwd = "";

        String result = barobillApiService.bankAccount.getBankAccountLogURL(certKey, corpNum, id, pwd);

        if (Pattern.compile("^-[0-9]{5}").matcher(result).matches()) { // 호출 실패
            System.out.println(result);
        } else { // 호출 성공
            System.out.println(result);
        }
    }
}