package kr.end.backend.trade.service;

import kr.end.backend.global.exception.JJUSTException;
import kr.end.backend.global.exception.ErrorCode;
import kr.end.backend.member.domain.Member;
import kr.end.backend.member.service.MemberService;
import kr.end.backend.trade.domain.Trade;
import kr.end.backend.trade.dto.request.TradeRequest;
import kr.end.backend.trade.dto.response.TradeResponse;
import kr.end.backend.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final MemberService memberService;

    public TradeResponse createTrade(TradeRequest request) {

        // TODO : memberId는 컨트롤러에서 받아올 예정임
        Member member = memberService.getMember(1L);
        Trade trade = null;

        if (request.parentId() != null) {
            trade = tradeRepository.findById(request.parentId())
                .orElseThrow(() -> new JJUSTException(ErrorCode.NOT_FOUND_PRODUCT));
        }

        Trade entity = request.toEntity(trade, member);

        Trade save = tradeRepository.save(entity);

        return TradeResponse.of(save);
    }
}
