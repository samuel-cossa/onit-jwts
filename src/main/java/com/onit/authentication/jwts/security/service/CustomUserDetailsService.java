package com.onit.authentication.jwts.security.service;

import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    final UserRepository userRepository;
    /**
     * @param username email
     * @return UserDetails
     * @throws UsernameNotFoundException when the user isnt found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      return new CustomUserPrincipal(
         userRepository.findByEmail(username)
            .orElseThrow(
               ()->   new UsernameNotFoundException("User not found with email = "+username))
      );
    }
}
