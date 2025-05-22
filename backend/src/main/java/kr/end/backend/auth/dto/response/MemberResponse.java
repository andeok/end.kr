package kr.end.backend.auth.dto.response;

import kr.end.backend.member.domain.Member;

public record MemberResponse(

        Long id,
        String email,
        String nickname
) {
    public static MemberResponse from(Member user) {
        return new MemberResponse(
            user.getId(), user.getEmail(), user.getNickname()
        );
    }

}
