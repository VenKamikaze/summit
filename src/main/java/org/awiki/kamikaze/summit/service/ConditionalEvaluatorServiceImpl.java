package org.awiki.kamikaze.summit.service;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.domain.codetable.CodeConditionalType;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SQLQuerySourceProcessorServiceImpl;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.TabularQuerySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class ConditionalEvaluatorServiceImpl implements ConditionalEvaluatorService
{
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private BindVarService bindVarService;
  
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
        return null != conditionResult && null != conditionResult.getResultValue();
      }
      
      default:
        throw new NotImplementedException("Conditional type: " + conditionalType + " is not yet supported.");
    }
  }
  
}
