package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.mapper.BindVarMapper;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class BindVarServiceImpl implements BindVarService
{
  private BindVarMapper mapper;
  
  //  https://stackoverflow.com/questions/2309970/named-parameters-in-jdbc for regex
  static Pattern bindParameters = Pattern.compile("(?!\\B'[^']*):(\\w+)(?![^']*'\\B)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

  @Autowired
  public void setMapper(BindVarMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public List<BindVar> createBindVarsFromFields(String regionQuery, final Map<String, PageItem<String>> fields) {
    final Matcher m = bindParameters.matcher(regionQuery);
    
    Map<String, PageItem<String>> fieldsForBinding = new HashMap<String, PageItem<String>>();
    
    while(m.find()) {
      if (fields.containsKey(m.group(1))) {
        fieldsForBinding.put(m.group(1), fields.get(m.group(1)));
      }
    }
    
    return convertFieldsToBindVars(fieldsForBinding.values());
  }

  @Override
  public List<BindVar> createVarcharBindVarsFromParameterMap(String regionQuery, final MultiValueMap<String, String> parameterMap) {
    final Matcher m = bindParameters.matcher(regionQuery);
    
    final List<BindVar> bindVars = new ArrayList<>(parameterMap.size());
    
    while(m.find()) {
      if (parameterMap.containsKey(m.group(1))) {
        bindVars.add(new BindVar(parameterMap.get(m.group(1)).get(0), java.sql.Types.VARCHAR, m.group(1))); // TODO FIXME: work with multiple values!
      }
    }
    
    return bindVars;
  }

  @Override
  public List<BindVar> convertFieldsToBindVars(Collection<PageItem<String>> fields) {
    return fields.parallelStream().map(f -> mapper.map(f)).collect(Collectors.toList());
    //return new ArrayList<>(CollectionUtils.collect(fields, mapper));
  }
  
  /**
   * TODO: We should probably just use Spring's SqlParameterValue all the way through considering we are mostly
   * tied to spring anyway
   * @param bindVars
   * @return
   */
  @SuppressWarnings("unchecked") // for empty map
  public Map<String, SqlParameter> convertBindVarsToSqlParameterMap(List<BindVar> bindVars) {
    if(bindVars == null)
      return MapUtils.EMPTY_SORTED_MAP;
    
    final Map<String, SqlParameter> paramMap = new HashMap<>(bindVars.size()); // possibly this size or smaller.
    for (BindVar var : bindVars) {
      paramMap.put(var.getBindParameter(), new SqlParameterValue(new SqlParameter(var.getBindParameter(), var.getType()), var.getValue()) );
    }
    return paramMap;
  }
}
