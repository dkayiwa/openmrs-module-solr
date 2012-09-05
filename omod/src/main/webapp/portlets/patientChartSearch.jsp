<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:hasPrivilege privilege="Patient Dashboard - View Chart Search Section">

	<div id="chartSearch">
		<form id="chartSearchForm" method="post" action="<%= request.getContextPath() %>/module/solr/chartSearch.form?patientId=${patient.patientId}">			
			<input type="text" name="searchText" value=""/>
			<input type="submit" value="Search"/>
		</form>
	</div>
	
</openmrs:hasPrivilege>
	
		