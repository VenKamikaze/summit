package org.awiki.kamikaze.summit.repository;

import org.awiki.kamikaze.summit.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

}
