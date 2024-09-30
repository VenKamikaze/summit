package org.awiki.kamikaze.summit.service.field.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.ListUtils;
import org.awiki.kamikaze.summit.dto.render.DropDownFieldDto;
import org.awiki.kamikaze.summit.dto.render.DropDownOptionItemDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.mapper.SourceProcessorResultTableRowMapper;
import org.awiki.kamikaze.summit.repository.FieldRepository;
import org.awiki.kamikaze.summit.service.field.TemplateFetchForKeyValuePairItemClosure;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.TabularQuerySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class DropDownFieldProcessorServiceImpl implements FieldProcessorService {

  private ProxySourceProcessorService sourceProcessors;
  private SourceProcessorResultTableRowMapper rowToOptionItemMapper;
  private TemplateFetchForKeyValuePairItemClosure templateFetcher;
  
  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(1);
  static {  RESPONSIBILITIES.add(DropDownFieldDto.class.getCanonicalName()); };
  
  @Autowired
  public void setSourceProcessors(ProxySourceProcessorService sourceProcessors) {
    this.sourceProcessors = sourceProcessors;
  }

  @Autowired
  public void setRowToOptionItemMapper(SourceProcessorResultTableRowMapper rowToOptionItemMapper) {
    this.rowToOptionItemMapper = rowToOptionItemMapper;
  }

  @Autowired
  public void setTemplateFetcher(TemplateFetchForKeyValuePairItemClosure templateFetcher) {
    this.templateFetcher = templateFetcher;
  }



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
    if(ListUtils.emptyIfNull(fieldDto.getSource()).size() == 0)
    {
      // if we have no source, then we cannot set any default values/parameters/pre-processing as the dropdown has no option items.
      return fieldDto;
    }

    // process the source content to get the contents of the drop-down
    DropDownFieldDto dropdown = ((DropDownFieldDto)fieldDto);
    dropdown.setOptions(processSource(fieldDto.getSource(), fieldDto.getCodeFieldSourceType(), null));  // TODO FIXME handle bind vars
    
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
      List<DropDownOptionItemDto> defaultOption = processSource(fieldDto.getDefaultValueSource(), fieldDto.getCodeFieldDefaultValueSourceType(), null); // TODO FIXME handle bind vars
      if(ListUtils.emptyIfNull(defaultOption).size() == 1)
      {
        dropdown.setKeyAsSelected(defaultOption.get(0).getKey());
      }
    }
    
    return dropdown;
  }
  
  private List<DropDownOptionItemDto> processSource(final List<SourceDto> sourceToProcess, final String sourceType, final List<BindVar> bindVars)
  {
    List<DropDownOptionItemDto> items = new ArrayList<>();

    TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(sourceType);
    for(SourceDto source : sourceToProcess) {
      SourceProcessorResultTable table = processor.executeQuery(source.getSource(), bindVars);
      
      if(table.getColumns().size() != 2) {
        throw new UnsupportedOperationException("DropDownFields must provide only two columns, a key and a value, in that order.");
      }
      
      items.addAll(table.getBody().stream().map(b -> rowToOptionItemMapper.map(b)).collect(Collectors.toList()));
    }

    IteratorUtils.forEach(items.iterator(), templateFetcher);
    return items;
  }
  
}
