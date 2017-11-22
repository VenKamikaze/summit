package org.awiki.kamikaze.summit.service.field.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.repository.FieldRepository;
import org.awiki.kamikaze.summit.service.field.FieldService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class SimpleFieldProcessorServiceImpl implements FieldProcessorService {

  @Autowired
  private FieldRepository repository;

  @Autowired
  private Mapper mapper;
  
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private ProxyFieldProcessorService fieldProcessors;
  
  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(1);
  static {  RESPONSIBILITIES.add(FieldDto.class.getCanonicalName()); };
  // Since fields all extend (or are) the base impl of FieldDto, this will probably catch every field that doesn't have a specific processor.
  
  @Override
  public List<String> getResponsibilities()
  {
    return RESPONSIBILITIES;
  }
  
  public HashSet<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap) {
    LinkedHashSet<PageItem<String>> fields = new LinkedHashSet<>();
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      final FieldProcessorService fieldProcessor = fieldProcessors.getFieldProcessorService(fieldDto.getClass().getCanonicalName());
      fields.add(fieldProcessor.processFieldForRender(fieldDto, fieldsToPopulate, parameterMap));
    }
    return fields;
  }

  @Override
  public FieldDto processFieldForRender(FieldDto fieldDto,
          Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, MultiValueMap<String, String> parameterMap)
  {
    // First, populate fields from any pre-region processing if exists.
    if (fieldsToPopulate != null && fieldsToPopulate.containsKey(fieldDto.getName()) ) {
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      processedContent.setPostProcessedContent(fieldsToPopulate.get(fieldDto.getName()).getFieldValue().getResultValue());
      fieldDto.setPostProcessedSource(processedContent);
    }
      
    // Parameters in request can override pre-region processing, so second, set the value from the page parameter if it exists
    if(parameterMap.containsKey(fieldDto.getName())) {
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      processedContent.setPostProcessedContent(parameterMap.get(fieldDto.getName()).get(0)); // TODO FIXME make this work with multiple values
      fieldDto.setPostProcessedSource(processedContent);
    }
    else { 
      // process the source content to get the value, which can override pre-region processing, but not parameters in a request.
      if(fieldDto.getSource() != null && fieldDto.getSource().size() > 0) {
        fieldDto = processFieldSource(fieldDto, null, false); // TODO FIXME handle bind vars
      }
    }
    
    // Default value is optional, if set, and there's no processed content, it may be displayed.
    if(fieldDto.getDefaultValueSource() != null && fieldDto.getDefaultValueSource().size() > 0)
    {
      fieldDto = processFieldSource(fieldDto, null, true); // TODO FIXME handle bind vars);
    }
    
    return fieldDto;
  }
  
  private FieldDto processFieldSource(final FieldDto fieldToProcess, List<BindVar> bindVars, boolean defaultValue)
  {
    if((defaultValue && fieldToProcess.getSource() != null) || (!defaultValue && fieldToProcess.getDefaultValueSource() != null))
    {
      FieldDto.PostProcessedFieldContentDto content = fieldToProcess.new PostProcessedFieldContentDto();
      final List<SourceDto> sourceToProcess = defaultValue ? fieldToProcess.getDefaultValueSource() : fieldToProcess.getSource();
      final String sourceType = defaultValue ? fieldToProcess.getCodeFieldDefaultValueSourceType() : fieldToProcess.getCodeFieldSourceType();
      
      SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(sourceType);
      if(processor != null) {
        String result = StringUtils.EMPTY;
        for(SourceDto source : sourceToProcess) {
          result += processor.processSource(source.getSource(), sourceType, bindVars).getResultValue();
        }
        content.setPostProcessedContent(result);
      }
      else {
        throw new UnsupportedOperationException("Multi-result select query for a field is not implemented yet!");
      }
      if(! defaultValue) {
        fieldToProcess.setPostProcessedSource(content);
      }
      else {
        fieldToProcess.setPostProcessedDefaultValue(content);
      }
    }
    return fieldToProcess;
  }

}
