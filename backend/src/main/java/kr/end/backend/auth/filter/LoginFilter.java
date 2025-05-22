package kr.end.backend.auth.filter;

import static kr.end.backend.global.exception.ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kr.end.backend.auth.dto.request.LoginRequest;
import kr.end.backend.auth.dto.response.CustomUserDetails;
import kr.end.backend.auth.dto.response.LoginResponse;
import kr.end.backend.auth.controller.cookie.CookieProvider;
import kr.end.backend.auth.service.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String LOGIN_REQUEST_URL = "/v1/login";
  private static final String LOGIN_REQUEST_HTTP_METHOD = "POST";
  private static final String LOGIN_REQUEST_CONTENT_TYPE = "application/json";
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(LOGIN_REQUEST_URL, LOGIN_REQUEST_HTTP_METHOD);

  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=utf-8";

  private final JwtProvider jwtProvider;
  private final CookieProvider cookieProvider;


  private final Validator validator;

  public LoginFilter(JwtProvider jwtProvider, CookieProvider cookieProvider, Validator validator) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    this.jwtProvider = jwtProvider;
    this.cookieProvider = cookieProvider;
    this.validator = validator;
  }


  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException, IOException {

    if (!isApplicationJson(request.getContentType())) {
      throw new AuthenticationServiceException(
          "Not Supported Content-Type: " + request.getContentType());
    }

    LoginRequest loginDto = parseDto(request);
    log.info("loginDto: {}", loginDto);
    return getAuthentication(loginDto);
  }

  private LoginRequest parseDto(HttpServletRequest request) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    LoginRequest loginDto = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginDto);
    if (!violations.isEmpty()) {
      Map<String, String> errorMap = violations
          .stream()
          .collect(Collectors.toMap(k -> k.getPropertyPath().toString(),
              ConstraintViolation::getMessage));
      throw new AuthenticationServiceException(objectMapper.writeValueAsString(errorMap));
    }
    return loginDto;
  }


  private Authentication getAuthentication(LoginRequest loginDto) {

    UsernamePasswordAuthenticationToken authentication =
        UsernamePasswordAuthenticationToken.unauthenticated(loginDto.email(), loginDto.password());
    return this.getAuthenticationManager().authenticate(authentication);
  }


  private boolean isApplicationJson(String contentType) {
    return contentType != null && contentType.equals(LOGIN_REQUEST_CONTENT_TYPE);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    CustomUserDetails principal = ((CustomUserDetails) authResult.getPrincipal());

    String accessToken = jwtProvider.createAccessToken(principal);
    String refreshToken = jwtProvider.createRefreshToken(principal);

    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(HttpServletResponse.SC_OK);

    ResponseCookie accessTokenCookie = cookieProvider.createAccessTokenCookie(accessToken);
    response.addHeader("Set-Cookie", accessTokenCookie.toString());

    ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(refreshToken);
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {

    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    new ObjectMapper().writeValue(
        response.getWriter(),
        new LoginResponse(MISMATCHED_EMAIL_OR_PASSWORD.getMessage()));
  }

}
