package com.endeca.portlet.heatmap.model;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

public class FieldList extends ArrayList<FieldModel> {
	
	public String toString() {
		ObjectMapper jacksonMapper = new ObjectMapper();
		try {
			return jacksonMapper.writeValueAsString(this);
		} catch (IOException e) {
			return null;
		}
	}
}