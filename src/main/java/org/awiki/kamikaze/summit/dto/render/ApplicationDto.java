package org.awiki.kamikaze.summit.dto.render;


import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;

public class ApplicationDto 
{
  private Long id;
	 
  @NotBlank(message = "Must not be empty.")
  private Long applicationNum;
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 200)
  private String name;

  private Set<ApplicationPageDto> applicationPages = new LinkedHashSet<ApplicationPageDto>();
  //private Set<ApplicationSchemasDto> applicationSchemas = new HashSet<ApplicationSchemasDto>();
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getApplicationNum() {
    return applicationNum;
  }

  public void setApplicationNum(Long applicationNum) {
    this.applicationNum = applicationNum;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<ApplicationPageDto> getApplicationPages() {
    return applicationPages;
  }

  public void setApplicationPages(Set<ApplicationPageDto> applicationPages) {
    this.applicationPages = applicationPages;
  }

	
}
