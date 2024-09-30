package org.awiki.kamikaze.summit.service;

import java.util.Collection;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.domain.codetable.CodeConditionalType;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
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
public class ConditionalEvaluatorServiceImpl implements ConditionalEvaluatorService
{
  private static final Logger log = LoggerFactory.getLogger(ConditionalEvaluatorServiceImpl.class);
  
  private ProxySourceProcessorService sourceProcessors;
  private BindVarService bindVarService;
  
  @Autowired
  public void setSourceProcessors(ProxySourceProcessorService sourceProcessors) {
    this.sourceProcessors = sourceProcessors;
  }

  @Autowired
  public void setBindVarService(BindVarService bindVarService) {
    this.bindVarService = bindVarService;
  }


  /* FIXME TODO: still relying on varchar only bind variables, allow other types */
  @Override
  public boolean evaluate(final ConditionalDto condition, final MultiValueMap<String, String> parameterMap) {
    
    final SourceProcessorResult sResult;
    
    if(SingularSourceProcessorService.BUILT_IN_SQL_DML_SELECT_CELL_TYPE.equals(condition.getSourceTypeCode())) {
      final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(condition.getSourceTypeCode());
      sResult = processor.processSource(condition.getSource().getSource(), condition.getSourceTypeCode(), 
              bindVarService.createVarcharBindVarsFromParameterMap(condition.getSource().getSource(), parameterMap));
    }
    else if(SQLQuerySourceProcessorServiceImpl.BUILT_IN_SQL_DML_SELECT_ROW_TYPE.equals(condition.getSourceTypeCode())) {
      final TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(condition.getSourceTypeCode());
      final SourceProcessorResultTable qResult = processor.executeQuery(condition.getSource().getSource(), 
              bindVarService.createVarcharBindVarsFromParameterMap(condition.getSource().getSource(), parameterMap));
      if(qResult.getCount() > 0) {
        sResult = new SourceProcessorResult(0L, SourceProcessorResult.STANDARD_SUCCESS_CODE, SourceProcessorResult.STANDARD_SUCCESS_MESSAGE, CodeConditionalType.CODE_CONDITIONAL_TEXT_TRUE_VALUE);
        }
      else {
        sResult = null;
      }
    }
    else {
      throw new NotImplementedException("Unknown source type for conditional: " + condition.getSourceTypeCode());
    }

    return evaluateConditionResult(sResult, condition.getCodeConditionalType());
  }

  private boolean evaluateConditionResult(final SourceProcessorResult conditionResult, final String conditionalType) {
    switch(conditionalType)
    {
      case CodeConditionalType.CODE_CONDITIONAL_TEXT_TRUE:
      {
        return CodeConditionalType.CODE_CONDITIONAL_TEXT_TRUE_VALUE.equals(conditionResult.getResultValue());
      }
      
      case CodeConditionalType.CODE_CONDITIONAL_EXISTS:
      {
        return null != conditionResult && SourceProcessorResult.STANDARD_SUCCESS_CODE == conditionResult.getReturnCode();
      }
      
      case CodeConditionalType.CODE_CONDITIONAL_NOT_EXISTS:
      {
        return null == conditionResult || SourceProcessorResult.STANDARD_NO_RESULT_CODE == conditionResult.getReturnCode();
      }
      
      default:
        throw new NotImplementedException("Conditional type: " + conditionalType + " is not yet supported.");
    }
  }

  @Override
  public boolean evaluate(ConditionalDto condition, Collection<PageItem<String>> processedFields)
  {
    final SourceProcessorResult sResult;
    
    if(SingularSourceProcessorService.BUILT_IN_SQL_DML_SELECT_CELL_TYPE.equals(condition.getSourceTypeCode())) {
      final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(condition.getSourceTypeCode());
      sResult = processor.processSource(condition.getSource().getSource(), condition.getSourceTypeCode(), 
              bindVarService.convertFieldsToBindVars(processedFields));
    }
    else if(SQLQuerySourceProcessorServiceImpl.BUILT_IN_SQL_DML_SELECT_ROW_TYPE.equals(condition.getSourceTypeCode())) {
      final TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(condition.getSourceTypeCode());
      final SourceProcessorResultTable qResult = processor.executeQuery(condition.getSource().getSource(), 
              bindVarService.convertFieldsToBindVars(processedFields));
      if(qResult.getCount() > 0) {
        sResult = new SourceProcessorResult(0L, SourceProcessorResult.STANDARD_SUCCESS_CODE, SourceProcessorResult.STANDARD_SUCCESS_MESSAGE, CodeConditionalType.CODE_CONDITIONAL_TEXT_TRUE_VALUE);
        }
      else {
        sResult = new SourceProcessorResult(0L, SourceProcessorResult.STANDARD_NO_RESULT_CODE, SourceProcessorResult.STANDARD_NO_RESULT_MESSAGE, null);
      }
    }
    else {
      throw new NotImplementedException("Unknown source type for conditional: " + condition.getSourceTypeCode());
    }

    boolean result = evaluateConditionResult(sResult, condition.getCodeConditionalType());
    log.debug("Conditional eval result was: " + result + " for source: " + condition.getSource().getSource());
    return result;
  }
  
}
