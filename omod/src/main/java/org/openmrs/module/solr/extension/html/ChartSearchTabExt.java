package org.openmrs.module.solr.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.PatientDashboardTabExt;


/**
 * Adds the Chart Search tab to the patient dashboard.
 *
 */
public class ChartSearchTabExt  extends PatientDashboardTabExt{

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTabName() {
		return "solr.patientDashboard.chartSearch";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return "Patient Dashboard - View Chart Search Section";
	}
	
	@Override
	public String getTabId() {
		return "patientChartSearch";
	}
	
	@Override
	public String getPortletUrl() {
		return "patientChartSearch";
	}
}
