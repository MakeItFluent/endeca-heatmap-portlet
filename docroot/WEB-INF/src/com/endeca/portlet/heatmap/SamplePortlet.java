package com.endeca.portlet.heatmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Element;

import com.endeca.mdex.conversation.Record;
import com.endeca.mdex.conversation.RecordList;
import com.endeca.mdex.conversation.Results;
import com.endeca.mdex.lql_parser.types.ExpressionBase;
import com.endeca.mdex.sconfig._2010.types.SemanticEntity;
import com.endeca.portal.data.DataSource;
import com.endeca.portal.data.DataSourceException;
import com.endeca.portal.data.QueryResults;
import com.endeca.portal.data.QueryState;
import com.endeca.portal.data.functions.DataSourceFilter;
import com.endeca.portal.data.functions.LQLFilter;
import com.endeca.portal.data.functions.NegativeRefinementFilter;
import com.endeca.portal.data.functions.QueryFunctionUnsupportedException;
import com.endeca.portal.data.functions.RecordFilter;
import com.endeca.portal.data.functions.RefinementFilter;
import com.endeca.portal.data.functions.ResultsConfig;
import com.endeca.portal.lql.LQLFilterCollection;
import com.endeca.portal.mdex.DiscoveryServiceUtil;
import com.endeca.portal.mdex.MDEXAttribute;
import com.endeca.portal.mdex.MDEXAttributeList;
import com.endeca.portlet.EndecaPortlet;
import com.endeca.portlet.ModelAndView;
import com.endeca.portlet.ProcessResource;
import com.endeca.portlet.heatmap.model.EditModel;
import com.endeca.portlet.heatmap.model.FieldModel;
import com.endeca.portlet.heatmap.model.FieldList;
import com.endeca.portlet.heatmap.model.ViewModel;
import com.endeca.portlet.util.EndecaPortletUtil;
import com.endeca.portlet.util.LanguageUtils;

/**
 * A portlet class designed to illustrate a basic Latitude Studio
 * component.
 *
 * @author Endeca Technologies, Inc.
 *
 */
public class SamplePortlet extends EndecaPortlet {

	public static final String RESOURCE_RECORDDATASTORE = "recordDataStore";
	public static final String RESOURCE_UPDATEEDITMODEL = "updateEditModel";
	public static final String PREFERENCE_EDITMODEL = "editModel";
	public static final String REQUEST_ATTRIBUTE_EDITMODEL = "editModel";
	public static final String REQUEST_ATTRIBUTE_FIELDLIST = "fieldList";
	public static final String REQUEST_ATTRIBUTE_VIEWMODEL = "viewModel";

	private static final Logger logger = Logger.getLogger( SamplePortlet.class );
 
	private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

	/* (non-Javadoc)
	 * Method override to handle view-mode runtime requests.
	 * @see com.endeca.portlet.EndecaPortlet#handleViewRenderRequest(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleViewRenderRequest(RenderRequest request,
			RenderResponse response) throws Exception {

		// EndecaPortlets use the ModelAndView object - similar to Spring MVC's - to pass information
		// to the JSP view layer
		ModelAndView mv = new ModelAndView(viewJSP);

		// load the edit model
		PortletPreferences prefs = request.getPreferences();
		EditModel editModel = jsonObjectMapper.readValue(prefs.getValue(PREFERENCE_EDITMODEL, "{}"), EditModel.class);

		// update the fields
		//updateFields(editModel, request);

		// use the edit model to construct the view model
		ViewModel viewModel = new ViewModel();
		viewModel.setEditModel(editModel);
		//viewModel.setFields(editModel.getFields());

		// store the view model as a request attribute to pass it to the JSP/JS
		request.setAttribute(REQUEST_ATTRIBUTE_VIEWMODEL, viewModel);

		return mv;
	}

	/* (non-Javadoc)
	 * Method override to handle view-mode runtime requests.
	 * @see com.endeca.portlet.EndecaPortlet#handleEditRenderRequest(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleEditRenderRequest(RenderRequest request,
			RenderResponse response) throws Exception {

		// EndecaPortlets use the ModelAndView object - similar to Spring MVC's - to pass information
		// to the JSP view layer
		ModelAndView mv = new ModelAndView(editJSP);

		// load the edit model
		PortletPreferences prefs = request.getPreferences();
		EditModel editModel = jsonObjectMapper.readValue(prefs.getValue(PREFERENCE_EDITMODEL, "{}"), EditModel.class);

		// update the fields
		//updateFields(editModel, request);

		// store the edit model as a request attribute to pass it to the JSP/JS
		request.setAttribute(REQUEST_ATTRIBUTE_EDITMODEL, editModel);
		DataSource DS = this.getDataSource(request);
		
		request.setAttribute(REQUEST_ATTRIBUTE_FIELDLIST, getFields(getDataSource(request)));

		return mv;

	}

	/**
	 * Resource handler to update the portlet preferences
	 *
	 * @param request the ResourceRequest
	 * @param response the ResourceResponse
	 * @throws PortletException
	 * @throws IOException
	 */
	@ProcessResource(resourceId = RESOURCE_UPDATEEDITMODEL)
	public void serveResourceUpdateEditModel(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		if ( !EndecaPortletUtil.hasUpdatePrivileges(request, getContainer()) )
			return;

		PortletPreferences prefs = request.getPreferences();
		String jsonStringFromClient = IOUtils.toString(request.getPortletInputStream());

		try {
			// parse the request into the edit model to ensure it is valid JSON
			EditModel editModel = jsonObjectMapper.readValue(jsonStringFromClient, EditModel.class);

			prefs.setValue(PREFERENCE_EDITMODEL, jsonObjectMapper.writeValueAsString(editModel));
			prefs.store();

		} catch (IOException e) {
			// IO exceptions related to JSON parsing will be caught.  IO exceptions related to writing response will be thrown
			logger.error(e);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
			response.getWriter().write(e.getMessage());
		}
	}

