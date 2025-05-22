package kr.end.backend.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import kr.end.backend.auth.controller.cookie.CookieResolver;
import kr.end.backend.auth.service.AuthService;
import kr.end.backend.global.exception.JJUSTException;
import kr.end.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthRequiredPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

  private final CookieResolver cookieResolver;
  private final AuthService authService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthRequiredPrincipal.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

    cookieResolver.checkLoginRequired(request);
    String token = cookieResolver.extractAccessToken(request);

    return authService.getAuthUser(token);
  }


}
