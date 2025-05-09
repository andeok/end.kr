package kr.end.backend.member.service;

import java.util.List;
import kr.end.backend.auth.dto.request.SignupRequest;
import kr.end.backend.global.exception.EndException;
import kr.end.backend.global.exception.ErrorCode;
import kr.end.backend.member.domain.Member;
import kr.end.backend.member.dto.response.MemberResponse;
import kr.end.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream().map(data -> new MemberResponse(data)).toList();
    }

    @Transactional
    public MemberResponse signupMember(SignupRequest request) {

        duplicateEmail(request.email());
//        duplicateNickname(request.nickname());

        String encode = generatePassword(request.password());
        Member member = request.toEntity(request, encode);
        Member result = memberRepository.save(member);

        return new MemberResponse(result);
    }

    private void duplicateNickname(String nickname) {
        boolean exists = memberRepository.findByNickname(nickname).isPresent();
        if (exists) {
            throw new EndException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    // 이메일 중복 검사
    private void duplicateEmail(String email) {
        boolean exists = memberRepository.findByEmail(email).isPresent();
        if (exists) {
            throw new EndException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    // 비밀번호 암호화
    private String generatePassword(String password) {
        String rawPassword = password;
        return passwordEncoder.encode(rawPassword);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EndException(ErrorCode.NOT_FOUND_MEMBER));
    }

}
