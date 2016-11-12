package org.awiki.kamikaze.summit.service.processor.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.awiki.kamikaze.summit.service.processor.SQLReportSourceProcessorServiceImpl;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Column;

/**
 * This class implements required methods to transform and populate a SourceProcessorResultTable from a
 * standard SQL ResultSet.
 * @author msaun
 *
 */
public class SourceProcessorResultTableExtractor implements ResultSetExtractor<SourceProcessorResultTable>
{
  private static final Logger logger = LoggerFactory.getLogger(SQLReportSourceProcessorServiceImpl.class);
  
  
  /** 
   * This extracts data from a standard SQL ResultSet and stores it in a SourceProcessorResultTable.
   * @param rs
   */
  @SuppressWarnings("serial")
  @Override
  public SourceProcessorResultTable extractData(ResultSet rs) throws SQLException, DataAccessException
  {
    final SourceProcessorResultTable results = new SourceProcessorResultTable();
    List<SourceProcessorResultTable.Row> rows = new ArrayList<SourceProcessorResultTable.Row>();
    /*if(rs.last())
    {
      // can only do this if we set TYPE_SCROLL_INSENSITIVE for every query... TODO perhaps consider this?
      rows = new ArrayList<SourceProcessorResultTable.Row>(rs.getRow() + 1); // add 1 for column names
    }*/
    if(rs.first())
    {
      final ResultSetMetaData rsm = rs.getMetaData();
      final List<SourceProcessorResultTable.Column> cols = new ArrayList<SourceProcessorResultTable.Column>(rsm.getColumnCount());

      if(rsm != null) {
        final SourceProcessorResultTable.Row row = results.new HeaderRow();
        row.setCells(new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount()) {{
          logger.debug(SourceProcessorResultTableExtractor.class.getCanonicalName() + ": " + "rsm.getColumnCount=" + rsm.getColumnCount());
          logger.debug(SourceProcessorResultTableExtractor.class.getCanonicalName() + ": " + "cols.size=" + cols.size());
          logger.debug(SourceProcessorResultTableExtractor.class.getCanonicalName() + ": " + "rsm.getColumnLabel(1)=" + rsm.getColumnLabel(1));

          for(int i = 0; i < rsm.getColumnCount(); ++i) {
            if(cols.size() != rsm.getColumnCount())
            {
              logger.debug(SourceProcessorResultTableExtractor.class.getCanonicalName() + ": " + "new Col(\""+rsm.getColumnLabel(i+1)+"\","+i+");");
              cols.add(results.new Column(rsm.getColumnLabel(i+1), i));
            }
            add(results.new Cell(row, cols.get(i), (rsm.getColumnLabel(i+1)) ));
          }
        }});
        rows.add(row);
      }

      do {
        SourceProcessorResultTable.Row row = results.new Row();
        List<SourceProcessorResultTable.Cell> rowOfCells = new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount());
        for(int i = 0; i < rsm.getColumnCount(); ++i) {
          SourceProcessorResultTable.Cell cell = results.new Cell(row, cols.get(i), rs.getString(i+1));
          cols.get(i).addCell(cell);
          rowOfCells.add(cell);
        }
        row.setCells(rowOfCells);
        rows.add(row);
      } while(rs.next());
      
      results.setRows(rows);
    }
    return results;
  }

}
