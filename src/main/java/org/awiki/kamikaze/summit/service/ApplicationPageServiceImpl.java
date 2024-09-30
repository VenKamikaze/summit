package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.render.ApplicationPageDto;
import org.awiki.kamikaze.summit.mapper.ApplicationPageMapper;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPageServiceImpl implements ApplicationPageService {
  private static final Logger log = LoggerFactory.getLogger(ApplicationPageServiceImpl.class);
  
  private ApplicationPageRepository repository;
  private ApplicationPageMapper mapper;
  
  @Autowired
  public void setRepository(ApplicationPageRepository repository) {
    this.repository = repository;
  }

  @Autowired
  public void setMapper(ApplicationPageMapper mapper) {
    this.mapper = mapper;
  }

  private ApplicationPage loadOrCreate(ApplicationPageDto dto)  {
    ApplicationPage appPage = null;
    if(dto.getId() != null)
      appPage = repository.findById(dto.getId()).get();
    
    if(null == appPage)
      appPage = new ApplicationPage();
    
    return appPage;
  }
  
  /* (non-Javadoc)
   * @see org.awiki.kamikaze.summit.service.ApplicationPageService#save(org.awiki.kamikaze.summit.dto.entry.ApplicationPageDto)
   */
  @Override
  public ApplicationPageDto save(ApplicationPageDto dto) {
    ApplicationPage applicationPage = loadOrCreate(dto);
    mapper.updateDomain(dto, applicationPage, new CycleAvoidingMappingContext());
    save(applicationPage);
    mapper.updateDto(applicationPage, dto, new CycleAvoidingMappingContext());
    return dto; 
  }
  
  void save(ApplicationPage applicationPage) {
    log.debug("Attempting to save applicationPage object");
    repository.save(applicationPage);
  }
  
}
