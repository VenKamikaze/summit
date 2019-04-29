package org.awiki.kamikaze.summit.dto.render;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;

public class ConditionalDto
{
  private long     id;
  private SourceDto   source = null;

  private String                codeSourceType;          // e.g. dml_selcel
  private String                codeConditionalType;     // e.g. TEXT_TRUE (returns 'true' as text for true result)
  
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
  
  public String getSourceTypeCode()
  {
    return codeSourceType;
  }

  public void setSourceTypeCode(String codeSourceType)
  {
    this.codeSourceType = codeSourceType;
  }

  public String getCodeConditionalType()
  {
    return codeConditionalType;
  }

  public void setCodeConditionalType(String codeConditionalType)
  {
    this.codeConditionalType = codeConditionalType;
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
