package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.dto.render.ValidationDto;
import org.springframework.util.MultiValueMap;

public interface ValidationService
{
  /**
   * Run the page's validations against the submitted form parameters, in
   * validation_num order. Each validation is skipped if its conditional
   * (typically on :REQUEST) fails. Runs ALL applicable validations so the
   * user sees every problem at once, APEX-style.
   * @return the error messages of the failed validations, empty when all passed
   */
  public List<String> validate(final Collection<ValidationDto> validations, final MultiValueMap<String, String> parameterMap);
}
