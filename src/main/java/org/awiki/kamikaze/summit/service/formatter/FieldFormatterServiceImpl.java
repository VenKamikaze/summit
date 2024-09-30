package org.awiki.kamikaze.summit.service.formatter;

import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_DATA_AND_SUBREGION_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_LABEL_LEFT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_LABEL_RIGHT_ID_VARIABLE;
import static org.awiki.kamikaze.summit.service.formatter.FormatEnums.REPLACEMENT_NAME_VARIABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.DropDownFieldDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.service.ConditionalEvaluatorService;
import org.awiki.kamikaze.summit.util.component.VariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class FieldFormatterServiceImpl implements FieldFormatterService
{
  private static final Logger logger = LoggerFactory.getLogger(GenericFormatterService.class);
  
  private VariableManager summitGlobalsVariableManager;
  private ProxyFormatterService formatters;
  private ConditionalEvaluatorService conditionalService;
  
  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(2);
  static {  
            RESPONSIBILITIES.add(FieldDto.class.getCanonicalName()); 
            RESPONSIBILITIES.add(DropDownFieldDto.class.getCanonicalName());
         };
  
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

  // We need to make sure we only replace the LABEL_LEFT or LABEL_RIGHT associated with the field we are operating within
  // So make sure we use the templateSource from the parent field to replace LABEL_LEFT/LABEL_RIGHT ?
  
  @Override
  public StringBuilder format(StringBuilder builder, PageItem<String> pgItem, int insertAt, Map<String, String> replacementVariableCache, 
          final Collection<PageItem<String>> pageFields)
  {
    final FieldDto fieldDto = (FieldDto) pgItem;
    
    replacementVariableCache.putAll(fieldDto.getCustomReplacementVariables());
    
    // If condition says to not render this item, then return immediately
    if(fieldDto.getConditional() != null) {
      if(! conditionalService.evaluate(fieldDto.getConditional(), pageFields) ) {
        return builder;
      }
    }
    
    final TemplateDto template = fieldDto.getTemplateDto(); 
    // A template can be null, for example with Mustache templating you can handle Row/HeaderRow/Cell objects at a higher level template
    if(null != template) {
      logger.debug("Formatting field : " + fieldDto.getClass().getCanonicalName());
      String templateSource = replaceInternalVariables(template.getSource(), fieldDto, replacementVariableCache);
      templateSource = replaceApplicationVariables(templateSource, summitGlobalsVariableManager.getReplacementVars());
      String processedSource = fieldDto.getProcessedSource() == null ? StringUtils.EMPTY : replaceInternalVariables(fieldDto.getProcessedSource(), fieldDto, replacementVariableCache);
      processedSource = replaceApplicationVariables(processedSource, summitGlobalsVariableManager.getReplacementVars());
      
      
      
      int labelReplaceLocation = -1;
      if(fieldDto.hasLabel()) {
        if(REPLACEMENT_LABEL_LEFT_ID_VARIABLE.equals(fieldDto.getLabel().getLabelPositionVariable())) {
          logger.debug("Formatting field - found left label.");
          labelReplaceLocation = templateSource.indexOf(REPLACEMENT_LABEL_LEFT_ID_VARIABLE.toString());
        }
        else if(FormatEnums.REPLACEMENT_LABEL_RIGHT_ID_VARIABLE.equals(fieldDto.getLabel().getLabelPositionVariable())) {
          logger.debug("Formatting field - found right label.");
          labelReplaceLocation = templateSource.indexOf(REPLACEMENT_LABEL_RIGHT_ID_VARIABLE.toString());
        }
      }
      templateSource = StringUtils.replace(templateSource, REPLACEMENT_LABEL_LEFT_ID_VARIABLE.toString(), StringUtils.EMPTY);
      templateSource = StringUtils.replace(templateSource, REPLACEMENT_LABEL_RIGHT_ID_VARIABLE.toString(), StringUtils.EMPTY);
      
      int templateSourceReplaceLocation = templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString()) == -1 ? 0 : templateSource.indexOf(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString());
      
      int nextInsertAt = templateSourceReplaceLocation + insertAt;
      builder.insert(insertAt, templateSource.replace(REPLACEMENT_DATA_AND_SUBREGION_VARIABLE.toString(), processedSource));

      if(labelReplaceLocation > -1) {
        int noLabelSize = builder.length();
        FormatterService<PageItem<?>> formatter = formatters.getFormatterService(fieldDto.getLabel().getClass().getCanonicalName());
        formatter.format(builder, fieldDto.getLabel(), insertAt + labelReplaceLocation, replacementVariableCache, pageFields);
        nextInsertAt = nextInsertAt + (builder.length() - noLabelSize); // avoid including label replacement
      }
      
      if(fieldDto.hasChildPageItems()) {
    	List<PageItem<String>> items = new ArrayList<>(fieldDto.getChildPageItems());
    	Collections.reverse(items);
        for(PageItem<String> innerItem : items ) {
          FormatterService<PageItem<?>> formatter = formatters.getFormatterService(innerItem.getClass().getCanonicalName());
          formatter.format(builder, innerItem, nextInsertAt, replacementVariableCache, pageFields);
        }
      }
    }
    else {
      logger.debug("Got null template for field: " + fieldDto.getClass().getCanonicalName());
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
