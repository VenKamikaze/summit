package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.springframework.util.MultiValueMap;

public interface FieldService {
  
  /**
   * Process collection of regionFields, plus any parameters passed in through the HttpServletRequest into a
   *   collection of fieldDtos, inserting any values and processing source as required.
   * @param regionFieldDtos
   * @param parameterMap
   * @return Collection of FieldDtos
   */
  public Set<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap);
  
  public Map<String, PageItem<String>> fieldsToMap(Collection<PageItem<String>> fields);
}
