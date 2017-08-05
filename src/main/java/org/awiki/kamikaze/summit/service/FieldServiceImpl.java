package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceImpl implements FieldService {

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  public HashSet<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, String> parameterMap) {
    HashSet<PageItem<String>> fields = new HashSet<>();
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      FieldDto.PostProcessedFieldContentDto processedDefaultContent = fieldDto.new PostProcessedFieldContentDto();
      
      // Set the value from the page parameter if it exists, and ignore processing.
      if(parameterMap.containsKey(fieldDto.getName())) {
        processedContent.setPostProcessedContent(parameterMap.get(fieldDto.getName()));
        fieldDto.setPostProcessedSource(processedContent);
      }
      else { // or process the source content to get the value.
        SingularSourceProcessorService processor = (SingularSourceProcessorService) sourceProcessors.getSourceProcessorService(fieldDto.getCodeFieldSourceType());
        processedContent.setPostProcessedContent(processor.querySource(fieldDto.getSource(), null).getResultValue()); // TODO FIXME handle bind vars
        fieldDto.setPostProcessedSource(processedContent);

        // Default value is optional
        if(StringUtils.isNotBlank(fieldDto.getDefaultValueSource()))
        {
          processedDefaultContent.setPostProcessedContent(processor.querySource( fieldDto.getDefaultValueSource() , null ).getResultValue() );  // TODO FIXME handle bind vars
          fieldDto.setPostProcessedDefaultValue(processedDefaultContent);
        }
      }

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
