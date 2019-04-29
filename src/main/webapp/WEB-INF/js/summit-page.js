var Summit = Summit || {};

/**
 * Contains helper methods for all summit pages
 */
Summit.Page = Summit.Page || {
  contextPath: null,
  apiPath: null,
  PAGE_PARAMETER_KEY: "pageParams",

  /* Initialise function for Summit.Report */
  initialise : function(contextPath, apiPath) {
    this.contextPath = contextPath;
    this.apiPath = apiPath;
  },

  /* Thanks to Sameer Kazi on Stackoverflow: https://stackoverflow.com/questions/19491336/get-url-parameter-jquery-or-how-to-get-query-string-values-in-js */
  getUrlParameter : function(key) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
      sParameterName = sURLVariables[i].split('=');

      if (sParameterName[0] === key) {
        return sParameterName[1] === undefined ? true : sParameterName[1];
      }
    }

  },

  submit : function(el) {
    var form = (el != null && el.length > 0) ? el.form : $();
    if (form) {
      form.submit();
      return true;
    }
    return false;
  }

}
