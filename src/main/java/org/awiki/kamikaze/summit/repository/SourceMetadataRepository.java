package org.awiki.kamikaze.summit.repository;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceMetadataRepository extends JpaRepository<SourceMetadata, Long> {

}
