package org.awiki.kamikaze.summit.service.processor.bindvars;


public class BindVar<T extends java.sql.Types>
{
  private String bindParameter;
  private Object value;
  private T type;
  
  public BindVar(Object value, T type, final String bindParameter)
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
  
  public T getType()
  {
    return type;
  }

  public void setType(T type)
  {
    this.type = type;
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
