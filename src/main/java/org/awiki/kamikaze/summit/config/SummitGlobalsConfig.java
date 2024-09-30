package org.awiki.kamikaze.summit.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties("summit")
@Data
@Getter
@Setter
@ToString
public class SummitGlobalsConfig 
{
  private Map<String, String> globals;
}
