package com.endeca.portlet.heatmap.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Representation of the "View Model" - the model that is composed during handleViewRenderRequest
 * based on saved preferences ("Edit Model") and other runtime factors
 * 
 * @author Endeca Technologies, Inc.
 *
 */
public class ViewModel {

	private List<FieldModel> fields;
	
	private EditModel editModel;
	
	/**
	 * Information describing the configuration of fields for display.
	 * @return
	 */
	public List<FieldModel> getFields() {
		return fields;
	}
	public void setFields(List<FieldModel> fields) {
		this.fields = fields;
	}
	
	public void setEditModel(EditModel editModel) {
		this.editModel = editModel;
	}
	
	public EditModel getEditModel()	{
		return this.editModel;
	}
	
	public String toString() {
		ObjectMapper jacksonMapper = new ObjectMapper();
		try {
			return jacksonMapper.writeValueAsString(this);
		} catch (IOException e) {
			return null;
		}
	}

}
