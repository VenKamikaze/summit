package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.util.MultiValueMap;

public interface BindVarService
{
  /**
   * From a source statement (String query) and a set of Map<String fieldName, PageItem field>, create a List<BindVar>
   * @param query - the source statement with bind vars being referenced within (via syntax :bindVar)
   * @param Map<FieldName, Field>
   * @return List<BindVar>
   */
  public List<BindVar> createBindVarsFromFields(final String query, final Map<String, PageItem<String>> fields);
  
  /**
   * From a source statement (String query) and a Map<String, String> urlParameters, create a List<BindVar> (each of Type varchar)
   * @param query - the source statement with bind vars being referenced within (via syntax :bindVar)
   * @param Map<String, String> parameterMap
   * @return List<BindVar> (each of Type varchar)
   */
  public List<BindVar> createVarcharBindVarsFromParameterMap(final String query, final MultiValueMap<String, String> parameterMap);
  
  /**
   * @param fields that have already been processed and have PostProcessed values.
   * @return
   */
  public List<BindVar> convertFieldsToBindVars(Collection<PageItem<String>> fields);
  
  /**
   * Before performing actual queries against a JDBC driver, we need to convert our BindVar types into
   * Spring SqlParamter types.
   * TODO: We should probably just use Spring's SqlParameterValue all the way through considering we are mostly
   * tied to spring anyway
   * @param bindVars
   * @return
   */
  public Map<String, SqlParameter> convertBindVarsToSqlParameterMap(List<BindVar> bindVars);
}
