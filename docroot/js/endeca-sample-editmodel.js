Ext.ns('Endeca.Portlets.SamplePortlet');

/**
 * @class Endeca.Portlets.SamplePortlet.EditModel
 * 
 * Constructs an instance of the EditModel JavaScript object, combining the state from the server 
 * with the method behavior defined in this file.
 *
 * Will copy each field under the server's root editModel into this object.
 * 
 * IMPORTANT: Avoid name conflicts.  Ensure that the members in the server's JSON object
 * do not conflict with method names in this JavaScript file.
 * 
 * @author Endeca Technologies, Inc.
 * 
 */
Endeca.Portlets.SamplePortlet.EditModel = function(editModelData) {

	for(var member in editModelData) {
		if(editModelData.hasOwnProperty(member)) {
			/* it's a property - copy it */
			this[member] = editModelData[member];
		}
	}
	
	return this;
};
	
Endeca.Portlets.SamplePortlet.EditModel.prototype = {
	
};
