package kr.end.backend.member.repository;

import java.util.Optional;
import kr.end.backend.global.exception.ErrorCode;
import kr.end.backend.global.exception.JJUSTException;
import kr.end.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  default Member getMemberById(Long id) {
    return findById(id).orElseThrow(() -> new JJUSTException(ErrorCode.NOT_FOUND_MEMBER));
  }


  Optional<Member> findByEmail(String email);

  Optional<Member> findByNickname(String nickname);
}
