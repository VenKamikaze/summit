var Summit = Summit || {};
Summit.Report = Summit.Report || {};

Summit.Report.ReportInstance = Summit.Report.ReportInstance || {
  regionId    : null,
  formId      : null,

  data        : { },
  template    : null,

  viewPath    : null,
  filterPath  : null,

  initialised : false,
  columnIndexForId : null,

  /* Initialise function for Summit.Report.ReportInstance */
  initialise : function(regionId, formId, data, template, viewPath, filterPath) {
    this.regionId = regionId; /* The region ID (not DOM related) of the region this is associated with */
    this.formId = formId; /* The DOM element ID of the form that this report is associated with */

    this.data = data; /* The JSON data, usually won't be set in the initialise function, but is set later via direct variable access */
    this.template = template; /* The Mustache HTML template */

    this.viewPath = viewPath; /* The URL for the view page this report is associated with (for viewing records this report is displaying) */
    this.filterPath = filterPath; /* The URL for the filter API path this report is associated with (for searching/filtering) */

    this.initialised = true;
    if(this.data != null) {
      this.columnIndexForId = Summit.Report.getIdColumnIndex(this.data);
    }
  },

  hasData: function() {
    return ! ($.isEmptyObject(this.data));
  },

  isInitialised: function() {
    return this.initialised;
  },

  setData: function(data) {
    this.data = data;
    if(this.data != null) {
      this.columnIndexForId = Summit.Report.getIdColumnIndex(this.data);
    }
  },

  /**
   * The select function is triggered via the onclick event on each <tr> of your table data
   * It should redirect to the appropriate view page for this particular report row.
   */
  select : function(id) {
    parent.window.location.href = this.viewPath + id;
  }
}
