package org.awiki.kamikaze.summit.dto.entry;



import org.hibernate.validator.constraints.NotBlank;

public class ApplicationPageDto 
{
	private Long id;
	
	private PageDto page;
	
	//@Size(max = 200)
	//@NotBlank(message = "Must not be empty.")
	private ApplicationDto application;
	
	//@NotBlank(message = "Must not be empty.")
	//private Long applicationNum;

	@NotBlank(message = "Must not be empty.")
  private Long pageNum;

  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ApplicationDto getApplication() {
    return application;
  }

  public void setApplication(ApplicationDto application) {
    this.application = application;
  }

  /*
  public Long getApplicationNum() {
    return applicationNum;
  }

  public void setApplicationNum(long applicationNum) {
    this.applicationNum = applicationNum;
  }
  */

  public Long getPageNum() {
    return pageNum;
  }

  public void setPageNum(long pageNum) {
    this.pageNum = pageNum;
  }

  public PageDto getPage() {
    return page;
  }

  public void setPage(PageDto page) {
    this.page = page;
  }

}
