package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.Predicate;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.springframework.util.MultiValueMap;

public interface FieldService {

  public static final String FIELD_SUBMITTED_FORM_NAME = "__SUMMIT_FORM_ID__";
  public static final String FORM_ID_PREFIX = "form-";
  
  public static final long TEMPLATE_BUTTON_ID_LOW  = -89;
  public static final long TEMPLATE_BUTTON_ID_HIGH = -80;
  
  /**
   * Predicate for use with CollectionUtils4 
   * Selects all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  public static final Predicate<RegionFieldDto> BUTTONS_PREDICATE = new Predicate<RegionFieldDto>() {
    @Override
    public boolean evaluate(RegionFieldDto rf)
    {
      return rf != null && rf.getField() != null && rf.getField().getTemplate() != null &&
              (rf.getField().getTemplate().getId() <= TEMPLATE_BUTTON_ID_HIGH &&
               rf.getField().getTemplate().getId() >= TEMPLATE_BUTTON_ID_LOW);
    }
  };
  
  /**
   * Process collection of regionFields, plus any parameters passed in through the HttpServletRequest into a
   *   collection of fieldDtos, inserting any values and processing source as required.
   * @param regionFieldDtos
   * @param parameterMap
   * @return Collection of FieldDtos
   */
  public Set<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap);
  
  public Map<String, PageItem<String>> fieldsToMap(Collection<PageItem<String>> fields);

  public FieldDto findFieldWithName(final RegionDto regionDto, final String name);
  
  public Collection<PageItem<String>> getAllFields(final PageDto pageDto);
}
