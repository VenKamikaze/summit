package org.awiki.kamikaze.summit.service.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.dto.entry.TemplateDto;
import org.awiki.kamikaze.summit.dto.entry.TemplateForListsDto;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Row;
import org.springframework.stereotype.Service;

// TODO: FIXME: This must be data driven. Lets get the implementation going for now, then figure out how to 
//      drive it from the DB. 2016-10-23

// Hande a SourceProcessorResultTable, and style it with templating from the DB as needed
@Service
public class TableFormatterServiceImpl implements TableFormatterService
{
  private static final int HEADER_ROWS = 1;
  private static final int BODY_ROWS = 2;
  private static final int FOOTER_ROWS = 3;
  
  final int assumedCellWidth = 12; // lets guess 12 characters per cell, to help estimating stringbuilder buffer size
  
  /**
   * This method takes in a SourceProcessorResultTable, it should add pre + post elements
   * per row of the table. It then iterates over each Row and adds pre + post elements around each cell
   * @param templateDto
   * @param sourceTable
   * @return
   */
  @Override
  public String format(final TemplateDto templateDto, Formattable<Row> sourceTable) {
    final int cellsPerRow = sourceTable.getHeaderElements().iterator().next().getCells().size();
    int formattedLength = templateDto.getHeaderSource().length()
                        + templateDto.getBodySource().length() 
                        + templateDto.getFooterSource().length()
                        + (sourceTable.getHeaderElements().size() * cellsPerRow * assumedCellWidth)
                        + (sourceTable.getFooterElements().size() * cellsPerRow * assumedCellWidth)
                        + (sourceTable.getBodyElements().size() * cellsPerRow * assumedCellWidth);
    
    StringBuilder formatted = new StringBuilder(formattedLength);
    if(true) throw new NotImplementedException("FIXME: implement me.");
    
    formatRows(templateDto.getTemplatesForLists(), sourceTable.getHeaderElements(), HEADER_ROWS); // get the top row
    formatRows(templateDto.getTemplatesForLists(), sourceTable.getBodyElements(), BODY_ROWS); // get the rest of the rows
    formatRows(templateDto.getTemplatesForLists(), sourceTable.getFooterElements(), FOOTER_ROWS); 
    
    return formatted.toString();
  }
  
  private String formatRows(final Collection<TemplateForListsDto> listTemplates, final Collection<Formattable<String>> rows, final int type) {

    // TODO: refactor me
    
    String preElement = "", postElement = "";
    if(HEADER_ROWS == type)
    {
      for(TemplateForListsDto t : listTemplates)
      {
        preElement += t.getHeaderPre();
        postElement += t.getHeaderPost();
      }
    }
    else if(BODY_ROWS == type)
    {
      for(TemplateForListsDto t : listTemplates)
      {
        preElement += t.getBodyPre();
        postElement += t.getBodyPost();
      }
    }
    else if(FOOTER_ROWS == type)
    {
      for(TemplateForListsDto t : listTemplates)
      {
        preElement += t.getFooterPre();
        postElement += t.getFooterPost();
      }
    }
    else
    {
      throw new UnsupportedOperationException("Unknown table row type: " + type);
    }

    StringBuilder rowsBuilder = new StringBuilder();

    // Iterate over each row (Formattable<String>) within the rows, add appropriate pre+post elements 
    // around each row, but also then wrap the cells too.
    for(Formattable<String> row: rows)
    {
      rowsBuilder.append(preElement).append(formatCells(row)).append(postElement);
    }
    return rowsBuilder.toString();
  }

  private String formatCells(final TemplateForListsDto listTemplate, final Formattable<String> row)
  {
    StringBuilder rowBuilder = new StringBuilder();
    
    for()
    {
      rowBuilder.append(listTemplate.getBodyPre()).append(
    }
  }
  
  @SuppressWarnings("serial")
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(1) {{ add(SourceProcessorResultTable.class.getCanonicalName()); }};
  }
}
