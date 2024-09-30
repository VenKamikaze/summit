package org.awiki.kamikaze.summit.mapper;

import java.math.BigDecimal;

import org.awiki.kamikaze.summit.constants.FieldConstants;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import lombok.NonNull;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface BindVarMapper 
{
  default BindVar mapFieldDto(@NonNull FieldDto f) {
    if(f.getProcessedSource() != null)
	{
      return (FieldConstants.TYPE_NUMBER.equals(f.getCodeFieldType())) ? 
         new BindVar(new BigDecimal(f.getProcessedSource().toString()), java.sql.Types.NUMERIC, f.getName()) :
         new BindVar(f.getProcessedSource(), java.sql.Types.VARCHAR, f.getName());
	}
    return mapOther(f);
  }
  
  default BindVar mapOther(@NonNull PageItem<?> p) {
    return new BindVar(null, java.sql.Types.NULL, p.getName());
  }
  
  default BindVar map(@NonNull PageItem<?> p) {
    if(p instanceof FieldDto) {
      return mapFieldDto((FieldDto) p);   	
    }
    return mapOther(p);
  }
}
