package org.awiki.kamikaze.summit.service.processor.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public class SourceProcessorResultTableExtractor implements ResultSetExtractor<SourceProcessorResultTable>
{
  
  /** 
   * Missing source, rename this to match ResultSetExtractor public method to process results from RS
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
        final SourceProcessorResultTable.Row row = results.new Row();
        row.setCells(new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount()) {{
          for(int i = 0; i < rsm.getColumnCount(); ++i) {
            add(results.new Cell(row, cols.get(i), rsm.getColumnLabel(i)) );
          }
          }}
        );
        rows.add(row);
      }

      do {
        SourceProcessorResultTable.Row row = results.new Row();
        List<SourceProcessorResultTable.Cell> rowOfCells = new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount());
        for(int i = 0; i < rsm.getColumnCount(); ++i) {
          SourceProcessorResultTable.Cell cell = results.new Cell(row, cols.get(i), rs.getString(i));
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
