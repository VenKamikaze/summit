package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Predicate;
import org.awiki.kamikaze.summit.domain.codetable.CodeProcessingType;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.springframework.util.MultiValueMap;

public interface PageProcessingService {
  
  @SuppressWarnings("serial")
  static final List<String> PRE_REGION_CODES = new ArrayList<String>(1) {{
    add(CodeProcessingType.CODE_PROCESS_BEFORE_REGIONS_1);
  }};
  
  @SuppressWarnings("serial")
  static final List<String> POST_PROCESS_CODES = new ArrayList<String>(1) {{
    add(CodeProcessingType.CODE_PROCESS_POST_1);
  }};
  
  @SuppressWarnings("serial")
  static final List<String> POST_PROCESS_BRANCHING_CODES = new ArrayList<String>(1) {{
    add(CodeProcessingType.CODE_PROCESS_POST_BRANCH_1);
  }};
  
  
  /**
   * Predicate for use with CollectionUtils4 
   * Selects all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  public static final Predicate<PageProcessingDto> PAGE_RENDER_PRE_REGION_PREDICATE = new Predicate<PageProcessingDto>() {
    @Override
    public boolean evaluate(PageProcessingDto processingDto)
    {
      return processingDto != null && PRE_REGION_CODES.contains(processingDto.getCodeProcessingType());
    }
  };
  
  /**
   * Predicate for use with CollectionUtils4 
   * Selects all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  public static final Predicate<PageProcessingDto> PAGE_POST_PROCESS_BRANCH_PREDICATE = new Predicate<PageProcessingDto>() {
    @Override
    public boolean evaluate(PageProcessingDto processingDto)
    {
      return processingDto != null && POST_PROCESS_BRANCHING_CODES.contains(processingDto.getCodeProcessingType());
    }
  };
  
  /**
   * Predicate for use with CollectionUtils4 
   * Selects all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  public static final Predicate<PageProcessingDto> PAGE_POST_PROCESS_PREDICATE = new Predicate<PageProcessingDto>() {
    @Override
    public boolean evaluate(PageProcessingDto processingDto)
    {
      return processingDto != null && POST_PROCESS_CODES.contains(processingDto.getCodeProcessingType());
    }
  };
  
  /**
   * Process PageProcessingSourceDto, plus any parameters passed in through the HttpServletRequest into a
   *   collection of fieldDtos, inserting any values and processing source as required.
   * Note: if the passed in source is not of a SELECT type (e.g. may be a 'modify' type) then the result will be an empty list
   * @param PageProcessingSourceDto
   * @param parameterMap
   * @param submitAction - the button that posted the form
   * @return Map of fieldName(String) to PageProcessingSourceSelectDto including populated values
   */
  public Map<String, PageProcessingSourceSelectDto> processSource(final PageProcessingSourceDto processSourceDto,
          final MultiValueMap<String, String> parameterMap);

  /**
   * Process a BRANCH1 PageProcessingSourceDto into a branch target URL (context-relative path).
   * Honours the processing's PAGE_PROCESSING_CONDITIONAL the same way processSource does.
   * Source types: 'static' treats the source as a URL template whose :name variables are
   * substituted (URL-encoded) from the parameter map; 'dml_selcel' executes the source as a
   * query whose single-cell result is the target URL.
   * @param PageProcessingSourceDto
   * @param parameterMap
   * @return the branch target, or null if the conditional failed or the source produced no target
   */
  public String processBranchSource(final PageProcessingSourceDto processSourceDto,
          final MultiValueMap<String, String> parameterMap);
}
