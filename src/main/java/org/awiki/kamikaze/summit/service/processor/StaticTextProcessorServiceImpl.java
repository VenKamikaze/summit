package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.springframework.stereotype.Service;

@Service
public class StaticTextProcessorServiceImpl implements SingularSourceProcessorService
{
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(Arrays.asList(SingularSourceProcessorService.BUILT_IN_STATIC_TEXT_TYPE));
  }

  /**
   * There's nothing to execute for static text. Returns null.
   */
  @Override
  public SourceProcessorResult executeSource(String source, List<BindVar> bindVars)
  {
    return null;
  }

  /**
   * Just return the static text that was input.
   */
  @Override
  public SourceProcessorResult querySource(final String text, final List<BindVar> bindVars)
  {
    SourceProcessorResult result = new SourceProcessorResult();
    result.setResultValue(text);
    result.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
    result.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
    return result;
  }
}
