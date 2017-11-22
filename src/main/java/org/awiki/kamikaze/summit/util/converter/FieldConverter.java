package org.awiki.kamikaze.summit.util.converter;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.domain.Conditional;
import org.awiki.kamikaze.summit.domain.Field;
import org.awiki.kamikaze.summit.domain.RegionField;
import org.awiki.kamikaze.summit.domain.Source;
import org.awiki.kamikaze.summit.domain.Template;
import org.awiki.kamikaze.summit.domain.codetable.CodeFieldType;
import org.awiki.kamikaze.summit.domain.codetable.CodeSourceType;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

public class FieldConverter extends DozerConverter<Field, FieldDto> implements MapperAware
{
  public FieldConverter(Class<Field> prototypeA, Class<FieldDto> prototypeB)
  {
    super(prototypeA, prototypeB);
  }

  private Mapper mapper;

  @Override
  public void setMapper(Mapper mapper) {
    this.mapper = mapper;
  }
  
  @Override
  public FieldDto convertTo(Field source, FieldDto destination)
  {
    if(source == null) {
      return null;
    }
    
    String dtoClassName;
    if(source.getTemplate() != null && StringUtils.isNotBlank(source.getTemplate().getClassName()) ) {
      dtoClassName = source.getTemplate().getClassName();
    }
    else {
      dtoClassName = FieldDto.class.getCanonicalName();
    }
    Object o;
    try
    {
      o = Class.forName(dtoClassName).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
    {
      throw new RuntimeException("Error instantiating FieldDto when mapping from domain");
    }
    if(! (o instanceof FieldDto)) {
      throw new RuntimeException("Cannot instantiate an object that is not a field from a field");
    }
    FieldDto dto = (FieldDto)o;
    dto.setId(source.getId());
    dto.setName(source.getName());
    dto.setNotes(source.getNotes());
    dto.setTemplate(mapper.map(source.getTemplate(), TemplateDto.class));
    dto.setConditional(mapper.map(source.getConditional(), ConditionalDto.class));
    for(RegionField rf : source.getRegionFields()) {
      dto.getRegionFields().add(mapper.map(rf, RegionFieldDto.class));
    }
    if(dto.getSource() == null &&
         (source.getSource() != null && source.getSource().size() > 0) ) {
      dto.setSource(new ArrayList<SourceDto>(source.getSource().size()));
    }
    for(Source s : source.getSource()) {
      dto.getSource().add(mapper.map(s, SourceDto.class));
    }
    if(dto.getDefaultValueSource() == null &&
            (source.getDefaultValueSource() != null && source.getDefaultValueSource().size() > 0) ) {
         dto.setDefaultValueSource(new ArrayList<SourceDto>(source.getDefaultValueSource().size()));
    }
    for(Source s : source.getDefaultValueSource()) {
      dto.getDefaultValueSource().add(mapper.map(s, SourceDto.class));
    }
    dto.setCodeFieldType(source.getCodeFieldType().getCode());
    dto.setCodeFieldSourceType(source.getCodeFieldSourceTypeBySourceType().getCode());
    dto.setCodeFieldDefaultValueSourceType(source.getCodeFieldSourceTypeByDefaultValueType().getCode());
    destination = dto;
    return dto;
  }

  @Override
  public Field convertFrom(FieldDto source, Field destination)
  {
    if(source == null) {
      return null;
    }
    if(true) {
      throw new RuntimeException("WIP, may not even end up using this mapper. FIXME TODO");
    }
      
    if(destination == null) {
      destination = new Field();
    }
    
    destination.setId(source.getId());
    destination.setName(source.getName());
    destination.setNotes(source.getNotes());
    if(destination.getConditional() == null) {
      destination.setConditional(new Conditional());
    }
    mapper.map(source.getConditional(), destination.getConditional());
    if(destination.getTemplate() == null) {
      destination.setTemplate(new Template());
    }
    mapper.map(source.getTemplate(), destination.getTemplate());
    if(destination.getCodeFieldType() == null) {
      destination.setCodeFieldType(new CodeFieldType());
    }
    destination.getCodeFieldType().setCode(source.getCodeFieldType());
    if(destination.getCodeFieldSourceTypeBySourceType() == null) {
      destination.setCodeFieldSourceTypeBySourceType(new CodeSourceType());
    }
    destination.getCodeFieldSourceTypeBySourceType().setCode(source.getCodeFieldSourceType());
    if(destination.getCodeFieldSourceTypeByDefaultValueType() == null) {
      destination.setCodeFieldSourceTypeByDefaultValueType(new CodeSourceType());
    }
    destination.getCodeFieldSourceTypeByDefaultValueType().setCode(source.getCodeFieldDefaultValueSourceType());
    if(destination.getSource() != null) {
      destination.getSource().clear();
    }
    destination.setSource(new ArrayList<Source>());
    for(SourceDto s : source.getSource()) {
      destination.getSource().add(mapper.map(s, Source.class));
    }
    if(destination.getDefaultValueSource() != null) {
      destination.getDefaultValueSource().clear();
    }
    destination.setDefaultValueSource(new ArrayList<Source>());
    for(SourceDto s : source.getDefaultValueSource()) {
      destination.getDefaultValueSource().add(mapper.map(s, Source.class));
    }
    if(destination.getRegionFields() == null) {
      destination.setRegionFields(new HashSet<RegionField>());
    }
    for(RegionFieldDto rf : source.getRegionFields()) {
      destination.getRegionFields().add(mapper.map(rf, RegionField.class));
    }

    
    return destination;
    
  }
}
