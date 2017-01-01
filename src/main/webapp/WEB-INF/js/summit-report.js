var Summit = Summit || {};

/**
 * Contains helper methods for summit reports
 * Also contains ReportInstance "class" for holding report data.
 */
Summit.Report = Summit.Report || {
  reportInstances: [], /* The report instances on this page */

  /* Initialise function for Summit.Report */
  initialise : function(reportInstances) {
    if(reportInstances != null) {
      this.reportInstances = reportInstances;
    }
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
  linkRowsToViewPage : function(reportInstance, elementContainingTable) {
    var mustacheTableElement = elementContainingTable.find("table");
    if(mustacheTableElement[0] !== undefined && reportInstance.viewPath != null) {
      var trElements = mustacheTableElement.find("tr"); /* ignore first element as it is the header */
      var i = 0;
      for(i = 0; i < this.reportInstances.length; i++) {
        if (this.reportInstances[i] === reportInstance) {
          if(typeof(console.log) !== "undefined") { console.log("Found report instance index: " + i); }
          break;
        }
      }
      $.each(reportInstance.data.body, function(index, row) {
        trElements[index+1].onclick = function() {
          Summit.Report.reportInstances[i].select(row.cells[reportInstance.columnIndexForId].value);
        };
      });
    }
  },



  /**
   * Performs the actual AJAX request to the server,
   * on success, will call processJsonResponse which handles the DOM manipulation with the result
   * Params : formData - serialised formData to send to the remote server.
   *        : reportInstance - an initialised Summit.Report.ReportInstance object, without JSON data (or with JSON data that can be replaced).
   */
  doRequest : function(reportInstance) {
    if(reportInstance != null && reportInstance.isInitialised() ) {
      var formElement = "";
      if(reportInstance.formId.charAt(0) != '#') {
        formElement = "#";
      }
      formElement += reportInstance.formId;
      var that = this;
      var data = $.ajax({ url: reportInstance.filterPath,
                        data: $(formElement).serialize(),
                        cache: false,
                        success: function(response) {
                            reportInstance.setData(response);
                            that.processJsonResponse(reportInstance);
                          }
                        });
    }
  },

  createNewReportInstance : function(regionId, formId, template, viewPath, filterPath, getData) {
    var instance = Object.create(Summit.Report.ReportInstance);
    instance.initialise(regionId, formId, null, template, viewPath, filterPath);
    this.reportInstances.push(instance);
    if(getData) {
      this.doRequest(instance);
    }
    return instance;
  },

  /**
   * Processes the JSON response of tabular data for the region identified by the regionId.
   * Params : reportInstance - the initialised report instance including JSON data.
   */
  processJsonResponse : function(reportInstance) {
    this.buildHtmlTable(reportInstance);
    if(reportInstance.columnIndexForId != null) {
      this.linkRowsToViewPage(reportInstance, $("#mustacheReportRegion-" + reportInstance.regionId));
    }
  },

  /**
   * Performs the DOM manpulation using a template that we define, combines it with the JSON data
   *   and renders the result on the page.
   * Params : reportInstance - the initialised report instance including JSON data.
   */
  buildHtmlTable : function(reportInstance) {
    // TODO : should make it easy for the template to be retrieved via a server side AJAX call
    var template = reportInstance.template != null ? reportInstance.template
                                                   : "<table id=\"mustache_table\" class=\"rounded-corner\"><thead>{{#header}}<tr class=\"tablerowheader\">{{#cells}}<th class=\"tablecell\">{{value}}</th>{{/cells}}</tr>{{/header}}</thead><tbody>{{#body}}<tr class=\"tablerowdata\">{{#cells}}<td class=\"tablecell\">{{value}}</td>{{/cells}}</tr>{{/body}}</tbody></table></div>";
    var html = Mustache.to_html(template, reportInstance.data);
    $("#mustacheReportRegion-" + reportInstance.regionId).html(html);
  }
}
