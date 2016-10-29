package org.awiki.kamikaze.summit.dto.entry;


public class TemplateForListsDto {

  private long       id;
  private String     name;
  
  // For each element in a list, wrap it in the specified pre & post items.
  // Helps with e.g. building up a HTML table from a result set.
  private String     headerPre;
  private String     bodyPre;
  private String     footerPre;
  
  private String     headerPost;
  private String     bodyPost;
  private String     footerPost;
  
  private TemplateDto template;
  
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHeaderPre()
  {
    return this.headerPre;
  }

  public void setHeaderPre(final String headerSource)
  {
    this.headerPre = headerSource;
  }

  public String getBodyPre()
  {
    return this.bodyPre;
  }

  public void setBodyPre(final String bodySource)
  {
    this.bodyPre = bodySource;
  }

  public String getFooterPre()
  {
    return this.footerPre;
  }

  public void setFooterPre(final String footerSource)
  {
    this.footerPre = footerSource;
  }

  public String getHeaderPost()
  {
    return this.headerPost;
  }

  public void setHeaderPost(final String headerSource)
  {
    this.headerPost = headerSource;
  }

  public String getBodyPost()
  {
    return this.bodyPost;
  }

  public void setBodyPost(final String bodySource)
  {
    this.bodyPost = bodySource;
  }

  public String getFooterPost()
  {
    return this.footerPost;
  }

  public void setFooterPost(final String footerSource)
  {
    this.footerPost = footerSource;
  }

  public TemplateDto getTemplate()
  {
    return this.template;
  }

  public void setTemplate(TemplateDto template)
  {
    this.template = template;
  }  
}
