package org.awiki.kamikaze.summit.service;

import java.util.Collection;

import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.springframework.util.MultiValueMap;

public interface ConditionalEvaluatorService {
  public boolean evaluate(final ConditionalDto condition, final MultiValueMap<String, String> parameterMap);
  public boolean evaluate(final ConditionalDto condition, final Collection<PageItem<String>> processedFields);
}
