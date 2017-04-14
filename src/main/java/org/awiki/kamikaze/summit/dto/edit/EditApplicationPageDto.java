package org.awiki.kamikaze.summit.dto.edit;

import org.hibernate.validator.constraints.NotBlank;

public class EditApplicationPageDto 
{
	private Long id;
	
	private EditPageDto page;
	
	@NotBlank(message = "Must not be empty.")
  private Long pageNum;

  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getPageNum() {
    return pageNum;
  }

  public void setPageNum(long pageNum) {
    this.pageNum = pageNum;
  }

  public EditPageDto getPage() {
    return page;
  }

  public void setPage(EditPageDto page) {
    this.page = page;
  }

}
