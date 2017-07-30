package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;

public interface BindVarService
{
  /**
   * @param fields that have already been processed and have PostProcessed values.
   * @return
   */
  public List<BindVar> convertFieldsToBindVars(Collection<PageItem<String>> fields);
}
