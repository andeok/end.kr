package kr.end.backend.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import kr.end.backend.auth.service.AuthMember;
import kr.end.backend.auth.service.TokenType;
import kr.end.backend.global.exception.ErrorCode;
import kr.end.backend.global.exception.JJUSTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtResolver {

  private final JwtProperties jwtProperties;

  public AuthMember resolveAccessToken(String token) {
    return resolveTokenByType(token, TokenType.ACCESS_TOKEN);
  }

  public AuthMember resolveRefreshToken(String token) {
    return resolveTokenByType(token, TokenType.REFRESH_TOKEN);
  }

  private AuthMember resolveTokenByType(String token, TokenType tokenType) {
    try {
      Claims claims = Jwts.parser()
          .setSigningKey(jwtProperties.getSecretKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      validateTokenType(claims, tokenType);

      Long id = Long.valueOf(claims.getSubject());
      return AuthMember.from(id);
    } catch (ExpiredJwtException exception) {
      throw new JJUSTException(ErrorCode.EXPIRED_JWT_TOKEN);
    } catch (JwtException exception) {
      throw new JJUSTException(ErrorCode.INVALID_JWT_TOKEN);
    }
  }

  private void validateTokenType(Claims claims, TokenType tokenType) {
    String extractTokenType = claims.get(jwtProperties.TOKEN_TYPE, String.class);
    if (!extractTokenType.equals(tokenType.name())) {
      throw new JJUSTException(ErrorCode.AUTHENTICATION_TOKEN_TYPE_MISMATCH);
    }
  }

}
