package org.awiki.kamikaze.summit.repository;

import java.util.List;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationPageRepository extends JpaRepository<ApplicationPage, Long> {

  public List<ApplicationPage> findAllByOrderByPageNum();
  
  public ApplicationPage findByApplicationIdAndPageId(long applicationId, long pageid);
}
