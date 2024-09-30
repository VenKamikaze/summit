package org.awiki.kamikaze.summit.util.mapper;

import org.apache.commons.collections4.Transformer;
import org.awiki.kamikaze.summit.dto.render.DropDownOptionItemDto;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SourceProcessorResultTableRowToDropDownOptionItemMapper implements
        Transformer<SourceProcessorResultTable.Row, DropDownOptionItemDto>
{
  private static final Logger logger = LoggerFactory.getLogger(SourceProcessorResultTableRowToDropDownOptionItemMapper.class);
  
  public DropDownOptionItemDto map(SourceProcessorResultTable.Row row)
  {
    if (row != null)
    {
      return new DropDownOptionItemDto(row.getCell(0).getValue(), row.getCell(1).getValue());
    }
    return null;
  }

  @Override
  public DropDownOptionItemDto transform(Row input)
  {
    return map(input);
  }
}
