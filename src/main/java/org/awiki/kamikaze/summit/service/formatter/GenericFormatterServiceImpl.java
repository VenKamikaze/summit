package org.awiki.kamikaze.summit.service.formatter;

import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_VARIABLE;

import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.domain.Template;
import org.awiki.kamikaze.summit.dto.entry.FieldDto;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageItem;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
import org.awiki.kamikaze.summit.repository.TemplateRepository;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericFormatterServiceImpl implements GenericFormatterService
{
  @Autowired
  private TemplateRepository repository;
  
  @Autowired
  private ProxyFormatterService formatters;
  
  @Override
  @SuppressWarnings("serial")
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(7) {{ add(PageDto.class.getCanonicalName());
                                       add(RegionDto.class.getCanonicalName());
                                       add(FieldDto.class.getCanonicalName()); 
                                       add(SourceProcessorResultTable.class.getCanonicalName());
                                       add(SourceProcessorResultTable.HeaderRow.class.getCanonicalName()); 
                                       add(SourceProcessorResultTable.Row.class.getCanonicalName()); 
                                       add(SourceProcessorResultTable.Cell.class.getCanonicalName()); 
                                    }};
  }

  @Override
  public StringBuilder format(StringBuilder builder, PageItem<String> item)
  {
    if(item.hasSubPageItems()) {
      for(PageItem<String> innerItem : item.getSubPageItems()) {
        @SuppressWarnings("unchecked")
        FormatterService<PageItem<String>> formatter = (FormatterService<PageItem<String>>) formatters.getFormatterService(innerItem.getClass().getCanonicalName());
        formatter.format(builder, innerItem);
      }
    }
    
    //FIXME: HACK: /* Change this to using template instance, once we have implemented it */
    Template template = repository.findByClassName(item.getClass().getCanonicalName());
    builder.append(template.getSource().replace(REPLACEMENT_VARIABLE.toString(), item.getProcessedSource() == null ? "" : item.getProcessedSource()));
    
    return builder;
  }

}
