package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.util.mapper.PageItemToBindVarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BindVarServiceImpl implements BindVarService
{
  @Autowired
  private PageItemToBindVarMapper mapper;
  
  @Override
  public List<BindVar> convertFieldsToBindVars(Collection<PageItem<String>> fields)
  {
    return new ArrayList<>(CollectionUtils.collect(fields, mapper));
  }
}
