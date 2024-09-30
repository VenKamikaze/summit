package org.awiki.kamikaze.summit.config;

import org.awiki.kamikaze.summit.util.component.VariableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(SummitGlobalsConfig.class)
@Slf4j
public class SummitBaseConfiguration 
{
  private SummitGlobalsConfig globalVariables;

  public SummitGlobalsConfig getGlobalVariables() {
    return globalVariables;
  }

  @Autowired
  public void setGlobalVariables(SummitGlobalsConfig globalVariables) {
    this.globalVariables = globalVariables;
  }
  
  @Bean
  @Qualifier("summit-base")
  public VariableManager variableManager()
  {
    globalVariables.getGlobals().entrySet().stream().forEach(k -> log.info("summit.globals.{}:",k)); 
    return new VariableManager(globalVariables.getGlobals());
  }
  
}
