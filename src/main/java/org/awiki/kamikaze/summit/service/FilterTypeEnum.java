package org.awiki.kamikaze.summit.service;

public enum FilterTypeEnum
{
  EXACT("exact", "="),
  CONTAINS("contains", "LIKE"),
  STARTSWITH("startsWith", "LIKE"),
  ENDSWITH("endsWith", "LIKE");
  
  private String type;
  private String sql;
  
  FilterTypeEnum(final String type, final String sql) {
    this.type = type;
    this.sql = sql;
  }
  
  public String getType() {
    return type;
  }
  
  public String getSql() {
    return sql;
  }
  
  @Override
  public String toString() {
    return getType();
  }
}
