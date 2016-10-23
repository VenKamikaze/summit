package org.awiki.kamikaze.summit.service.formatter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.dto.entry.TemplateDto;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.springframework.stereotype.Service;

// TODO: FIXME: This must be data driven. Lets get the implementation going for now, then figure out how to 
//      drive it from the DB. 2016-10-23

// Hande a SourceProcessorResultTable, and style it with templating from the DB as needed
@Service
public class SourceProcessorTableFormatterServiceImpl implements SourceProcessorTableFormatterService
{
  
  
  @Override
  public String format(final TemplateDto templateDto, final SourceProcessorResultTable sourceTable) {
    int formattedLength = templateDto.getHeaderSource().length() + sourceTable.get
                        + templateDto.getBodySource().length() + formattable.getBody().length()
                        + templateDto.getFooterSource().length() + formattable.getFooter().length();
    int formattedLength = 1000; // TODO FIXME
    
    StringBuilder formatted = new StringBuilder(formattedLength);
    if(true) throw new NotImplementedException("FIXME: implement me.");
    formatHeader(templateDto.getHeaderSource(), formattable.getHeader());
    
    return formatted.toString();
  }
  
  private String formatHeader(final String headerTemplate, final String headerSource) {
    return "";
    //return headerTemplate.replace(target, replacement)
  }

  @SuppressWarnings("serial")
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(1) {{ add(SourceProcessorResultTable.class.getCanonicalName()); }};
  }
}
