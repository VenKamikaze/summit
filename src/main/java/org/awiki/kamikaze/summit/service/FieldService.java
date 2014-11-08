package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.dto.entry.FieldDto;

public interface FieldService {

  public FieldDto save(FieldDto dto);
  
  public FieldDto load(Integer id);
  
  public FieldDto load(FieldDto fieldDto);
  
}
