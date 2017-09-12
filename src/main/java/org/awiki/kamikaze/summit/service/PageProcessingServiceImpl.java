package org.awiki.kamikaze.summit.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SQLQuerySourceProcessorServiceImpl;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.TabularQuerySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class PageProcessingServiceImpl implements PageProcessingService
{
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private BindVarService bindVarService;
  
  /* FIXME TODO: still relying on varchar only bind variables, allow other types */
  @Override
  public Map<String, PageProcessingSourceSelectDto> processSource(PageProcessingSourceDto processSourceDto,
          MultiValueMap<String, String> parameterMap)
  {
    Map<String, PageProcessingSourceSelectDto> results = new HashMap<>(processSourceDto.getPageProcessingSourceSelect().size());
    if(SingularSourceProcessorService.SINGULAR_SOURCE_TYPES.contains(processSourceDto.getCodeSourceType())) {
      final SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(processSourceDto.getCodeSourceType());
      final SourceProcessorResult sResult = processor.processSource(processSourceDto.getSource(), processSourceDto.getCodeSourceType(), 
              bindVarService.createVarcharBindVarsFromParameterMap(processSourceDto.getSource(), parameterMap));
      PageProcessingSourceSelectDto res = processSourceDto.getPageProcessingSourceSelect().size() > 0 ? processSourceDto.getPageProcessingSourceSelect().get(0) : null;
      if (res != null) {
        res.setFieldValue(sResult);
      }
      results.put(res.getFieldName(), res);
    }
    else if(SQLQuerySourceProcessorServiceImpl.BUILT_IN_SQL_DML_SELECT_ROW_TYPE.equals(processSourceDto.getCodeSourceType())) {
      final TabularQuerySourceProcessorService processor = sourceProcessors.getTabularSourceProcessorService(processSourceDto.getCodeSourceType());
      final SourceProcessorResultTable qResult = processor.executeQuery(processSourceDto.getSource(), 
              bindVarService.createVarcharBindVarsFromParameterMap(processSourceDto.getSource(), parameterMap));
      if(qResult.getCount() > 0) {
        for(PageProcessingSourceSelectDto res : processSourceDto.getPageProcessingSourceSelect()) {
          final SourceProcessorResult cell = new SourceProcessorResult();
          cell.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
          cell.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
          cell.setResultValue(qResult.getCellByXY(new Long(res.getFieldIndex()).intValue(), 1).getValue() ); // row 1 == first data row, since row 0 == header info
          res.setFieldValue(cell);
          results.put(res.getFieldName(), res);
        }
      }
    }
    else {
      throw new NotImplementedException("Unknown source type for pageProcessing: " + processSourceDto.getCodeSourceType());
    }

    return results;
  }

}
