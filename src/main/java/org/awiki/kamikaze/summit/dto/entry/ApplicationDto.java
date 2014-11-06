package org.awiki.kamikaze.summit.dto.entry;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class ApplicationDto 
{
	private int id;
	 
  @NotBlank(message = "Must not be empty.")
  private int applicationNum;
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 200)
  private String name;

  private Set<ApplicationPageDto> applicationPages = new LinkedHashSet<ApplicationPageDto>();
  
  //private Set<ApplicationSchemasDto> applicationSchemas = new HashSet<ApplicationSchemasDto>();
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getApplicationNum() {
    return applicationNum;
  }

  public void setApplicationNum(int applicationNum) {
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
