package com.onit.authentication.jwts.helper;

import com.onit.authentication.jwts.common.exception.ResourceNotFoundException;
import com.onit.authentication.jwts.modules.user.domain.User;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * AuthenticatedUserServiceHelper
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/21/25
 */

@Service
public class AuthenticatedUserServiceHelper {
  /**
   * Recupera usuário autenticado
   */
  private final CustomUserPrincipal getAuthenticatedPrincipal() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
      return principal;
    }
    throw new ResourceNotFoundException("User not authenticated");
  }

  public User getAuthenticatedUser() {
    return getAuthenticatedPrincipal().user();
  }
}
