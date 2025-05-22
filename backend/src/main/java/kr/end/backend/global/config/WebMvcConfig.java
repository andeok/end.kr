package kr.end.backend.global.config;

import java.util.List;
import kr.end.backend.auth.config.AuthRequiredPrincipalArgumentResolver;
import kr.end.backend.auth.controller.cookie.CookieResolver;
import kr.end.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {

  private final AuthService authService;
  private final CookieResolver cookieResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

    resolvers.add(new AuthRequiredPrincipalArgumentResolver(cookieResolver, authService));
  }
}
