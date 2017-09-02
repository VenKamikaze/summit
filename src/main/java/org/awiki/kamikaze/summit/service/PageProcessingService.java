package org.awiki.kamikaze.summit.service;

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
   * Note: if the passed in source is not of a SELECT type (e.g. may be a 'modify' type) then the result will be an empty list
   * @param PageProcessingSourceDto
   * @param parameterMap
   * @return Map of fieldName(String) to PageProcessingSourceSelectDto including populated values
   */
  public Map<String, PageProcessingSourceSelectDto> processSource(final PageProcessingSourceDto processSourceDto, final Map<String, String> parameterMap);
}
