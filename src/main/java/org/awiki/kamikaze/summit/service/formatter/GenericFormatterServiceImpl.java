package org.awiki.kamikaze.summit.service.formatter;

import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_SUBREGION_VARIABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.entry.FieldDto;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageItem;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
import org.awiki.kamikaze.summit.dto.entry.TemplateDto;
import org.awiki.kamikaze.summit.repository.TemplateRepository;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.component.VariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class GenericFormatterServiceImpl implements GenericFormatterService
{
  private static final Logger logger = LoggerFactory.getLogger(GenericFormatterService.class);
  
  @Autowired
  private VariableManager variableManager;
  
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
  public StringBuilder format(StringBuilder builder, PageItem<String> item, int insertAt)
  { 
    final TemplateDto template = item.getTemplateDto(); 
    // A template can be null, for example with Mustache templating you can handle Row/HeaderRow/Cell objects at a higher level template
    if(null != template) {
      logger.debug("Formatting item : " + item.getClass().getCanonicalName());
      String templateSource = replaceInternalVariables(template.getSource(), item);
      templateSource = replaceApplicationVariables(templateSource, variableManager.getReplacementVars());
      int templateSourceReplaceLocation = templateSource.indexOf(REPLACEMENT_SUBREGION_VARIABLE.toString()) == -1 ? 0 : templateSource.indexOf(REPLACEMENT_SUBREGION_VARIABLE.toString());
      int nextInsertAt = templateSourceReplaceLocation + insertAt;
      builder.insert(insertAt, templateSource.replace(REPLACEMENT_SUBREGION_VARIABLE.toString(), item.getProcessedSource() == null ? "" : item.getProcessedSource()));
  
      if(item.hasSubPageItems()) {
        for(PageItem<String> innerItem : Lists.reverse(new ArrayList<>(item.getSubPageItems())) ) {
          FormatterService<PageItem<?>> formatter = formatters.getFormatterService(innerItem.getClass().getCanonicalName());
          formatter.format(builder, innerItem, nextInsertAt);
        }
      }
    }
    else {
      logger.debug("Got null template for item: " + item.getClass().getCanonicalName());
    }
    
    return builder;
  }
  
  /**
   * 
   * @param builder
   * @param item
   * @return recalculated length of insertAt variable after replacements have run.
   */
  private String replaceInternalVariables(String templateSource, PageItem<String> item) {
    return StringUtils.replace(templateSource, REPLACEMENT_ID_VARIABLE.toString(), item.getId().toString());
  }
  
  String replaceApplicationVariables(String templateSource, Map<String,String> replacementVariables) {
    String processedString = templateSource;
    for(Map.Entry<String,String> me : replacementVariables.entrySet()) {
      processedString = StringUtils.replace(processedString, me.getKey(), me.getValue());
    }
    return processedString;
  }

}
