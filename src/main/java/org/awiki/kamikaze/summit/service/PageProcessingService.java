package org.awiki.kamikaze.summit.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Predicate;
import org.awiki.kamikaze.summit.domain.codetable.CodeProcessingType;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;

public interface PageProcessingService {
  
  /**
   * Predicate for use with CollectionUtils4 
   * Selects all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  public static final Predicate<PageProcessingDto> PAGE_RENDER_PRE_REGION_PREDICATE = new Predicate<PageProcessingDto>() {
    @Override
    public boolean evaluate(PageProcessingDto processingDto)
    {
      return processingDto != null && CodeProcessingType.CODE_PROCESS_BEFORE_REGIONS_1.equals(processingDto.getCodeProcessingType());
    }
  };
  
  /**
   * Process PageProcessingSourceDto, plus any parameters passed in through the HttpServletRequest into a
   *   collection of fieldDtos, inserting any values and processing source as required.
   * @param PageProcessingSourceDto
   * @param parameterMap
   * @return Collection of PageProcessingSourceSelectDto (also attached to the processSourceDto)
   */
  public List<PageProcessingSourceSelectDto> processSource(final PageProcessingSourceDto processSourceDto, final Map<String, String> parameterMap);
}
