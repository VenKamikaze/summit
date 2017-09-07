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

  /*************** HELPER METHODS FOR Summit.Form BELOW ********************/

  createNewFormInstance : function(regionId, formId, template) {
    var instance = Object.create(Summit.Form.FormInstance);
    instance.initialise(regionId, formId, template);
    this.formInstances.push(instance);
    return instance;
  },

  // TODO: client side validation functions should probably go here.
}
