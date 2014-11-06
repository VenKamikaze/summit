package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.entry.ApplicationPageDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPageService {

  @Autowired
  ApplicationPageRepository repository;
  
  private static final Logger log = LoggerFactory.getLogger(ApplicationPageService.class);
  
  @Autowired
  Mapper mapper;
  
  private ApplicationPage loadOrCreate(ApplicationPageDto dto)  {
    ApplicationPage appPage = null;
    if(dto.getId() != null)
      appPage = repository.findOne(dto.getId());
    
    if(null == appPage)
      appPage = new ApplicationPage();
    
    return appPage;
  }
  
  public ApplicationPageDto save(ApplicationPageDto dto) {
    ApplicationPage applicationPage = loadOrCreate(dto);
    mapper.map(dto, applicationPage);
    save(applicationPage);
    mapper.map(applicationPage, dto);
    return dto; 
  }
  
  void save(ApplicationPage applicationPage) {
    log.debug("Attempting to save applicationPage object");
    repository.save(applicationPage);
  }
  
}
