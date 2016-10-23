package org.awiki.kamikaze.summit.service.formatter;

import org.awiki.kamikaze.summit.dto.entry.TemplateDto;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface SourceProcessorTableFormatterService extends FormatterService
{
  public String format(final TemplateDto templateDto, final SourceProcessorResultTable sourceTable);
}
