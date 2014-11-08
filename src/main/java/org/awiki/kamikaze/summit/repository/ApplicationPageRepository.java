package org.awiki.kamikaze.summit.repository;

import java.util.List;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationPageRepository extends JpaRepository<ApplicationPage, Integer> {

  public List<ApplicationPage> findAllOrderByPageNum();
}