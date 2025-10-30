package com.onit.authentication.jwts.security.jwt;

import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * JwtAuthenticationFilter
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  final JwtTokenProvider tokenProvider;
  final UserRepository userRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      // Ignore filtering public Endpoints
      final var path = request.getRequestURI();
      if (isPublicPath(path)) {
        filterChain.doFilter(request, response);
        return;
      }

      final var jwt = extractJwtFromCookie(request);

      if (!StringUtils.hasText(jwt)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token not present");
        return;
      }

      if (!tokenProvider.validateToken(jwt)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token inválido ou expirado");
        return;
      }

      String email = tokenProvider.getEmailFromToken(jwt);
      var user = userRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found with email = " + email));

      var principal = new CustomUserPrincipal(user);
      var authentication = new UsernamePasswordAuthenticationToken(
          principal, null, principal.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception ex) {
      log.error("JWT Authentication Error: {}", ex.getMessage());
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Authentication Error");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null)
      return null;

    return Arrays.stream(request.getCookies())
        .filter(cookie -> "accessToken".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }

  private boolean isPublicPath(String path) {
    return path.startsWith("/auth") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.startsWith("/swagger-resources") ||
        path.equals("/swagger-ui.html") ||
        path.startsWith("/webjars") ||
        path.startsWith("/actuator") ||
        path.startsWith("/posts") ||
        path.equals("/error");
  }
}
