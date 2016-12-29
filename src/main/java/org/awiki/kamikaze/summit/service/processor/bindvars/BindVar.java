package org.awiki.kamikaze.summit.service.processor.bindvars;

import java.lang.reflect.Field;
import java.sql.Types;


public class BindVar
{
  private String bindParameter;
  private Object value;
  private int type;
  
  public BindVar(Object value, int type, final String bindParameter)
  {
    this.type = type;
    this.bindParameter = bindParameter;
    this.value = value;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void setValue(final Object v)
  {
    this.value = v;
  }
  
  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }
  
  public String getTypeNameAsString() {
    for(Field field : Types.class.getFields()) {
      try
      {
        if(this.type == (int) field.get(null)) {
          return field.getName();
        }
      }
      catch (IllegalArgumentException | IllegalAccessException e)
      {
        throw new RuntimeException("Unexpected exception when determining java.sql.type with int = " + this.type, e);
      }
    }
    return null;
  }

  public String getBindParameter()
  {
    return bindParameter;
  }

  public void setBindParameter(final String bindParameter)
  {
    this.bindParameter = bindParameter;
  }
}
