var Summit = Summit || {};
Summit.Form = Summit.Form || {};

Summit.Form.FormInstance = Summit.Form.FormInstance || {
  regionId    : null,
  formId      : null,

  template    : null,

  initialised : false,

  /* Initialise function for Summit.Form.FormInstance */
  initialise : function(regionId, formId, template) {
    this.regionId = regionId; /* The region ID (not DOM related) of the region this is associated with */
    this.formId = formId; /* The DOM element ID of the form that this form is associated with */

    this.template = template; /* The Mustache HTML template, if applicable */

    this.initialised = true;
  },

  isInitialised: function() {
    return this.initialised;
  },

}
