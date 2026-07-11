package org.awiki.kamikaze.summit.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SQLQuerySourceProcessorServiceImpl;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.TabularQuerySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class PageProcessingServiceImpl implements PageProcessingService
{
  private static final Logger log = LoggerFactory.getLogger(PageProcessingServiceImpl.class);

  private ProxySourceProcessorService sourceProcessors;
  private BindVarService bindVarService;
  private ConditionalEvaluatorService conditionalService;
  
  @Autowired
  public void setSourceProcessors(ProxySourceProcessorService sourceProcessors) {
    this.sourceProcessors = sourceProcessors;
  }

  @Autowired
  public void setBindVarService(BindVarService bindVarService) {
    this.bindVarService = bindVarService;
  }

  @Autowired
  public void setConditionalService(ConditionalEvaluatorService conditionalService) {
    this.conditionalService = conditionalService;
  }

  /* FIXME TODO: still relying on varchar only bind variables, allow other types */
  @Override
  public Map<String, PageProcessingSourceSelectDto> processSource(PageProcessingSourceDto processSourceDto,
          MultiValueMap<String, String> parameterMap)
  {
    Map<String, PageProcessingSourceSelectDto> results = new HashMap<>(processSourceDto.getPageProcessingSourceSelect().size());
    
    // If this is a conditional piece of processing, only continue if we pass the conditional test.
    if(processSourceDto.getPageProcessing().getConditional() != null) {
      if (! conditionalService.evaluate(processSourceDto.getPageProcessing().getConditional(), parameterMap)) {
        return results;
      }
    }
    
    if(SingularSourceProcessorService.SINGULAR_SOURCE_TYPES.contains(processSourceDto.getCodeSourceType())) {
      final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(processSourceDto.getCodeSourceType());
      final SourceProcessorResult sResult = processor.processSource(processSourceDto.getSource(), processSourceDto.getCodeSourceType(), 
              bindVarService.createVarcharBindVarsFromParameterMap(processSourceDto.getSource(), parameterMap));
      PageProcessingSourceSelectDto res = processSourceDto.getPageProcessingSourceSelect().size() > 0 ? processSourceDto.getPageProcessingSourceSelect().get(0) : null;
      if (res != null) {
        res.setFieldValue(sResult);
        results.put(res.getFieldName(), res);
      }
    }
    else if(SQLQuerySourceProcessorServiceImpl.BUILT_IN_SQL_DML_SELECT_ROW_TYPE.equals(processSourceDto.getCodeSourceType())) {
      final TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(processSourceDto.getCodeSourceType());
      final SourceProcessorResultTable qResult = processor.executeQuery(processSourceDto.getSource(), 
              bindVarService.createVarcharBindVarsFromParameterMap(processSourceDto.getSource(), parameterMap));
      if(qResult.getCount() > 0) {
        for(PageProcessingSourceSelectDto res : processSourceDto.getPageProcessingSourceSelect()) {
          final SourceProcessorResult cell = new SourceProcessorResult();
          cell.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
          cell.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
          cell.setResultValue(qResult.getCellByXY(new Long(res.getFieldIndex()).intValue(), 1).getValue() ); // row 1 == first data row, since row 0 == header info
          res.setFieldValue(cell);
          results.put(res.getFieldName(), res);
        }
      }
    }
    else {
      throw new NotImplementedException("Unknown source type for pageProcessing: " + processSourceDto.getCodeSourceType());
    }

    return results;
  }

  @Override
  public String processBranchSource(PageProcessingSourceDto processSourceDto,
          MultiValueMap<String, String> parameterMap)
  {
    // Branches are gated by PAGE_PROCESSING_CONDITIONAL just like POST1 processings,
    // typically on :REQUEST matching the submitted button.
    if(processSourceDto.getPageProcessing().getConditional() != null) {
      if (! conditionalService.evaluate(processSourceDto.getPageProcessing().getConditional(), parameterMap)) {
        return null;
      }
    }

    final String target;
    if(SingularSourceProcessorService.BUILT_IN_STATIC_TEXT_TYPE.equals(processSourceDto.getCodeSourceType())) {
      target = substituteUrlParameters(processSourceDto.getSource(), parameterMap);
    }
    else if(SingularSourceProcessorService.BUILT_IN_SQL_DML_SELECT_CELL_TYPE.equals(processSourceDto.getCodeSourceType())) {
      final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(processSourceDto.getCodeSourceType());
      final SourceProcessorResult result = processor.processSource(processSourceDto.getSource(), processSourceDto.getCodeSourceType(),
              bindVarService.createVarcharBindVarsFromParameterMap(processSourceDto.getSource(), parameterMap));
      target = result != null ? result.getResultValue() : null;
    }
    else {
      throw new NotImplementedException("Unsupported source type for a branch processing: " + processSourceDto.getCodeSourceType());
    }

    return StringUtils.isBlank(target) ? null : target.trim();
  }

  /**
   * Substitute :name variables in a static branch URL template with the (URL-encoded) first
   * value of the matching parameter. Uses the same bind-variable pattern as SQL sources so
   * templates follow the documented bind scraping rules. A :name with no matching parameter
   * substitutes as an empty string (logged), keeping the URL well-formed.
   */
  private String substituteUrlParameters(final String template, final MultiValueMap<String, String> parameterMap) {
    final Matcher m = BindVarServiceImpl.bindParameters.matcher(template);
    final StringBuffer sb = new StringBuffer();
    while(m.find()) {
      final String name = m.group(1);
      final String value = parameterMap.containsKey(name) ? parameterMap.getFirst(name) : null;
      if(value == null) {
        log.warn("Branch URL template references :" + name + " but no such parameter was submitted; substituting empty. Template: " + template);
      }
      m.appendReplacement(sb, Matcher.quoteReplacement(URLEncoder.encode(StringUtils.defaultString(value), StandardCharsets.UTF_8)));
    }
    m.appendTail(sb);
    return sb.toString();
  }

}
