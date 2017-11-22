package org.awiki.kamikaze.summit.service.field.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.awiki.kamikaze.summit.dto.render.DropDownFieldDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.repository.FieldRepository;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.TabularQuerySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.SourceProcessorResultTableRowToDropDownOptionItemMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class DropDownFieldProcessorServiceImpl implements FieldProcessorService {

  @Autowired
  private FieldRepository repository;

  @Autowired
  private Mapper mapper;
  
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private SourceProcessorResultTableRowToDropDownOptionItemMapper rowToOptionItemMapper;
  
  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(1);
  static {  RESPONSIBILITIES.add(DropDownFieldDto.class.getCanonicalName()); };
  
  @Override
  public List<String> getResponsibilities()
  {
    return RESPONSIBILITIES;
  }
  
  @Override
  public HashSet<PageItem<String>> processFieldsForRender(final Collection<RegionFieldDto> regionFieldDtos, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap) {
    LinkedHashSet<PageItem<String>> fields = new LinkedHashSet<>();
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      fields.add(processFieldForRender(fieldDto, fieldsToPopulate, parameterMap));
    }
    return fields;
  }

  @Override
  public FieldDto processFieldForRender(FieldDto fieldDto,
          Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, MultiValueMap<String, String> parameterMap)
  {
    DropDownFieldDto dropdown = (DropDownFieldDto) fieldDto;
    
    // process the source content to get the contents of the drop-down
    if(dropdown.getSource() != null && dropdown.getSource().size() > 0) {
      processFieldSource(dropdown, null, false); // TODO FIXME handle bind vars
    }
    
    // Then check if any pre-region processing if exists - set the option item to selected that corresponds to this
    if (fieldsToPopulate != null && fieldsToPopulate.containsKey(fieldDto.getName()) ) {
      String value = fieldsToPopulate.get(fieldDto.getName()).getFieldValue().getResultValue();
      dropdown.setKeyAsSelected(value);
    }

    // Parameters in request can override pre-region processing, so next, set the selected option item from the page parameter if it exists
    if(parameterMap.containsKey(dropdown.getName())) {
      String value = parameterMap.get(dropdown.getName()).get(0); // TODO FIXME make this work with multiple values
      dropdown.setKeyAsSelected(value);
    }
    
    // Default value is optional, if set, then mark that as the default selected option item
    if(fieldDto.getDefaultValueSource() != null && fieldDto.getDefaultValueSource().size() > 0)
    {
      processFieldSource(dropdown, null, true); // TODO FIXME handle bind vars
    }
    
    return fieldDto;
  }
  
  private FieldDto processFieldSource(final DropDownFieldDto fieldToProcess, List<BindVar> bindVars, boolean defaultValue)
  {
    if((defaultValue && fieldToProcess.getSource() != null) || (!defaultValue && fieldToProcess.getDefaultValueSource() != null))
    {
      final List<SourceDto> sourceToProcess = defaultValue ? fieldToProcess.getDefaultValueSource() : fieldToProcess.getSource();
      final String sourceType = defaultValue ? fieldToProcess.getCodeFieldDefaultValueSourceType() : fieldToProcess.getCodeFieldSourceType();
      
      TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(sourceType);
      for(SourceDto source : sourceToProcess) {
        SourceProcessorResultTable table = processor.executeQuery(source.getSource(), bindVars); 
        if(table.getColumns().size() != 2) {
          throw new UnsupportedOperationException("DropDownFields must provide only two columns, a key and a value, in that order.");
        }
        if(defaultValue) {
          // if default value then assume one record and set the key as selected
          if(table.getCount() > 1) {
            throw new UnsupportedOperationException("Default Value for DropDownFields must be a single result or none");
          }
          fieldToProcess.setKeyAsSelected(table.getCellByXY(0, 0).getValue());
        }
        else {
          // If populating drop down values then map all results to DropDownOptionItems.
          fieldToProcess.setOptions(new ArrayList<>(CollectionUtils.collect(table.getBody(), rowToOptionItemMapper)));
        }
      }

    }
    return fieldToProcess;
  }

}
