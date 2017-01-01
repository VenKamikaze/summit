var Summit = Summit || {};

/**
 * Contains helper methods for all summit pages
 */
Summit.Page = Summit.Page || {
  contextPath: null,
  apiPath: null,

  /* Initialise function for Summit.Report */
  initialise : function(contextPath, apiPath) {
    this.contextPath = contextPath;
    this.apiPath = apiPath;
  }

}
