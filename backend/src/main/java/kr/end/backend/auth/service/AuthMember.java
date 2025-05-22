package kr.end.backend.auth.service;

import jakarta.validation.constraints.NotNull;

public record AuthMember(@NotNull Long id) {

  public static AuthMember from(Long id) {
    return new AuthMember(id);
  }
}
