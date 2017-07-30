package org.awiki.kamikaze.summit.service.formatter;

import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_NAME_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_DATA_AND_SUBREGION_VARIABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
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
  public StringBuilder format(StringBuilder builder, PageItem<String> item, int insertAt, Map<String, String> replacementVariableCache)
  {
    replacementVariableCache.putAll(item.getReplacementVariables());
    
    final TemplateDto template = item.getTemplateDto(); 
    // A template can be null, for example with Mustache templating you can handle Row/HeaderRow/Cell objects at a higher level template
    if(null != template) {
      logger.debug("Formatting item : " + item.getClass().getCanonicalName());
      String templateSource = replaceInternalVariables(template.getSource(), item, replacementVariableCache);
      templateSource = replaceApplicationVariables(templateSource, variableManager.getReplacementVars());
      int templateSourceReplaceLocation = templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString()) == -1 ? 0 : templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString());
      int nextInsertAt = templateSourceReplaceLocation + insertAt;
      builder.insert(insertAt, templateSource.replace(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString(), item.getProcessedSource() == null ? "" : item.getProcessedSource()));
  
      if(item.hasSubPageItems()) {
        for(PageItem<String> innerItem : Lists.reverse(new ArrayList<>(item.getSubPageItems())) ) {
          FormatterService<PageItem<?>> formatter = formatters.getFormatterService(innerItem.getClass().getCanonicalName());
          formatter.format(builder, innerItem, nextInsertAt, replacementVariableCache);
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
   * @param replacementVariableCache - a running cache that is built up of unique replacement variables per PageItem processed.
   * @return recalculated length of insertAt variable after replacements have run.
   */
  private String replaceInternalVariables(String templateSource, final PageItem<String> item, final Map<String, String> replacementVariableCache) {
    for(Map.Entry<String, String> kvp : replacementVariableCache.entrySet()) {
      templateSource = StringUtils.replace(templateSource, kvp.getKey(), kvp.getValue());
    }
    templateSource = StringUtils.replace(templateSource, REPLACEMENT_NAME_VARIABLE.toString(), item.getName() );
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
