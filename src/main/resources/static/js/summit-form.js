var Summit = Summit || {};

/**
 * Contains helper methods for summit forms
 * Also contains references to formInstances that exist on this page.
 */
Summit.Form = Summit.Form || {
  formInstances: [], /* The report instances on this page */

  /* Initialise function for Summit.Form */
  initialise : function(formInstances) {
    if(formInstances != null) {
      this.formInstances = formInstances;
    }
    try {
      console.log("Initialised Summit.Form");
    }
    catch (e) {
      /* IE8 has issues with console.log, do nothing */
    }
  },

  /*************** Element builders for Summit.Form below ******************/
  /**
  * Performs the DOM manipulation using a template that we define, combines it with the JSON data
  *   and returns the object
  * Params : elementJson - the JSON representation of the element we want to use as our source data
  *        : template    - the mustache template we want to populate from our elementJson
  */
  // FIXME TODO: get rid of the default template that is used for testing.
  buildElement : function(elementJson, template) {
    // TODO : should make it easy for the template to be retrieved via a server side AJAX call
    template == null ? "<select id=\"{{id}}\" name=\"{{name}}\">{{#options}}<option selected=\"{{selected}}\" value=\"{{value}}\">{{name}}</option>{{/options}}</select>" : null;
    var html = Mustache.to_html(template, elementJson.data);
    return html;
    //$("#mustacheReportRegion-" + reportInstance.regionId).html(html);
  },


  /*************** HELPER METHODS FOR Summit.Form BELOW ********************/

  createNewFormInstance : function(regionId, formId, template) {
    var instance = Object.create(Summit.Form.FormInstance);
    instance.initialise(regionId, formId, template);
    this.formInstances.push(instance);
    return instance;
  },

  // TODO: client side validation functions should probably go here.
}
