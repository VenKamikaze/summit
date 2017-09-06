package org.awiki.kamikaze.summit.service.metadata;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.dto.render.SourceMetadataDto;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;

public interface QuerySourceMetadataGatherer
{
  public SourceMetadataDto updateSourceMetadata(SourceDto sourceDto, final List<BindVar> bindVars);

//  public SourceMetadataDto getSourceMetadata(SourceDto sourceDto, final List<BindVar> bindVars);

  public void clearSourceMetadata();
  
  ////// can probably remove the following methods when we switch over to sourceMetadata completely
  // For caching
  public Collection<String> updateColumnList(long sourceId, Collection<String> columns);
  
  // For caching
  public void clearColumnList(long sourceId);
}
