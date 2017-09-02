package org.awiki.kamikaze.summit.dto.render;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;

public class SourceDto implements java.io.Serializable
{
  private static final long serialVersionUID = -87324744496L;
  
  private long                         id;
  
  @NotBlank(message = "Must not be empty.")
  private String                       source;
  
  private Set<RegionDto>               regions = new HashSet<RegionDto>(0);
  private Set<FieldDto>                fields = new HashSet<FieldDto>(0);
  private Set<PageProcessingSourceDto> pageProcessingSource = new HashSet<PageProcessingSourceDto>(0);
  private SourceMetadataDto            sourceMetadata = null;
  
  public SourceDto()
  {
  }

  public SourceDto(long id, String  source)
  {
    this.id = id;
    this.source = source;
  }

  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String  getSource()
  {
    return this.source;
  }

  public void setSource( String source)
  {
    this.source = source;
  }
  
  public Set<RegionDto> getRegions()
  {
    return regions;
  }

  public void setRegions(Set<RegionDto> regions)
  {
    this.regions = regions;
  }
  
  
  public Set<FieldDto> getFields()
  {
    return fields;
  }

  public void setFields(Set<FieldDto> fields)
  {
    this.fields = fields;
  }

  public Set<PageProcessingSourceDto> getPageProcessingSource()
  {
    return pageProcessingSource;
  }

  public void setPageProcessingSource(Set<PageProcessingSourceDto> pageProcessingSource)
  {
    this.pageProcessingSource = pageProcessingSource;
  }

  public SourceMetadataDto getSourceMetadata()
  {
    return sourceMetadata;
  }

  public void setSourceMetadata(SourceMetadataDto sourceMetadata)
  {
    this.sourceMetadata = sourceMetadata;
  }
  
}
