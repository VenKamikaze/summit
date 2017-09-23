package org.awiki.kamikaze.summit.util.mapper;

import java.math.BigDecimal;

import org.apache.commons.collections4.Transformer;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PageItemToBindVarMapper implements Transformer<PageItem<?>, BindVar>
{
  private static final Logger logger = LoggerFactory.getLogger(PageItemToBindVarMapper.class);
  private static final String NUMBER_TYPE = "NUMBER";
  
  // TODO FIXME missing a large amount of bind var Types
  public BindVar map(PageItem<?> pageItem)
  {
    if(pageItem.getProcessedSource() != null)
    {
      if(pageItem instanceof FieldDto)
      {
        FieldDto f = (FieldDto) pageItem;
        if (NUMBER_TYPE.equals(f.getCodeFieldType()))
        {
          return new BindVar(new BigDecimal(pageItem.getProcessedSource().toString()), java.sql.Types.NUMERIC, pageItem.getName());
        }
        else
        {
          return new BindVar(pageItem.getProcessedSource(), java.sql.Types.VARCHAR, pageItem.getName());
        }
      }
    }
    return new BindVar(null, java.sql.Types.NULL, pageItem.getName());
  }
  
  @Override
  public BindVar transform(PageItem<?> input)
  {
    return map(input);
  }
}
