package org.awiki.kamikaze.summit.repository;

import org.awiki.kamikaze.summit.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

  public Template findByClassName(String className);
}
