package dongguk.capstone.backend.account.service;

import dongguk.capstone.backend.account.dto.request.AccountReqRegistDTO;

public interface AccountService {
    int accountRegist(Long userId, AccountReqRegistDTO accountReqRegistDTO);
}
