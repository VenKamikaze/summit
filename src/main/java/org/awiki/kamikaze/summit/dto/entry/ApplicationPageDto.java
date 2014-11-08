package org.awiki.kamikaze.summit.dto.entry;



import org.hibernate.validator.constraints.NotBlank;

public class ApplicationPageDto 
{
	private Integer id;
	
	private PageDto page;
	
	//@Size(max = 200)
	//@NotBlank(message = "Must not be empty.")
	private ApplicationDto application;
	
	@NotBlank(message = "Must not be empty.")
	private Integer applicationNum;

	@NotBlank(message = "Must not be empty.")
  private Integer pageNum;

  public Integer getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ApplicationDto getApplication() {
    return application;
  }

  public void setApplication(ApplicationDto application) {
    this.application = application;
  }

  public Integer getApplicationNum() {
    return applicationNum;
  }

  public void setApplicationNum(int applicationNum) {
    this.applicationNum = applicationNum;
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public PageDto getPage() {
    return page;
  }

  public void setPage(PageDto page) {
    this.page = page;
  }

}