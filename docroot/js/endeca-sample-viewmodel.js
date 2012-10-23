Ext.ns('Endeca.Portlets.SamplePortlet');

/**
 * @class Endeca.Portlets.SamplePortlet.ViewModel
 * 
 * Constructs an instance of the ViewModel JavaScript object, combining the state from the server 
 * with the method behavior defined in this file.
 *
 * Will copy each field under the server's root viewModel into this object.
 * 
 * IMPORTANT: Avoid name conflicts.  Ensure that the members in the server's JSON object
 * do not conflict with method names in this JavaScript file.
 * 
 * @author Endeca Technologies, Inc.
 * 
 */
Endeca.Portlets.SamplePortlet.ViewModel = function(viewModelData) {

	for(var member in viewModelData) {
		if(viewModelData.hasOwnProperty(member)) {
			/* it's a property - copy it */
			this[member] = viewModelData[member];
		}
	}
	
	return this;
};

Endeca.Portlets.SamplePortlet.ViewModel.prototype = {

	/**
	 * Convert an array of complex field objects { fieldName:'foo', displayed:true } to an array of fieldName
	 * strings
	 */
	getFieldsAsStringArray : function() {
		if (Ext.isEmpty(this.fields)) {
			return [];
		}
		var arrFields = [];
		for ( var i = 0; i < this.fields.length; i++) {
			var field = this.fields[i];
			if (field.displayed) {
				arrFields.push(field.fieldName);
			}
		}
		return arrFields;
	},
	
	/**
	 * Convert an array of complex field objects { fieldName:'foo', displayed:true } to an array of
	 * Ext.grid.Column
	 */
	getFieldsAsExtGridColumnArray : function() {
		if (Ext.isEmpty(this.fields)) {
			return [];
		}
		var arrColumns = [];
		for ( var i = 0; i < this.fields.length; i++) {
			var field = this.fields[i];
			if (field.displayed) {
				arrColumns.push( {
					id : field.fieldName,
					header : field.fieldName,
					sortable : false,
					menuDisabled: true,
					dataIndex : field.fieldName
				});
			}
		}
		return arrColumns;
	}

};
