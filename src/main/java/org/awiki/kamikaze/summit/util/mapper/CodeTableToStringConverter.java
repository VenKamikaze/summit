package org.awiki.kamikaze.summit.util.mapper;

import org.awiki.kamikaze.summit.domain.codetable.CodeTable;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CodeTableToStringConverter implements Converter<CodeTable, String>
{
  @Autowired
  private Mapper mapper;

  private static final Logger logger = LoggerFactory.getLogger(CodeTableToStringConverter.class);
   
  @Override
  public String convert(CodeTable source)
  {
    logger.debug("Coverting code: " + source.getCode());
    return source.getCode();
  }
  
}
