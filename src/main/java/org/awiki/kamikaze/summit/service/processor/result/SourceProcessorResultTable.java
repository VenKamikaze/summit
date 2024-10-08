package org.awiki.kamikaze.summit.service.processor.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.collections4.MapUtils;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Objects;

/**
 * When our source returns a table of information, this class is what is used to store the results.
 * It consists of {@link Column}s, {@link Row}s and {@link Cell}s.
 * Cells know about their parent column and row. Each Cell should know what x and y position it has within the table.
 *  
 * @author msaun
 *
 */
public class SourceProcessorResultTable implements PageItem<String>
{  
  private static final Logger logger = LoggerFactory.getLogger(SourceProcessorResultTable.class);
  
  @JsonIgnore
  private TemplateDto templateDto;
  
  /*
   * This class only knows about the list of rows it contains.
   * Each Row knows about it's Cells
   * Each Cell knows it's value, parent row and a parent Column
   */
  
  private String id;
  
  private Long pages;
  
  private Long totalCount; // probably different to rows.size() when we having paging enabled.
  
  @JsonManagedReference("tablerow")
  private List<Row> rows = new ArrayList<Row>();

  @JsonIgnore
  private List<Column> columns = new ArrayList<Column>();
  
  public SourceProcessorResultTable(String id)
  {
    setId(id);
  }

