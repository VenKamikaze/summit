package org.awiki.kamikaze.summit.service.processor.result;

import java.util.ArrayList;
import java.util.List;

public class SourceProcessorResultTable
{
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
  
  
  public class Row {
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
  }
  
  public class Cell {
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
  }
}
