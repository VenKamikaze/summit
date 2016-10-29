package org.awiki.kamikaze.summit.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "template", schema = "public")
public class TemplateForLists  implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2184401134662692768L;
  
  private long       id;
  private Template   template;
  private String     name;
  
  // For each element in a list, wrap it in the specified pre & post items.
  // Helps with e.g. building up a HTML table from a result set.
  private String     headerPre;
  private String     bodyPre;
  private String     footerPre;
  
  private String     headerPost;
  private String     bodyPost;
  private String     footerPost;

  public TemplateForLists()
  {
  }

  public TemplateForLists(long id, final String name, final String headerPre,
      final String bodyPre,final String footerPre,final String headerPost,
      final String bodyPost,final String footerPost)
  {
    this.id = id;
    this.name = name;
    this.headerPre = headerPre;
    this.bodyPre = bodyPre;
    this.footerPre = footerPre;

    this.headerPost = headerPost;
    this.bodyPost = bodyPost;
    this.footerPost = footerPost;
  }

  public TemplateForLists(long id, final String name, final String headerPre,
          final String bodyPre,final String footerPre,final String headerPost,
          final String bodyPost,final String footerPost, Template template)
  {
    this(id, name, headerPre, bodyPre, footerPre, headerPost, bodyPost, footerPost);
    this.template = template;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "header_pre", nullable = true)
  public String getHeaderPre()
  {
    return this.headerPre;
  }

  public void setHeaderPre(final String headerSource)
  {
    this.headerPre = headerSource;
  }

  @Column(name = "body_pre", nullable = true)
  public String getBodyPre()
  {
    return this.bodyPre;
  }

  public void setBodyPre(final String bodySource)
  {
    this.bodyPre = bodySource;
  }

  @Column(name = "footer_pre", nullable = true)
  public String getFooterPre()
  {
    return this.footerPre;
  }

  public void setFooterPre(final String footerSource)
  {
    this.footerPre = footerSource;
  }

  @Column(name = "header_post", nullable = true)
  public String getHeaderPost()
  {
    return this.headerPost;
  }

  public void setHeaderPost(final String headerSource)
  {
    this.headerPost = headerSource;
  }

  @Column(name = "body_post", nullable = true)
  public String getBodyPost()
  {
    return this.bodyPost;
  }

  public void setBodyPost(final String bodySource)
  {
    this.bodyPost = bodySource;
  }

  @Column(name = "footer_post", nullable = true)
  public String getFooterPost()
  {
    return this.footerPost;
  }

  public void setFooterPost(final String footerSource)
  {
    this.footerPost = footerSource;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  public Template getTemplate()
  {
    return this.template;
  }

  public void setTemplate(Template template)
  {
    this.template = template;
  }

  
}
