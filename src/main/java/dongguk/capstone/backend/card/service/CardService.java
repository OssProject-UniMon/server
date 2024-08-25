package dongguk.capstone.backend.card.service;

import dongguk.capstone.backend.card.dto.request.CardReqRegistDTO;

public interface CardService {
    int cardRegist(Long userId, CardReqRegistDTO cardReqRegistDTO);
}
