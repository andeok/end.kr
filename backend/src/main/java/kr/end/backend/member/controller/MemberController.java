package kr.end.backend.member.controller;

import kr.end.backend.auth.config.AuthRequiredPrincipal;
import kr.end.backend.auth.dto.response.MemberResponse;
import kr.end.backend.member.domain.Member;
import kr.end.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/me")
  public ResponseEntity<MemberResponse> readUserInfo(@AuthRequiredPrincipal Member member) {
    return ResponseEntity.ok(MemberResponse.from(member));
  }

}
