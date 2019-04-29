package org.awiki.kamikaze.summit.dto.render;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.OrderBy;

public class SourceMetadataDto implements java.io.Serializable
{
  private static final long serialVersionUID = -34196633313396L;
  
  private long                        id;
  private SourceDto                   source;
  private List<SourceMetadataColsDto> sourceMetadataCols = new ArrayList<>();
  private Date                        dateCreated;
  private Date                        lastUpdated;
  private String                      updatedBy;
  
  public SourceMetadataDto()
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

  public SourceDto getSource()
  {
    return source;
  }

  public void setSource(SourceDto source)
  {
    this.source = source;
  }

  @OrderBy("columnOrder asc")
  public List<SourceMetadataColsDto> getSourceMetadataCols()
  {
    return sourceMetadataCols;
  }

  public void setSourceMetadataCols(List<SourceMetadataColsDto> sourceMetadataCols)
  {
    this.sourceMetadataCols = sourceMetadataCols;
  }

  public Date getDateCreated()
  {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated)
  {
    this.dateCreated = dateCreated;
  }

  public Date getLastUpdated()
  {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated)
  {
    this.lastUpdated = lastUpdated;
  }

  public String getUpdatedBy()
  {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy)
  {
    this.updatedBy = updatedBy;
  }
}
