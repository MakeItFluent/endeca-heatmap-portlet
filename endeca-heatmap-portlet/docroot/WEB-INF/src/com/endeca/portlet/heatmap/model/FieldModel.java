package com.endeca.portlet.heatmap.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.endeca.portal.mdex.MDEXAttribute;

/**
 * Represents a field to be displayed in a grid.
 * 
 * Could be used to contain additional information, such as format
 * 
 * @author Endeca Technologies, Inc.
 *
 */
public class FieldModel {

	private String fieldName;
	//private boolean isDisplayed;
	private boolean isNumeric;
	private boolean isGeographic; //TODO: how to get geographic fields!?
	
	public FieldModel() {
		isNumeric=true;
		fieldName="";
	}
	
	public FieldModel(MDEXAttribute attr) {		
		this.fieldName = attr.getName();
		this.isNumeric = attr.isNumericProperty();
	}
	
	/**
	 * Gets the data name of the field/property
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * Whether or not the field should be displayed.
	 * @return
	 */
	public boolean isNumeric() {
		return isNumeric;
	}
	
	/**
	 * WAS cannot access isX() accessors - this is provided for it.
	 * However, this also confuses Jackson's Reflection, so we have to flag it for JsonIgnore
	 * @return
	 */
	@JsonIgnore
	public boolean getNumeric() { 
		return isNumeric();
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	

}