  public void setId(String id) {
    this.id = id;
  }
  
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
    return getBody().size();
  }

  public List<Row> getRows()
  {
    return rows;
  }
  
  public List<Column> getColumns()
  {
    return columns;
  }

  public void setRows(List<Row> rows)
  {
    this.rows = rows;
  }

  public void setColumns(List<Column> columns)
  {
    this.columns = columns;
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
    if(rows.size() > 0 && getRowByY(0) instanceof SourceProcessorResultTable.HeaderRow) {
      return getRowByY(0);
    }
    return null;
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
    private int sqlType;
    
    @JsonIgnore
    private List<Cell> cellsInColumn = new ArrayList<>();


    public Column(String name, int order)
    {
      this.name = name;
      this.order = order;
    }
    
    public Column(String name, int order, int sqlType)
    {
      this.name = name;
      this.order = order;
      this.sqlType = sqlType;
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
    
    public int getSqlType()
    {
      return sqlType;
    }
    
    public void setSqlType(int sqlType)
    {
      this.sqlType = sqlType;
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
    
    @Override
    public String toString() {
      return getName();
    }
    
  }
  
  public class HeaderRow extends SourceProcessorResultTable.Row {
    public HeaderRow() { }
    
    public HeaderRow(final List<Cell> cells) {
      super(cells);
    }
    
    @Override
    public boolean equals(Object obj)
    {
      if(obj instanceof HeaderRow) {
        return this.hashCode() == ( ((HeaderRow)obj).hashCode());
      }
      return false;
    }
    
    @Override
    public int hashCode()
    {
      return Objects.hashCode(cells);
    }
  }
  
  
  public class Row implements PageItem<String>, Iterable<Cell> {

    @JsonIgnore
    private TemplateDto templateDto;
    
    @JsonManagedReference("rowcell")
    protected List<Cell> cells = new ArrayList<>();

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
    public boolean hasChildPageItems()
    {
      return this.cells.size() > 0;
    }

    @JsonIgnore
    @Override
    public Collection<PageItem<String>> getChildPageItems()
    {
      return new ArrayList<PageItem<String>>(this.cells);
    }

    @Override
    public String getProcessedSource()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public TemplateDto getTemplateDto()
    {
      return templateDto;
    }

    public void setTemplateDto(final TemplateDto templateDto)
    {
      this.templateDto = templateDto;
      for(TemplateDto childTemplate : templateDto.getChildren()) {
        if(Cell.class.getCanonicalName().equals(childTemplate.getClassName())) {
          for(Cell c : getCells()) {
            c.setTemplateDto(childTemplate);
          }
        }
        // FIXME: FooterRow TODO
      }
    }

    @Override
    public Object getId()
    {
      logger.warn("getId() called on " + this.getClass().getCanonicalName() + " but we have no ID. Returning hashCode()");
      return hashCode();
    }
    
    @JsonIgnore
    @Override
    public Map<String, String> getCustomReplacementVariables()
    {
      return MapUtils.EMPTY_SORTED_MAP;
    }

    @Override
    public String getName()
    {
      logger.warn("getName() called on " + this.getClass().getCanonicalName() + " but we have no name. Returning null");
      return null;
    }

    @Override
    public ConditionalDto getConditional()
    {
      // TODO Auto-generated method stub
      return null;
    }
  }

  
  public class Cell implements PageItem<String> {
    @JsonIgnore
    private TemplateDto templateDto;
    
    @JsonBackReference("rowcell")
    private Row parentRow;
    
    @JsonIgnore
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
    public boolean hasChildPageItems()
    {
      return false;
    }

    @Override
    public String getProcessedSource()
    {
      return this.getValue();
    }

    @JsonIgnore
    @Override
    public Collection<PageItem<String>> getChildPageItems()
    {
      return new ArrayList<>(0);
    }

    @Override
    public TemplateDto getTemplateDto()
    {
      return templateDto;
    }

    public void setTemplateDto(TemplateDto templateDto)
    {
      this.templateDto = templateDto;
    }

    @Override
    public Object getId()
    {
      logger.warn("getId() called on " + this.getClass().getCanonicalName() + " but we have no specific ID. Return hashCode()");
      return hashCode();
    }
    
    @JsonIgnore
    @Override
    public Map<String, String> getCustomReplacementVariables()
    {
      return MapUtils.EMPTY_SORTED_MAP;
    }

    @Override
    public String getName()
    {
      logger.warn("getName() called on " + this.getClass().getCanonicalName() + " but we have no name. Returning null");
      return null;
    }

    @Override
    public ConditionalDto getConditional()
    {
      // TODO Auto-generated method stub
      return null;
    }
  }
  
  @Override
  public boolean hasChildPageItems()
  {
    return this.rows.size() > 0;
  }

  @Override
  @JsonIgnore
  public Collection<PageItem<String>> getChildPageItems()
  {
    return new ArrayList<PageItem<String>>(this.rows);
  }

  @Override
  public String getProcessedSource()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TemplateDto getTemplateDto()
  {
    return templateDto;
  }

  /**
   * Also sets sub-element templates for headerRow and Row
   * FIXME: Footer not implemented
   * @param templateDto
   */
  public void setTemplateDto(final TemplateDto templateDto)
  {
    this.templateDto = templateDto;
    for(TemplateDto childTemplate : templateDto.getChildren()) {
      if(Row.class.getCanonicalName().equals(childTemplate.getClassName())) {
        for(Row r : getRows()) {  // operates on HeaderRow and Row indiscriminately
          r.setTemplateDto(childTemplate); // this lets Cell items in HeaderRow share templates with rows if no unique header cell template is specified.
        }
      }
      if(HeaderRow.class.getCanonicalName().equals(childTemplate.getClassName())
              && getHeader() != null) {
        getHeader().setTemplateDto(childTemplate); // set header template after
      }
      // FIXME: FooterRow TODO
    }
  }

  @Override
  public Object getId()
  {
    if(id != null)
      return id;
    logger.warn("getId() called on " + this.getClass().getCanonicalName() + " but we have no specific ID. Return hashCode()");
    return hashCode();
  }

  public Long getTotalCount()
  {
    return totalCount;
  }

  public void setTotalCount(Long totalCount)
  {
    this.totalCount = totalCount;
    fillInMissingPageData();
  }
  
  private void fillInMissingPageData() {
    if(this.getTotalCount() != null && ( this.getCount() != this.getTotalCount()) ) {
      if(this.getPages() == null) {
        this.pages = (long) Math.ceil(this.getTotalCount().doubleValue() / (1.0 * this.getCount()) ) ;
      }
    }
  }

  @JsonIgnore
  @Override
  public Map<String, String> getCustomReplacementVariables()
  {
    return MapUtils.EMPTY_SORTED_MAP;
  }

  @Override
  public String getName()
  {
    logger.warn("getName() called on " + this.getClass().getCanonicalName() + " but we have no name. Returning null");
    return null;
  }

  @Override
  public ConditionalDto getConditional()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
