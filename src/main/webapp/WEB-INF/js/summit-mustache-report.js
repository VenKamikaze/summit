var summit = summit || {};

summit.report = summit.report || {
  contextPath: null,
  apiPath: null,

  initialise : function(contextPath, apiPath) {
    this.contextPath = contextPath;
    this.apiPath = apiPath;
  },

  /**
   * Find the 0-based column index that has the column name (value) of 'id'
   * This lets us link returned rows in a report to other pages based on this 'id' value
   * Params : data - the JSON table data returned via a server API call for this report region
   */
  getIdColumnIndex : function(data) {
    var columnIndexForId = null;
    $.each(data.header.cells, function(index, cell) {
      if("id" === cell.value) {
        columnIndexForId = index;
      }
    });
    return columnIndexForId;
  },

  /**
   * Use the 'id' column to make each row of data clickable on a table
   * and link it to a view page via the URL specified in the 'select' function
   * Params: idIdx - the index of the 'id' column so we can retrieve the unique ID to link (does not support composite keys)
   *         data - the JSON table data returned via a server API call for this report region
   *         elementContainingTable - the parent element (usually a div) that sits above the table we are manipulating.
   */
  linkRowsToViewPage : function(idIdx, data, elementContainingTable) {
    var mustacheTableElement = elementContainingTable.find("table");
    if(mustacheTableElement[0] !== undefined) {
      var trElements = mustacheTableElement.find("tr"); /* ignore first element as it is the header */
      $.each(data.body, function(index, row) {
        trElements[index+1].onclick = function() {
          select(row.cells[idIdx].value);
        };
      });
    }
  },

  /**
   * The select function is triggered via the onclick event on each <tr> of your table data
   * It should redirect to the appropriate view page for this particular report row.
   */
  select : function(id) {
    // FIXME : this would have to go to a particular view page, usually unique per report data.
    parent.window.location.href = this.contextPath + "/view/" + id;
  },

  /**
   * Performs the actual AJAX request to the server,
   * on success, will call processJsonResponse which handles the DOM manipulation with the result
   */
  doRequest : function(formData, regionId) {
    var data = $.ajax({ url: searchUrl,
                      data: formData,
                      cache: false,
                      success: function(response) {
                          processJsonResponse(response, regionId);
                        }
                      });
  },

  /**
   * Processes the JSON response of tabular data for the region identified by the regionId.
   * Params : data - the JSON table data returned via a server API call for this report region
   *          regionId - the ID of the region that links to this tabular data (used for DOM manipulation).
   */
  processJsonResponse : function(data, regionId) {
    buildHtmlTable(data, regionId);
    var idIdx = getIdColumnIndex(data);
    if(idIdx != null) {
      linkRowsToViewPage(idIdx, data, $("#mustacheReportRegion-" + regionId));
    }
  },

  /**
   * Performs the DOM manpulation using a template that we define, combines it with the JSON data
   *   and renders the result on the page.
   * Params : data - the JSON table data returned via a server API call for this report region
   *          regionId - the ID of the region that links to this tabular data (used for the DOM manipulation).
   */
  buildHtmlTable : function(data, regionId) {
    // FIXME : should allow the template to be retrieved via a server side call instead.
    var template = "<table id=\"mustache_table\" class=\"rounded-corner\"><thead>{{#header}}<tr class=\"tablerowheader\">{{#cells}}<th class=\"tablecell\">{{value}}</th>{{/cells}}</tr>{{/header}}</thead><tbody>{{#body}}<tr class=\"tablerowdata\">{{#cells}}<td class=\"tablecell\">{{value}}</td>{{/cells}}</tr>{{/body}}</tbody></table></div>";
    var html = Mustache.to_html(template, data);
    $("#mustacheReportRegion-" + regionId).html(html);
  }
}
