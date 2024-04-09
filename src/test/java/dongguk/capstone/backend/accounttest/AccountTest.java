package dongguk.capstone.backend.accounttest;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import dongguk.capstone.backend.accountdto.AccountLogsRequestDTO;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

public class AccountTest {
    private final BarobillApiService barobillApiService;
    private AccountLogsRequestDTO accountLogsRequestDTO;

    public AccountTest() throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.TESTBED);
    }

    @Test
    public void logs(){

    }
}
