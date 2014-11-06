package org.awiki.kamikaze.summit.repository;

import org.awiki.kamikaze.summit.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}
