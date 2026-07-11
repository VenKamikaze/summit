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
      if(FieldConstants.TYPE_NUMBER.equals(f.getCodeFieldType())) {
        // A NUMBER field populated with a blank value (e.g. a form re-rendered
        // after a failed validation) binds as SQL NULL, same as an unpopulated
        // field - new BigDecimal("") can only ever throw.
        return f.getProcessedSource().toString().trim().isEmpty()
           ? mapOther(f)
           : new BindVar(new BigDecimal(f.getProcessedSource().toString()), java.sql.Types.NUMERIC, f.getName());
      }
      return new BindVar(f.getProcessedSource(), java.sql.Types.VARCHAR, f.getName());
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
