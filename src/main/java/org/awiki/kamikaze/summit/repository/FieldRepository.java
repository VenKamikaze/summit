package org.awiki.kamikaze.summit.repository;

import org.awiki.kamikaze.summit.domain.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

}
