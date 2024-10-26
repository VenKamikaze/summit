package org.awiki.kamikaze.summit.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CsrfTokenProvider {
  
  private CsrfTokenRepository csrfTokenRepository;

  @Autowired
  public void setCsrfTokenRepository(CsrfTokenRepository repo) {
    this.csrfTokenRepository = repo;
  }
  
  private CsrfToken getCsrfToken() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = attr.getRequest();
    HttpServletResponse response = attr.getResponse();

    // This will either get an existing token or generate a new one
    CsrfToken token = csrfTokenRepository.generateToken(request);
    
    // Save the token for this request/response pair
    csrfTokenRepository.saveToken(token, request, response);
    
    return token;
  }

  public String getCsrfTokenValue() {
    return getCsrfToken().getToken();
  }
  
  public String getCsrfTokenHtmlInput() {
      // Return the HTML input field
      return String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\"/>",
          getCsrfToken().getParameterName(),
          getCsrfToken().getToken());
  }
}
