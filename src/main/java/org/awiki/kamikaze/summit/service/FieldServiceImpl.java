package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class FieldServiceImpl implements FieldService {

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  public HashSet<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap) {
    LinkedHashSet<PageItem<String>> fields = new LinkedHashSet<>();
    
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
        processedContent.setPostProcessedContent(parameterMap.get(fieldDto.getName()).get(0)); // TODO FIXME make this work with multiple values
      }
      else { // process the source content to get the value, which can override pre-region processing, but not parameters in a request.
        SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(fieldDto.getCodeFieldSourceType());
        for(SourceDto source : fieldDto.getSource()) {
          processedContent.setPostProcessedContent(processedContent.getPostProcessedContent() + 
                  processor.processSource(source.getSource(), fieldDto.getCodeFieldSourceType(), null).getResultValue()); // TODO FIXME handle bind vars
        }
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

  @Override
  public FieldDto findFieldWithName(final RegionDto regionDto, final String name)
  {
    RegionFieldDto rf = IterableUtils.find(regionDto.getRegionFields(), new Predicate<RegionFieldDto>() {
      @Override
      public boolean evaluate(RegionFieldDto rf)
      {
        return rf != null && rf.getField() != null &&
                 name.equals(rf.getField().getName());
      }
    });
    
    if(rf != null) {
      return rf.getField();
    }
    return null;
  }

  @Override
  public Collection<PageItem<String>> getAllFields(PageDto pageDto)
  {
    Collection<PageItem<String>> fields = new LinkedHashSet<>();
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      for(RegionFieldDto rf : pr.getRegionDto().getRegionFields()) {
        fields.add(rf.getField());
      }
    }
    return fields;
  }
}
