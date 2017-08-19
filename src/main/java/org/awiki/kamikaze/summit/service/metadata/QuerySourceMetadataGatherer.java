package org.awiki.kamikaze.summit.service.metadata;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;

public interface QuerySourceMetadataGatherer
{
  public SourceMetadata getSourceMetadata(long sourceId, final List<BindVar> bindVars);
  
  public void clearSourceMetadata(long sourceId); // for caching
  
  
  
  ////// can probably remove the following methods when we switch over to sourceMetadata completely
  // For caching
  public Collection<String> updateColumnList(long sourceId, Collection<String> columns);
  
  // For caching
  public void clearColumnList(long sourceId);
}
