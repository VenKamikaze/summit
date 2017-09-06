package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceImpl implements FieldService {

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  public HashSet<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final Map<String, String> parameterMap) {
    HashSet<PageItem<String>> fields = new HashSet<>();
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      FieldDto.PostProcessedFieldContentDto processedDefaultContent = fieldDto.new PostProcessedFieldContentDto();
      
      // First, populate fields from any pre-region processing if exists.
      if (fieldsToPopulate.containsKey(fieldDto.getName()) ) {
        processedContent.setPostProcessedContent(fieldsToPopulate.get(fieldDto.getName()).getFieldValue().getResultValue());
      }
        
      // Parameters in request can override pre-region processing, so second, set the value from the page parameter if it exists
      if(parameterMap.containsKey(fieldDto.getName())) {
        processedContent.setPostProcessedContent(parameterMap.get(fieldDto.getName()));
        fieldDto.setPostProcessedSource(processedContent);
      }
      else { // process the source content to get the value, which can override pre-region processing, but not parameters in a request.
        SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(fieldDto.getCodeFieldSourceType());
        String processedSource = "";
        for(SourceDto source : fieldDto.getSource()) {
          processedSource += processor.processSource(source.getSource(), fieldDto.getCodeFieldSourceType(), null).getResultValue(); // TODO FIXME handle bind vars
        }
        processedContent.setPostProcessedContent(processedSource);      
      }
      
      // Default value is optional, if set, and there's no processed content, it may be displayed.
      if(StringUtils.isNotBlank(fieldDto.getDefaultValueSource()))
      {
        SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(fieldDto.getCodeFieldDefaultValueSourceType());
        processedDefaultContent.setPostProcessedContent(processor.processSource( fieldDto.getDefaultValueSource() , fieldDto.getCodeFieldDefaultValueSourceType(), null ).getResultValue() );  // TODO FIXME handle bind vars
        fieldDto.setPostProcessedDefaultValue(processedDefaultContent);
      }
      
      fieldDto.setPostProcessedSource(processedContent);
      fields.add(fieldDto);
    }
    return fields;
  }

  @Override
  public Map<String, PageItem<String>> fieldsToMap(Collection<PageItem<String>> fields)
  {
    final HashMap<String, PageItem<String>> nameFieldMap = new HashMap<>(fields.size());
    for(final PageItem<String> field : fields) {
      nameFieldMap.put(field.getName(), field);
    }
    return nameFieldMap;
  }
}
