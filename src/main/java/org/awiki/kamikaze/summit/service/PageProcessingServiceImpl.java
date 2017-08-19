package org.awiki.kamikaze.summit.service;

import java.util.List;
import java.util.Map;

import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.springframework.beans.factory.annotation.Autowired;

public class PageProcessingServiceImpl implements PageProcessingService
{
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Override
  public List<PageProcessingSourceSelectDto> processSource(PageProcessingSourceDto processSourceDto,
          Map<String, String> parameterMap)
  {
    final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(processSourceDto.getCodeSourceType());
    // We need to cast the predicate columns to varchar if we have any parameterMap values being bound, so we don't get type conversion errors
    // 
    //processor.processSource(sourceDto.getSource(), sourceDto.getCodeSourceType(), )
    throw new RuntimeException("IMPL ME!");
    
  }

}
