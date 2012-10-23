<%--
/**
 * view.jsp
 *
 * Server-side view template for the "view" mode, with the following responsibilities:
 *
 * 1.) ensure that javascript dependencies are imported
 * 2.) instantiate the Portlet JavaScript Object
 * 3.) pass data from the server to the Portlet JavaScript Object, including:
 *     - the portlet id (via <portlet:namespace/>)
 *     - localized string resources (via <edisc:initJSLanguageUtils/>)
 *     - resource URLs
 *     - the view model
 * 4.) provide two divs for ExtJS code to render - one for the root panel, one for error messages
 * 5.) invoke .initializeView() on the Portlet JavaScript Object - transferring control to JavaScript
 *
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%-- TODO REVIEW remove temptation of c/fmt/fn? --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://endeca.com/discovery" prefix="edisc"%>
<portlet:defineObjects /><%-- establish the renderRequest, renderResponse, and portletConfig for use in the JSP --%>

<%-- We can't assume that liferay-display.xml imported our JavaScript successfully - call import-javascript-dependencies as a backup --%>
<%@ include file="import-javascript-dependencies.jspf" %>

<%-- prepare divs - 3 types of messaging divs, and one for rendering of the view panel --%>
<div id="<portlet:namespace />Error" class="portlet-msg-error" style="display:none"></div>
<div id="<portlet:namespace />Warning" class="portlet-msg-alert" style="display:none"></div>
<div id="<portlet:namespace />Success" class="portlet-msg-success" style="display:none"></div>
<div id="<portlet:namespace />ViewPanel" class="endecaSamplePortletView" >
	<%-- map is created in this div --%>
	<div id="<portlet:namespace />map-canvas" style="height:${viewModel.editModel.portletHeight}px;"></div>
</div>

<script type="text/javascript">

	<%-- instantiate our portlet javascript object --%>
	var e<portlet:namespace/>SamplePortletJs = new Endeca.Portlets.SamplePortlet.Controller('<portlet:namespace/>');
	
	<%-- load the resource strings and urls --%>  
	e<portlet:namespace/>SamplePortletJs.resources = <edisc:initJSLanguageUtils/>;
	e<portlet:namespace/>SamplePortletJs.resourceUrls = {
			recordDataStore: '<portlet:resourceURL escapeXml="false" id="recordDataStore"/>'
	};

	<%-- load the viewModel: passing it from JSP to JS, adding behavior via JavaScript constructor --%>
	<c:if test="${not empty viewModel}">
		e<portlet:namespace/>SamplePortletJs.viewModel = new Endeca.Portlets.SamplePortlet.ViewModel(${viewModel});
	</c:if>
	
	<%-- tell Ext to call initializeView() when the portlet is ready --%>
	Ext.onReady(function() {
		<%-- Create the map object on the canvas div using function call in our custom JS file. Should be decoupled with an event --%>	
		createMap("<portlet:namespace />map-canvas", e<portlet:namespace/>SamplePortletJs);
		
		e<portlet:namespace/>SamplePortletJs.initializeView();
	});
</script>


