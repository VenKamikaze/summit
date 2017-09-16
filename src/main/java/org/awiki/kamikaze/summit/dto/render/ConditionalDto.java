package org.awiki.kamikaze.summit.dto.render;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;

public class ConditionalDto
{
  private long     id;
  private SourceDto   source = null;

  private SourceProcessorResult processedSource;
  
  public enum CONDITIONAL {
    TRUE("true");
    
    private String value = "";
    
    private CONDITIONAL(String value) {
      this.value = value;
    }
    
    public String toString() {
      return value;
    }
  }
  
  public static ConditionalDto ALWAYS_TRUE = new ConditionalDto();
  static {
    ALWAYS_TRUE.setProcessedSource(new SourceProcessorResult());
    ALWAYS_TRUE.getProcessedSource().setResultValue(CONDITIONAL.TRUE.toString());
    ALWAYS_TRUE.getProcessedSource().setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
    ALWAYS_TRUE.getProcessedSource().setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
  }
  
  public ConditionalDto()
  {
  }

  public ConditionalDto(long id, SourceDto source)
  {
    this.id = id;
    this.source = source;
  }

  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public SourceDto getSource()
  {
    return this.source;
  }

  public void setSource(SourceDto source)
  {
    this.source = source;
  }

  public SourceProcessorResult getProcessedSource()
  {
    return processedSource;
  }

  public void setProcessedSource(SourceProcessorResult postProcessedSource)
  {
    this.processedSource = postProcessedSource;
  }
}
