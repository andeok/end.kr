package kr.end.backend.auth.service;

import kr.end.backend.auth.dto.response.CustomUserDetails;
import kr.end.backend.global.exception.JJUSTException;
import kr.end.backend.global.exception.ErrorCode;
import kr.end.backend.member.domain.Member;
import kr.end.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Member member = memberRepository.findByEmail(username)
        .orElseThrow(
            () -> new JJUSTException(ErrorCode.NOT_FOUND_MEMBER)
        );
    return new CustomUserDetails(member);
  }
}
