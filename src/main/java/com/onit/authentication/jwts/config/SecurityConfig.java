package com.onit.authentication.jwts.config;

import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.security.jwt.JwtAuthenticationFilter;
import com.onit.authentication.jwts.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * SecurityConfig
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  final JwtTokenProvider jwtTokenProvider;
  final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(
     HttpSecurity http,
     CorsConfigurationSource corsConfigurationSource)throws Exception{

    var jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider,
       userRepository);

    http
       .csrf(AbstractHttpConfigurer::disable)
       .cors(cors -> cors.configurationSource(corsConfigurationSource))
       .sessionManagement(session -> session
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
       .exceptionHandling(ex -> ex
          .authenticationEntryPoint((request, response, authException) -> response
             .sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized, Must Work Hard!")))
       .authorizeHttpRequests(request->
             request
                .requestMatchers(WHITE_LIST_URL)
                .permitAll()
                .anyRequest()
                .authenticated()
                             )
       .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
     AuthenticationConfiguration authenticationConfiguration
                                                    ) throws Exception{
    return  authenticationConfiguration.getAuthenticationManager();
  }
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  private static final String[]  WHITE_LIST_URL = {
      "/auth/**",
      "/posts",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/swagger-resources/**",
      "/swagger-ui.html",
      "/webjars/**",
      "/actuator/**",
      "/error"
  };

}
