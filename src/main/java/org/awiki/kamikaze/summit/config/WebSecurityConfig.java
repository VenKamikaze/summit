package org.awiki.kamikaze.summit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  @Bean
  public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
      UserDetails user = User.withUsername("admin")
          .password(passwordEncoder.encode("password"))
          .roles("ADMIN")
          .build();
      return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
      // Using CookieCsrfTokenRepository as an example
      return CookieCsrfTokenRepository.withHttpOnlyFalse();
  }

  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf((csrf) -> csrf
          .csrfTokenRepository(csrfTokenRepository())
          .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
       ).authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
          .requestMatchers("/**").permitAll());
    return http.build();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  
  /*
  @Bean
  public HttpSessionCsrfTokenRepository csrfTokenRepository() {
    return new HttpSessionCsrfTokenRepository();
  }
  */
}
