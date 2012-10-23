package com.endeca.portlet.heatmap.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Representation of the "Edit Model" - the data model used to drive the edit screen
 * and save all preferences.
 *
 * @author Endeca Technologies, Inc.
 *
 */
public class EditModel {

	private Long resultCountThreshold;
	private List<FieldModel> fields;
	private String locationField;
	private String weightField;
	private boolean useWeightField;
	private int portletHeight;
	private static final int DEFAULT_PORTLET_HEIGHT = 400;
	private static final long DEFAULT_NUM_RECORDS = 100000;


	/**
	 * A configurable threshold of maximum allowed results returned by the
	 * query for the grid to be displayed.
	 *
	 * If no value has been specified by the user, uses a value of 100000
	 *
	 * @return
	 */
	public Long getResultCountThreshold() {
		if(resultCountThreshold == null) {
			return new Long(DEFAULT_NUM_RECORDS);
		} else {
			return resultCountThreshold;
		}
	}
	public void setResultCountThreshold(Long resultCountThreshold) {
		this.resultCountThreshold = resultCountThreshold;
	}

	public List<FieldModel> getFields() {
		return fields;
	}

	public void setFields(List<FieldModel> fields) {
		this.fields = fields;
	}

	public String toString() {
		ObjectMapper jacksonMapper = new ObjectMapper();
		try {
			return jacksonMapper.writeValueAsString(this);
		} catch (IOException e) {
			return null;
		}
	}
	
	// getters+setters
	public String getLocationField() {
		return locationField;
	}
	public void  setLocationField(String locationField) {
		this.locationField = locationField;
	}
	public String getWeightField() {
		return weightField;
	}
	public void setWeightField(String weightField) {
		this.weightField = weightField;
	}
	public boolean getUseWeightField() {
		return useWeightField;
	}
	public void setUseWeightField(boolean useWeightField) {
		this.useWeightField = useWeightField;
	}
	public int getPortletHeight() {
		if(this.portletHeight == 0)
			return DEFAULT_PORTLET_HEIGHT;
		else
			return portletHeight;
	}
	public void setPortletHeight(int portletHeight) {
		
		this.portletHeight = portletHeight;
	}
	
}
