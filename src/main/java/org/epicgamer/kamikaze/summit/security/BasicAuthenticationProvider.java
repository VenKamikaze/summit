package org.epicgamer.kamikaze.summit.security;

import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

public class BasicAuthenticationProvider implements AuthenticationProvider
{
  private static final Logger log = LoggerFactory.getLogger(BasicAuthenticationProvider.class);
  
  @PersistenceContext(type=PersistenceContextType.TRANSACTION)
  private EntityManager em;
  
  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    log.debug("authenticate called");
    
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    
    HashSet<GrantedAuthority> grantedAuthoritys=new HashSet<GrantedAuthority>();
    
    //Query query=em.createNativeQuery("select authenticate_user(:username,:password)");
    
    //query.setParameter("username", username);
    //query.setParameter("password", password);
    
    //Object message=query.getSingleResult();
    
    //if (message!=null)
    //{
//      throw new BadCredentialsException(message.toString());
    //}
    

    if (null == username || username.length() == 0)
    {
      throw new BadCredentialsException("Username not found.");
    }
    
    //grantedAuthoritys.add(new GrantedAuthorityImpl(role.getRole().getRole()));
      
    UsernamePasswordAuthenticationToken simpleUserAuthentication =
              new UsernamePasswordAuthenticationToken(/*user */ new Object(), authentication.getCredentials(), grantedAuthoritys);
    
    return simpleUserAuthentication;
  }

  @Override
  public boolean supports(Class<?> authentication)
  {
    // TODO Auto-generated method stub
    return true;
  }

}
