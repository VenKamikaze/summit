package org.awiki.kamikaze.summit.service.formatter;

import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_DATA_AND_SUBREGION_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_NAME_VARIABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.DropDownOptionItemDto;
import org.awiki.kamikaze.summit.dto.render.LabelDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.service.ConditionalEvaluatorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.component.VariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class GenericFormatterServiceImpl implements GenericFormatterService
{
  private static final Logger logger = LoggerFactory.getLogger(GenericFormatterService.class);
  
  private VariableManager summitGlobalsVariableManager;
  private ProxyFormatterService formatters;
  private ConditionalEvaluatorService conditionalService;
  
  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(6);
  static {  RESPONSIBILITIES.add(PageDto.class.getCanonicalName());
            RESPONSIBILITIES.add(RegionDto.class.getCanonicalName());
            RESPONSIBILITIES.add(LabelDto.class.getCanonicalName());
            RESPONSIBILITIES.add(DropDownOptionItemDto.class.getCanonicalName());
            RESPONSIBILITIES.add(SourceProcessorResultTable.class.getCanonicalName());
            RESPONSIBILITIES.add(SourceProcessorResultTable.HeaderRow.class.getCanonicalName()); 
            RESPONSIBILITIES.add(SourceProcessorResultTable.Row.class.getCanonicalName()); 
            RESPONSIBILITIES.add(SourceProcessorResultTable.Cell.class.getCanonicalName()); };
  
  
  @Autowired
  @Qualifier("summit-base")
  public void setVariableManager(VariableManager variableManager) {
    this.summitGlobalsVariableManager = variableManager;
  }

  @Autowired
  public void setFormatters(@Lazy ProxyFormatterService formatters) {
    this.formatters = formatters;
  }

  @Autowired
  public void setConditionalService(ConditionalEvaluatorService conditionalService) {
    this.conditionalService = conditionalService;
  }

  @Override
  public List<String> getResponsibilities()
  {
    return RESPONSIBILITIES;
  }

  @Override
  public StringBuilder format(StringBuilder builder, PageItem<String> item, int insertAt, Map<String, String> replacementVariableCache, 
          final Collection<PageItem<String>> pageFields)
  {
    replacementVariableCache.putAll(item.getCustomReplacementVariables());
    
    // If condition says to not render this item, then return immediately
    if(item.getConditional() != null) {
      if(! conditionalService.evaluate(item.getConditional(), pageFields) ) {
        return builder;
      }
    }
    
    final TemplateDto template = item.getTemplateDto(); 
    // A template can be null, for example with Mustache templating you can handle Row/HeaderRow/Cell objects at a higher level template
    if(null != template) {
      logger.debug("Formatting item : " + item.getClass().getCanonicalName());
      String templateSource = replaceInternalVariables(template.getSource(), item, replacementVariableCache);
      templateSource = replaceApplicationVariables(templateSource, summitGlobalsVariableManager.getReplacementVars());
      String processedSource = item.getProcessedSource() == null ? StringUtils.EMPTY : replaceInternalVariables(item.getProcessedSource(), item, replacementVariableCache);
      processedSource = replaceApplicationVariables(processedSource, summitGlobalsVariableManager.getReplacementVars());
      int templateSourceReplaceLocation = templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString()) == -1 ? 0 : templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString());
      int nextInsertAt = templateSourceReplaceLocation + insertAt;
      builder.insert(insertAt, templateSource.replace(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString(), processedSource));
      logger.trace("Builder (" + item.getClass().getCanonicalName() + "): " + builder.toString());
  
      if(item.hasChildPageItems()) {
      	List<PageItem<String>> items = new ArrayList<>(new ArrayList<>(item.getChildPageItems()));
      	Collections.reverse(items);
        for(PageItem<String> innerItem : items ) {
          FormatterService<PageItem<?>> formatter = formatters.getFormatterService(innerItem.getClass().getCanonicalName());
          formatter.format(builder, innerItem, nextInsertAt, replacementVariableCache, pageFields);
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