	/**
	 * Originally got data for an Ext data grid - changed to just get all data with an LQL query.
	 *
	 * @param request the ResourceRequest
	 * @param response the ResourceResponse
	 * @throws PortletException
	 * @throws IOException
	 */
	@ProcessResource(resourceId = RESOURCE_RECORDDATASTORE)
	public void serveResourceRecordDataStore(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		
		// originally this call is used for a paging datasource - just set the start offset to zero and the resultsPerPage to a big number 
		long offset = 0;
		long numResultsPerPage = 20000;

		try {
			offset = Long.parseLong(request.getParameter("start"));
			numResultsPerPage = Long.parseLong(request.getParameter("limit"));
		} catch (NumberFormatException nfe) {
			/* Ext does this for the initial request; swallow the exception, and use the defaults */
		}

		// get the edit model that contains the names of the fields
		PortletPreferences prefs = request.getPreferences();
		EditModel editModel = jsonObjectMapper.readValue(prefs.getValue(PREFERENCE_EDITMODEL, "{}"), EditModel.class);

		// create an LQL query which gets values for the location field set in the edit mode 
		// - and, optionally the weight field (with null checks) 
		String locationFieldName = editModel.getLocationField();
		String[] fieldNames;
		String notNullLQL = "";
		if (editModel.getUseWeightField()) {
			fieldNames = new String[] {locationFieldName, editModel.getWeightField()};
			notNullLQL = locationFieldName + " IS NOT NULL AND " + editModel.getWeightField() + " IS NOT NULL";
		}
		else {
			fieldNames = new String[] {locationFieldName};
			notNullLQL = locationFieldName + " IS NOT NULL";
		}
		
		
		List<Map<String,Object>> dataSourceRows = new ArrayList<Map<String,Object>>();
		long recordCount = 0l;

		try {
			// get the current datasource and its associated query state
			DataSource dataSource = getDataSource(request);
			QueryState q = dataSource.getQueryState();

			// define the pieces of information we want to get back.
			ResultsConfig resultsConfig = new ResultsConfig();
			resultsConfig.setRecordsPerPage(numResultsPerPage);
			resultsConfig.setOffset(offset);
			
			resultsConfig.setColumns(fieldNames);
			q.addFunction(resultsConfig);
			
			// add a simple datasource filter using the LQL string defined above
			ExpressionBase longLatNotNullExpression = dataSource.parseLQLExpression(notNullLQL);
			DataSourceFilter longLatFilter = new DataSourceFilter(longLatNotNullExpression);
			q.addFunction(longLatFilter);
			
			// Execute the query
			QueryResults queryResults = dataSource.execute(q);

			Results csResults = queryResults.getDiscoveryServiceResults();

			if ( csResults != null ) {
				RecordList csRecords = DiscoveryServiceUtil.getRecordList(csResults);
				if ( csRecords != null ) {
					recordCount = csRecords.getNumRecords();

					// since this is a DataStore request, we need to return 500 so JavaScript can display an error
					if(recordCount > editModel.getResultCountThreshold().longValue()) {
						response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
						Object[] messageArgs = {new Long(recordCount), editModel.getResultCountThreshold()};
						response.getWriter().write(LanguageUtils.getMessage(request, "error-record-count-above-threshold", messageArgs));
						return;
					}

					for ( com.endeca.mdex.conversation.RecordListEntry  recordListEntry : csRecords.getRecordListEntry() ) {
						Record r = recordListEntry.getRecord();
						Map<String,Object> dataSourceRow = new HashMap<String,Object>();

						for (Element element : r.getAny()) {
							dataSourceRow.put(element.getNodeName(), element.getTextContent());
						}

						dataSourceRows.add(dataSourceRow);
					}
				}
			}

			response.setContentType(com.endeca.portlet.util.Constants.MIME_TYPE_JSON + "; charset=UTF-8");
			response.setProperty("Cache-Control", "no-cache");

			Map<String,Object> dataStoreResponse = new HashMap<String,Object>();
			dataStoreResponse.put("rows", dataSourceRows);
			dataStoreResponse.put("total", new Long(recordCount));

			jsonObjectMapper.writeValue(response.getWriter(), dataStoreResponse);

		} catch (Exception e) {
			// exceptions related to data source or JSON parsing will be caught.  IO exceptions related to writing response will be thrown
			logger.error(e);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
			response.getWriter().write(e.getMessage());

		}
	}

	/**
	 * Retrieves the set of fields in the MDEX
	 * @return
	 */
	private FieldList getFields(DataSource dataSource) throws DataSourceException {
		
		FieldList fields = new FieldList();
		
		MDEXAttributeList attrs = dataSource.getMDEXAttributes();
		
		for(MDEXAttribute a : attrs) {
			fields.add(new FieldModel(a));
		}
		
		return fields;
	}
	
}
