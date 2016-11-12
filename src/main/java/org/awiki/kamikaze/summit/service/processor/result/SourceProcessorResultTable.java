package org.awiki.kamikaze.summit.service.processor.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.awiki.kamikaze.summit.dto.entry.PageItem;
import org.awiki.kamikaze.summit.service.formatter.Formattable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When our source returns a table of information, this class is what is used to store the results.
 * It consists of {@link Column}s, {@link Row}s and {@link Cell}s.
 * Cells know about their parent column and row. Each Cell should know what x and y position it has within the table.
 *  
 * (NOTE: ignore the following -- removed the implements Formattable while I decide how this should work) The whole table is {@link Formattable}, but further to that, each Row is also {@link Formattable}
 * This allows dynamic styling of reports, which means you can produce HTML style reports or CSV style reports
 * easily.
 * @author msaun
 *
 */
public class SourceProcessorResultTable implements PageItem<String>, Formattable<org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Row>
{  
  private static final Logger logger = LoggerFactory.getLogger(SourceProcessorResultTable.class);
  
  /*
   * This class only knows about the list of rows it contains.
   * Each Row knows about it's Cells
   * Each Cell knows it's value, parent row and a parent Column
   */
  
  private Long pages;
  
  private List<Row> rows = new ArrayList<Row>();
  
  public Long getPages()
  {
    return pages;
  }

  public void setPages(Long pages)
  {
    this.pages = pages;
  }

  public int getCount()
  {
    return this.rows.size();
  }

  public List<Row> getRows()
  {
    return rows;
  }

  public void setRows(List<Row> rows)
  {
    this.rows = rows;
  }

  public Cell getCellByXY(int x, int y)
  {
    return this.rows.get(y).getCell(x);
  }
  
  public Column getColumn(int x) {
    return this.rows.get(0).getCell(x).parentColumn;
  }
  
  public Row getRowByY(int y) {
    return this.rows.get(y);
  }

  public Row getHeader()
  {
    return new HeaderRow(getRowByY(0).getCells());
  }
  
  public List<Row> getBody()
  {
    List<Row> bodyRows = new ArrayList<>(getRows());
    bodyRows.remove(getHeader());
    return bodyRows;
  }
  
  public Row getFooter()
  {
    // TODO: FIXME: not implemented.
    return null;
  }


  public class Column {
    private String name;
    private int order;
    
    private List<Cell> cellsInColumn = new ArrayList<>();


    public Column(String name, int order)
    {
      this.name = name;
      this.order = order;
    }
    
    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public int getOrder()
    {
      return order;
    }

    public void setOrder(int order)
    {
      this.order = order;
    }

    public List<Cell> getCellsInColumn()
    {
      return cellsInColumn;
    }

    public void setCellsInColumn(List<Cell> cellsInColumn)
    {
      this.cellsInColumn = cellsInColumn;
    }
    
    public void addCell(Cell cell)
    {
      this.cellsInColumn.add(cell);
    }
  }
  
  public class HeaderRow extends SourceProcessorResultTable.Row {
    public HeaderRow(final List<Cell> cells) {
      super(cells);
    }
  }
  
  
  public class Row implements PageItem<String>, Iterable<Cell>, Formattable<String> {
    private List<Cell> cells = new ArrayList<>();

    public Row() { }
    
    public Row(List<Cell> cells)
    {
      this.cells = cells;
    }
    
    public List<Cell> getCells()
    {
      return cells;
    }
    
    /**
     * 0-based index
     * @param columnIndex
     * @return Cell
     */
    public Cell getCell(int columnIndex) {
      return cells.get(columnIndex);
    }

    public void setCells(List<Cell> cells)
    {
      this.cells = cells;
    }

    @Override
    public Iterator<Cell> iterator()
    {
      return new ArrayListIterator(this.cells);
    }

    @Override
    public Collection<String> getHeaderElements()
    {
      logger.debug("getHeaderElements called on row(), but row() cannot have non-body elements"); 
      return new ArrayList<String>(0);
    }

    @Override
    public Collection<String> getBodyElements()
    {
      return convertToStringCollection(this);
    }

    @Override
    public Collection<String> getFooterElements()
    {
      logger.debug("getFooterElements called on row(), but row() cannot have non-body elements");
      return new ArrayList<String>(0);
    }

    private Collection<String> convertToStringCollection(final Row row)
    {
      final Collection<String> elements = new ArrayList<>(row.getCells().size());
      for(final Cell c : row)
      {
        elements.add(c.getValue());
      }
      return elements;
    }

    @Override
    public boolean hasSubPageItems()
    {
      return false;
    }

    @Override
    public Collection<PageItem<String>> getSubPageItems()
    {
      return new ArrayList<PageItem<String>>(this.cells);
    }

    @Override
    public void setProcessedSource(String t)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public String getProcessedSource()
    {
      // TODO Auto-generated method stub
      return null;
    }
    
  }

  
  public class Cell implements PageItem<String> {
    private Row parentRow;
    private Column parentColumn;
    private String value;
    
    public Cell(Row parentRow, Column parentColumn, String value)
    {
      this.parentColumn = parentColumn;
      this.parentRow = parentRow;
      this.value = value;
    }
    
    public Row getParentRow()
    {
      return parentRow;
    }
    public void setParentRow(Row parentRow)
    {
      this.parentRow = parentRow;
    }
    public Column getParentColumn()
    {
      return parentColumn;
    }
    public void setParentColumn(Column parentColumn)
    {
      this.parentColumn = parentColumn;
    }
    public String getValue()
    {
      return value;
    }
    public void setValue(String value)
    {
      this.value = value;
    }

    @Override
    public boolean hasSubPageItems()
    {
      return false;
    }

    @Override
    public void setProcessedSource(String t)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public String getProcessedSource()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Collection<PageItem<String>> getSubPageItems()
    {
      return new ArrayList<>(0);
    }
  }
  
  @SuppressWarnings("serial")
  @Override
  public Collection<Row> getHeaderElements()
  {
    return new ArrayList<Row>(1) {{ add(getHeader()); }};
  }

  @Override
  public Collection<Row> getBodyElements()
  {
    return this.getBody();
  }

  @SuppressWarnings("serial")
  @Override
  public Collection<Row> getFooterElements()
  {
    return new ArrayList<Row>(1) {{ add(getFooter()); }};
  }

  @Override
  public boolean hasSubPageItems()
  {
    return this.rows.size() > 0;
  }

  @Override
  public Collection<PageItem<String>> getSubPageItems()
  {
    return new ArrayList<PageItem<String>>(this.rows);
  }


  @Override
  public void setProcessedSource(String t)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getProcessedSource()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
