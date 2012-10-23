<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://endeca.com/discovery" prefix="edisc"%>
<portlet:defineObjects /><%-- establish the renderRequest, renderResponse, and portletConfig for use in the JSP --%>

<%-- We can't assume that liferay-display.xml imported our JavaScript successfully - call import-javascript-dependencies as a backup --%>
<%@ include file="import-javascript-dependencies.jspf" %>

<%-- prepare divs - 3 types of messaging divs, and one for rendering of the edit panel --%>
<div id="<portlet:namespace />Error" class="portlet-msg-error" style="display:none"></div>
<div id="<portlet:namespace />Warning" class="portlet-msg-alert" style="display:none"></div>
<div id="<portlet:namespace />Success" class="portlet-msg-success" style="display:none"></div>
<div id="<portlet:namespace />EditPanel" class="endecaSamplePortletEdit">
	<div>
		Location metric:
		<select id="<portlet:namespace />LocationDimensionSelect"></select>
	</div>
	<div>
		Weight metric: 
		<select id="<portlet:namespace />WeightDimensionSelect"></select>
		<select id="<portlet:namespace />UseWeightMetric">
			<option value="true">Use weight metric</option>
			<option value="false">Ignore weight metric</option>
		</select>
	</div>
	<div>
		Portlet height:
		<input type="text" id="<portlet:namespace />PortletHeight"></input>
	</div>
	<div>
		<button id="<portlet:namespace />Save">Save</button>
	</div>
</div>

<script type="text/javascript">

	<%-- instantiate the javascript object  --%>
	var e<portlet:namespace/>SamplePortletJs = new Endeca.Portlets.SamplePortlet.Controller('<portlet:namespace/>');

	<%-- load the resource strings and urls --%>  
	e<portlet:namespace/>SamplePortletJs.resources = <edisc:initJSLanguageUtils/>;
	e<portlet:namespace/>SamplePortletJs.resourceUrls = {
			updateEditModel: '<portlet:resourceURL escapeXml="false" id="updateEditModel"/>'
	};

	<%-- load the editModel: passing state from JSP to JS, adding behavior via JavaScript constructor --%>
	<c:if test="${not empty editModel}">
		e<portlet:namespace/>SamplePortletJs.editModel = new Endeca.Portlets.SamplePortlet.EditModel(${editModel});
		e<portlet:namespace/>SamplePortletJs.fieldList = ${fieldList};
	</c:if>

	<%-- tell Ext to call initializeEdit() when the portlet is ready --%>
	Ext.onReady(function() {
		e<portlet:namespace/>SamplePortletJs.initializeEdit();
	});
</script>


