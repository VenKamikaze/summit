package org.awiki.kamikaze.summit.service.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.repository.FieldRepository;
import org.awiki.kamikaze.summit.service.field.processor.FieldProcessorService;
import org.awiki.kamikaze.summit.service.field.processor.ProxyFieldProcessorService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class FieldServiceImpl implements FieldService {

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

  public FieldDto processFieldForRender(FieldDto fieldDto,
          Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, MultiValueMap<String, String> parameterMap)
  {
    return fieldProcessors.getFieldProcessorService(fieldDto.getClass().getCanonicalName()).processFieldForRender(fieldDto, fieldsToPopulate, parameterMap);
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

  @Override
  public FieldDto getUnprocessedFieldById(Long fieldId)
  {
    return mapper.map(repository.findOne(fieldId), FieldDto.class);
  }

}
