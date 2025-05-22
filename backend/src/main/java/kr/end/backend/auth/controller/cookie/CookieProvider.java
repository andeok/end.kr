package kr.end.backend.auth.controller.cookie;

import java.time.Duration;
import kr.end.backend.auth.service.jwt.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

  protected static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
  protected static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  private final JwtProperties jwtProperties;

  public CookieProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  public ResponseCookie createAccessTokenCookie(String token) {
    return createCookie(
        ACCESS_TOKEN_COOKIE_NAME,
        token,
        jwtProperties.getAccessTokenExpirationMillis());
  }

  public ResponseCookie createRefreshTokenCookie(String token) {
    return createCookie(
        REFRESH_TOKEN_COOKIE_NAME,
        token,
        jwtProperties.getRefreshTokenExpirationMillis());
  }

  private ResponseCookie createCookie(String tokenName, String token, long expiredMillis) {
    return ResponseCookie
        .from(tokenName, token)
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .maxAge(Duration.ofMillis(expiredMillis))
        .path("/")
        .build();
  }

  public ResponseCookie deleteAccessTokenCookie() {
    return deleteCookie(ACCESS_TOKEN_COOKIE_NAME);
  }

  public ResponseCookie deleteRefreshTokenCookie() {
    return deleteCookie(REFRESH_TOKEN_COOKIE_NAME);
  }

  private ResponseCookie deleteCookie(String tokenName) {
    return ResponseCookie
        .from(tokenName, "")
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .maxAge(0)
        .path("/")
        .build();
  }


}
