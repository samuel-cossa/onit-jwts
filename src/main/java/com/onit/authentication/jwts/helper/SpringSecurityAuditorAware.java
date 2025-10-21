package com.onit.authentication.jwts.helper;

import java.util.Optional;

import com.onit.authentication.jwts.modules.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
/**
 * SpringSecurityAuditorAware
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/21/25
 */

@RequiredArgsConstructor
@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

  final AuthenticatedUserServiceHelper authenticatedUserServiceHelper;

  @Override
  @NonNull
  public Optional<User> getCurrentAuditor() {
    try {
      return Optional.ofNullable(authenticatedUserServiceHelper.getAuthenticatedUser());
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
