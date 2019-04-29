package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface TabularQuerySourceProcessorService extends SourceProcessorService {

  SourceProcessorResultTable executeQuery(final String source, final List<BindVar> bindVars);
  
  public Long getTotalRecordCount(final String fullQuery, final List<BindVar> bindVars);
}
