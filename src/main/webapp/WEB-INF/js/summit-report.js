var Summit = Summit || {};

/**
 * Contains helper methods for summit reports
 * Also contains ReportInstance "class" for holding report data.
 */
Summit.Report = Summit.Report || {
  reportInstances: [], /* The report instances on this page */

  /* Initialise function for Summit.Report */
  initialise : function(reportInstances) {
    this.reportInstances = reportInstances;
    try {
      console.log("Initialised Summit.Report");
    }
    catch (e) {
      /* IE8 has issues with console.log, do nothing */
    }
  },

  /*************** HELPER METHODS FOR Summit.Report BELOW ********************/

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
    parent.window.location.href = Summit.Page.contextPath + "/view/" + id;
  },

  /**
   * Performs the actual AJAX request to the server,
   * on success, will call processJsonResponse which handles the DOM manipulation with the result
   * Params : formData - serialised formData to send to the remote server.
   *        : reportInstance - an initialised Summit.Report.ReportInstance object, without JSON data (or with JSON data that can be replaced).
   */
  doRequest : function(reportInstance) {
    if(reportInstance != null && reportInstance.regionId != null) {
      var data = $.ajax({ url: reportInstance.filterPath,
                        data: $(reportInstance.formId).serialize(),
                        cache: false,
                        success: function(response) {
                            reportInstance.data = response;
                            processJsonResponse(response, reportInstance.regionId);
                          }
                        });
    }
  },

  createNewReportInstance : function(regionId, formId, template, viewPath, filterPath, getData) {
    var instance = Object.create(Summit.Report.ReportInstance);
    instance.initialise(regionId, formId, null, template, viewPath, filterPath);
    this.reportInstances.push(instance);
    if(getData) {
      doRequest(instance);
    }
    return instance;
  }

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
