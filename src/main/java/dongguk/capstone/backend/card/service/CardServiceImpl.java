package dongguk.capstone.backend.card.service;

import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import dongguk.capstone.backend.card.dto.request.CardReqRegistDTO;
import dongguk.capstone.backend.card.entity.Card;
import dongguk.capstone.backend.card.repository.CardRepository;
import dongguk.capstone.backend.card.entity.CardEmbedded;
import dongguk.capstone.backend.user.entity.User;
import dongguk.capstone.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;

@Service
@Slf4j
public class CardServiceImpl implements CardService{
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BarobillApiService barobillApiService;

    public CardServiceImpl(UserRepository userRepository, CardRepository cardRepository) throws MalformedURLException {
        barobillApiService = new BarobillApiService(BarobillApiProfile.RELEASE);
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }


    /**
     * 카드 등록 로직
     * @param userId
     * @param cardReqRegistDTO
     * @return
     */
    @Override
    @Transactional
    public int cardRegist(Long userId, CardReqRegistDTO cardReqRegistDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        CardEmbedded cardEmbedded = CardEmbedded.builder()
                .userId(userId)
                .cardNum(cardReqRegistDTO.getCardNum())
                .build();

        Card card = Card.builder()
                .user(user)
                .cardEmbedded(cardEmbedded)
                .cardCompany(cardReqRegistDTO.getCardCompany())
                .cardType(cardReqRegistDTO.getCardType())
                .webId(cardReqRegistDTO.getWebId())
                .webPwd(cardReqRegistDTO.getWebPwd())
                .build();

        cardRepository.save(card);

        return barobillApiService.card.registCard("181A0E21-E0B0-4AC8-9C8F-BBEAEA954C9D", "2018204468", card.getCardCompany(), card.getCardType(),
                cardEmbedded.getCardNum(), card.getWebId(), card.getWebPwd(), "", "");
    }
}
