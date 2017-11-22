package org.awiki.kamikaze.summit.service.field.processor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.springframework.util.MultiValueMap;

public interface FieldProcessorService {

  /**
   * Process collection of regionFields, plus any parameters passed in through the HttpServletRequest into a
   *   collection of fieldDtos, inserting any values and processing source as required.
   * @param regionFieldDtos
   * @param parameterMap
   * @return Collection of FieldDtos
   */
  public Set<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap);
  
  /**
   * Process a fields, plus any parameters passed in through the HttpServletRequest into a
   *   fieldDto, inserting any values and processing source as required.
   * @param fieldDtos
   * @param parameterMap
   * @return FieldDtos
   */
  public FieldDto processFieldForRender(final FieldDto fieldDto, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap);
  
  public List<String> getResponsibilities();
}
