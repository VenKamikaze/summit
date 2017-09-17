package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.springframework.util.MultiValueMap;

public interface ConditionalEvaluatorService {
  public boolean evaluate(final ConditionalDto condition, final MultiValueMap<String, String> parameterMap);
}
