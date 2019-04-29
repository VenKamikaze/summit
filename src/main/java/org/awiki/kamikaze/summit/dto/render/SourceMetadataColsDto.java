package org.awiki.kamikaze.summit.dto.render;

public class SourceMetadataColsDto implements java.io.Serializable
{
  private static final long serialVersionUID = -79499543396L;
  
  private long                      id;
  private SourceMetadataDto         sourceMetadata;
  private long                      columnOrder;
  private String                    columnName;
  private long                      columnType;
  
  public SourceMetadataColsDto()
  {
  }

  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public SourceMetadataDto getSourceMetadata()
  {
    return this.sourceMetadata;
  }

  public void setSourceMetadata(SourceMetadataDto sourceMetadata)
  {
    this.sourceMetadata = sourceMetadata;
  }

  public long getColumnOrder()
  {
    return columnOrder;
  }

  public void setColumnOrder(long columnOrder)
  {
    this.columnOrder = columnOrder;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public void setColumnName(String columnName)
  {
    this.columnName = columnName;
  }

  public long getColumnType()
  {
    return columnType;
  }

  public void setColumnType(long columnType)
  {
    this.columnType = columnType;
  }
}
