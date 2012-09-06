<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRChartSearchService.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<script type="text/javascript">
	var lastSearch;
	$j(document).ready(function() {
		new OpenmrsSearch("findConcept", true, doConceptSearch, doSelectionHandler, 
				[{fieldName:"name", header:" "}, {fieldName:"preferredName", header:" "}],
				{searchLabel: '<spring:message code="Concept.search" javaScriptEscape="true"/>',
                    searchPlaceholder:'<spring:message code="Concept.search.placeholder" javaScriptEscape="true"/>',
					includeVoidedLabel: '<spring:message code="SearchResults.includeRetired" javaScriptEscape="true"/>', 
					columnRenderers: [nameColumnRenderer, null], 
					columnVisibility: [true, false],
					searchPhrase:'<request:parameter name="phrase"/>',
					showIncludeVerbose: true,
					verboseHandler: doGetVerbose
				});
	});
	
	function doSelectionHandler(index, data) {
		document.location = "admin/observations/obs.form?obsId=" + data.conceptId;
	}
	
	//searchHandler
	function doConceptSearch(text, resultHandler, getMatchCount, opts) {
		DWRChartSearchService.findCountAndConcepts(${model.patient.patientId}, text, opts.includeVoided, null, null, null, null, opts.start, opts.length, getMatchCount, resultHandler);
	}
	
	//custom render, appends an arrow and preferredName it exists
	function nameColumnRenderer(oObj){
		if(oObj.aData[1] && $j.trim(oObj.aData[1]) != '')
			return "<span>"+oObj.aData[0]+"</span><span class='otherHit'> &rArr; "+oObj.aData[1]+"</span>";
		
		return "<span>"+oObj.aData[0]+"</span>";
	}
	
	//generates and returns the verbose text
	function doGetVerbose(index, data){
		if(!data)
			return "";
		return "#"+data.conceptId+": "+data.description;
	}
</script>

<openmrs:hasPrivilege privilege="Patient Dashboard - View Chart Search Section">

<div>
	<b class="boxHeader"><spring:message code="Concept.find"/></b>
	<div class="box">
		<div class="searchWidgetContainer" id="findConcept"></div>
	</div>
</div>
	
</openmrs:hasPrivilege>
	
		