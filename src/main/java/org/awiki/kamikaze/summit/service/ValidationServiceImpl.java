package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.domain.codetable.CodeValidationType;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.ValidationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class ValidationServiceImpl implements ValidationService
{
  private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);

  private ConditionalEvaluatorService conditionalService;

  @Autowired
  public void setConditionalService(ConditionalEvaluatorService conditionalService) {
    this.conditionalService = conditionalService;
  }

  @Override
  public List<String> validate(final Collection<ValidationDto> validations, final MultiValueMap<String, String> parameterMap)
  {
    final List<String> errors = new ArrayList<>();
    for(final ValidationDto validation : validations) {
      if(validation.getConditional() != null &&
              ! conditionalService.evaluate(validation.getConditional(), parameterMap)) {
        continue;
      }
      if(! passes(validation, parameterMap)) {
        log.info("Validation failed: " + validation.getName());
        errors.add(validation.getErrorMessage());
      }
    }
    return errors;
  }

  private boolean passes(final ValidationDto validation, final MultiValueMap<String, String> parameterMap)
  {
    if(CodeValidationType.CODE_VALIDATION_NOT_NULL.equals(validation.getCodeValidationType())) {
      final List<String> values = parameterMap.get(validation.getFieldName());
      return values != null && !values.isEmpty() && StringUtils.isNotBlank(values.get(0));
    }

    // The source-based validation types (TEXT_TRUE / EXISTS / NOTEXISTS) share
    // their semantics with conditionals, so evaluate through the conditional
    // evaluator: the validation passes when the condition holds.
    if(validation.getSource() == null) {
      throw new NotImplementedException("Validation '" + validation.getName() + "' of type "
              + validation.getCodeValidationType() + " requires a source.");
    }
    final ConditionalDto condition = new ConditionalDto();
    condition.setSource(validation.getSource());
    condition.setSourceTypeCode(validation.getSourceTypeCode());
    condition.setCodeConditionalType(validation.getCodeValidationType());
    return conditionalService.evaluate(condition, parameterMap);
  }
}
