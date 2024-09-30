package org.awiki.kamikaze.summit.controller.fields;

import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.service.field.FieldService;
import org.awiki.kamikaze.summit.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for fields.
 */
@RestController
public class FieldRestController {
  private static final Logger logger = LoggerFactory.getLogger(FieldRestController.class);

  // private final PageFilteringService filterService;
  // private final ProxyFormatterService sourceFormatters;
  private FieldService fieldService;

  @Autowired  
  public void setFieldService(FieldService fieldService) {
    this.fieldService = fieldService;
  }


  @RequestMapping(value = "/api/field/{fieldId}", method = { RequestMethod.GET })
  public FieldDto getField(@PathVariable final String fieldId,
      @RequestParam(required = false, name = "pageParams") final String pageParams) {

    logger.info("FIXME WIP: Hit page /api/field/" + fieldId);

    return fieldService.processFieldForRender(fieldService.getUnprocessedFieldById(Long.parseLong(fieldId)), null,
        StringUtils.toParameterMap(pageParams));
  }

}
