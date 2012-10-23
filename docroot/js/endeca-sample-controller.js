Ext.ns('Endeca.Portlets.SamplePortlet');

/**
 * @class Endeca.Portlets.SamplePortlet.Controller
 * 
 * Constructs an instance of the Portlet JavaScript Controller, providing the portletId.
 * 
 * The portletId is needed so that the JavaScript code can locate its HTML divs
 * 
 * @author Endeca Technologies, Inc.
 */
Endeca.Portlets.SamplePortlet.Controller = function(portletId) {
	this.portletId = portletId;
	return this;
};

Endeca.Portlets.SamplePortlet.Controller.prototype = {

	/**
	 * Initialize the View component hierarchy, and render it to the div with id=(portletId + 'View')
	 */
	initializeView : function() {
		var jsController = this;
		
		Ext.Ajax.request( {
			url : jsController.resourceUrls['recordDataStore'],
			success : function(response) {
				var json = Ext.decode(response.responseText);
				
				if(typeof json != 'undefined' && json != null && typeof json.rows != 'undefined' && json.rows != null) {
					data = json.rows;
					// update the map with the data - including a reference to the controller to get settings
					// should be decoupled with an event rather than a direct call
					createGraph(data, jsController);
				}
			}
		});
		
	},
	
	sel: function(idFragment) {
		return jQuery('#' + this.portletId + idFragment);
	},
	
	/**
	 * Initialize the Edit component hierarchy, and render it to the div with id=(portletId + 'Edit')
	 */
	initializeEdit : function() {
		var jsController = this,
			selectTemplate = '{{#fields}}<option value="{{fieldName}}">{{fieldName}}</option>{{/fields}}',
			sortedFields = _.sortBy(jsController.fieldList, function (d) {return d.fieldName; }),
			sortedNumericFields = _.filter(sortedFields, function(d) {return d.numeric;});
		
		console.log(jsController.editModel);
		
		this.sel('LocationDimensionSelect').append(Mustache.render(selectTemplate, {fields: sortedFields}))
			.val(jsController.editModel.locationField);
	
		this.sel('WeightDimensionSelect').append(Mustache.render(selectTemplate, {fields: sortedNumericFields}))
			.val(jsController.editModel.weightField);
		
		this.sel('PortletHeight').val(jsController.editModel.portletHeight);
		
		this.sel('UseWeightMetric').val(jsController.editModel.useWeightField.toString());
		
		this.sel('Save').text(jsController.resources.getMessage('edit-toolbar-save'))
			.click(function() {
				jsController.postEditModel();
			});
	},

	/**
	 * Post the editModel to the server
	 */
	postEditModel : function() {
		var jsController = this;

		/* make any changes to the edit model based on form fields */
		jsController.editModel.locationField = this.sel('LocationDimensionSelect').val();
		jsController.editModel.weightField = this.sel('WeightDimensionSelect').val();
		jsController.editModel.useWeightField = this.sel('UseWeightMetric').val() == 'true';
		jsController.editModel.portletHeight = this.sel('PortletHeight').val();
		
		/* post the new edit model to the server */
		Ext.Ajax.request( {
			url : jsController.resourceUrls['updateEditModel'],
			method : 'POST',
			jsonData : jsController.editModel,
			success : function(response) {
				jsController.displaySuccessMessage(jsController.resources.getMessage('success-settings-saved'));
			},
			failure : function(response) {
				if (Ext.isEmpty(response) || Ext.isEmpty(response.responseText)) {
					jsController.displayErrorMessage(jsController.resources.getMessage('error-server-unavailable'));
				} else {
					jsController.displayErrorMessage(response.responseText);
				}
			}
		});

	},

	/**
	 * Display a message in a success div (green), hiding all other messages
	 */
	displaySuccessMessage : function(message) {
		var jsController = this;
		Ext.fly(jsController.portletId + 'Success').update(message);
		Ext.fly(jsController.portletId + 'Success').setVisible(true, true);
		Ext.fly(jsController.portletId + 'Warning').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Error').setDisplayed(false);
	},

	/**
	 * Display a message in a warning div (yellow), hiding all other messages
	 */
	displayWarnMessage : function(message) {
		var jsController = this;
		Ext.fly(jsController.portletId + 'Success').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Warning').update(message);
		Ext.fly(jsController.portletId + 'Warning').setVisible(true, true);
		Ext.fly(jsController.portletId + 'Error').setDisplayed(false);
	},

	/**
	 * Display a message in an error div (red), hiding all other messages
	 */
	displayErrorMessage : function(message) {
		var jsController = this;
		Ext.fly(jsController.portletId + 'Success').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Warning').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Error').update(message);
		Ext.fly(jsController.portletId + 'Error').setVisible(true, true);
	},

	/**
	 * Hide all message divs
	 */
	hideMessages : function() {
		var jsController = this;
		Ext.fly(jsController.portletId + 'Success').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Warning').setDisplayed(false);
		Ext.fly(jsController.portletId + 'Error').setDisplayed(false);
	}

};
