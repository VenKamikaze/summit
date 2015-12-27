package org.awiki.kamikaze.summit.service.processor.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SourceProcessorResultTableExtractor extends ResultSetExtractor<SourceProcessorResultTable>
{
  
  /** 
   * Missing source, rename this to match ResultSetExtractor public method to process results from RS
   * @param rs
   */
  public SourceProcessorResultTable extract(ResultSet rs)
  {
    SourceProcessorResultTable results = new SourceProcessorResultTable();
    List<SourceProcessorResultTable.Row> rows;
    if(rs.last())
    {
      rows = new ArrayList<SourceProcessorResultTable.Row>(rs.getRow() + 1); // add 1 for column names
    }
    if(rs.first())
    {
      final ResultSetMetaData rsm = rs.getMetaData();
      final List<SourceProcessorResultTable.Column> cols = new ArrayList<SourceProcessorResultTable.Column>(rsm.getColumnCount());

      if(rsm != null) {
        final SourceProcessorResultTable.Row row = new SourceProcessorResultTable.Row();
        row.setCells(new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount()) {{
          for(int i = 0; i < rsm.getColumnCount(); ++i) {
            add(new SourceProcessorResultTable.Cell(row, cols.get(i), rsm.getColumnLabel(i)) );
          }
          }}
        );
        rows.add(row);
      }

      do {
        SourceProcessorResultTable.Row row = new SourceProcessorResultTable.Row();
        List<SourceProcessorResultTable.Cell> rowOfCells = new ArrayList<SourceProcessorResultTable.Cell>(rsm.getColumnCount());
        for(int i = 0; i < rsm.getColumnCount(); ++i) {
          SourceProcessorResultTable.Cell cell = new SourceProcessorResultTable.Cell(row, cols.get(i), rs.getString(i));
          cols.get(i).addCell(cell);
          rowOfCells.add(cell);
        }
        row.setCells(rowOfCells);
        rows.add(row);
      } while(rs.next());
      
      results.setRows(rows);
      results.setCount((long) rows.size());
    }
    return results;
  }
}
