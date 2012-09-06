/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.solr.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.solr.SolrEngine;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.dwr.ConceptListItem;


public class DWRChartSearchService {

	protected static final Log log = LogFactory.getLog(DWRChartSearchService.class);
	
	/**
	 * Returns a map of results with the values as count of matches and a partial list of the
	 * matching concepts (depending on values of start and length parameters) while the keys are are
	 * 'count' and 'objectList' respectively, if the length parameter is not specified, then all
	 * matches will be returned from the start index if specified.
	 * 
	 * @param phrase concept name or conceptId
	 * @param includeRetired boolean if false, will exclude retired concepts
	 * @param includeClassNames List of ConceptClasses to restrict to
	 * @param excludeClassNames List of ConceptClasses to leave out of results
	 * @param includeDatatypeNames List of ConceptDatatypes to restrict to
	 * @param excludeDatatypeNames List of ConceptDatatypes to leave out of results
	 * @param start the beginning index
	 * @param length the number of matching concepts to return
	 * @param getMatchCount Specifies if the count of matches should be included in the returned map
	 * @return a map of results
	 * @throws APIException
	 * @since 1.8
	 */
	public Map<String, Object> findCountAndConcepts(Integer patientId, String phrase, boolean includeRetired, List<String> includeClassNames,
	        List<String> excludeClassNames, List<String> includeDatatypeNames, List<String> excludeDatatypeNames,
	        Integer start, Integer length, boolean getMatchCount) throws APIException {
		//Map to return
		Map<String, Object> resultsMap = new HashMap<String, Object>();
		Vector<Object> objectList = new Vector<Object>();	
		
		try {
			if (!StringUtils.isBlank(phrase)) {
				
				int matchCount = 0;
				if (getMatchCount) {
					//get the count of matches
					matchCount += getDocumentList(patientId, phrase).getNumFound();
				}
				
				//if we have any matches or this isn't the first ajax call when the caller
				//requests for the count
				if (matchCount > 0 || !getMatchCount) {
					objectList.addAll(findBatchOfConcepts(patientId, phrase, includeRetired, includeClassNames, excludeClassNames,
					    includeDatatypeNames, excludeDatatypeNames, start, length));
				}
				
				resultsMap.put("count", matchCount);
				resultsMap.put("objectList", objectList);
			} else {
				resultsMap.put("count", 0);
				objectList.add(Context.getMessageSourceService().getMessage("searchWidget.noMatchesFound"));
			}
			
		}
		catch (Exception e) {
			log.error("Error while searching for concepts", e);
			objectList.clear();
			objectList.add(Context.getMessageSourceService().getMessage("Concept.search.error") + " - " + e.getMessage());
			resultsMap.put("count", 0);
			resultsMap.put("objectList", objectList);
		}
		
		return resultsMap;
	}
	
	/**
	 * Gets a list of conceptListItems matching the given arguments
	 * 
	 * @param phrase the concept name string to match against
	 * @param includeRetired boolean if false, will exclude retired concepts
	 * @param includeClassNames List of ConceptClasses to restrict to
	 * @param excludeClassNames List of ConceptClasses to leave out of results
	 * @param includeDatatypeNames List of ConceptDatatypes to restrict to
	 * @param excludeDatatypeNames List of ConceptDatatypes to leave out of results
	 * @param start the beginning index
	 * @param length the number of matching concepts to return
	 * @return a list of conceptListItems matching the given arguments
	 * @should return concept by given id if exclude and include lists are empty
	 * @should return concept by given id if classname is included
	 * @should not return concept by given id if classname is not included
	 * @should not return concept by given id if classname is excluded
	 * @should return concept by given id if datatype is included
	 * @should not return concept by given id if datatype is not included
	 * @should not return concept by given id if datatype is excluded
	 * @should include
	 * @since 1.8
	 */
	public List<Object> findBatchOfConcepts(Integer patientId, String phrase, boolean includeRetired, List<String> includeClassNames,
	        List<String> excludeClassNames, List<String> includeDatatypeNames, List<String> excludeDatatypeNames,
	        Integer start, Integer length) {
		//TODO factor out the reusable code in this and findCountAndConcepts methods to a single utility method
		// List to return
		// Object type gives ability to return error strings
		Vector<Object> objectList = new Vector<Object>();
		
		// TODO add localization for messages
		
		Locale defaultLocale = Context.getLocale();
		
		// get the list of locales to search on
		List<Locale> searchLocales = Context.getAdministrationService().getAllowedLocales(); //getSearchLocales();
		
		try {
			//List<ConceptSearchResult> searchResults = new Vector<ConceptSearchResult>();
			List<Object> searchResults = new Vector<Object>();
			
			if (!StringUtils.isBlank(phrase)) {				
				// perform the search
				//searchResults.addAll(getDocumentList(patientId, phrase));
				SolrDocumentList documents = getDocumentList(patientId, phrase);
				for (SolrDocument document : documents) {
					Integer obsId = (Integer) document.get("obs_id");
					Integer person_id = (Integer) document.get("person_id");
					String conceptName = (String) document.get("concept_name");
					Date date = (Date) document.get("obs_datetime");
					String text = (String) document.get("value_text");
					String conceptDescription = (String) document.get("concept_description");
					//load this obs from the database using its id
					
					if(StringUtils.isBlank(conceptName))
						conceptName = (String) document.get("value_coded_concept_name");
					
					if(StringUtils.isBlank(conceptDescription))
						conceptDescription = (String) document.get("value_coded_concept_description");
					
					ConceptListItem conceptListItem = new ConceptListItem();
					conceptListItem.setConceptId(obsId);
					conceptListItem.setName(conceptName + " - " + Context.getDateFormat().format(date));
					conceptListItem.setDescription(conceptDescription);
					objectList.add(conceptListItem);
				}
			}
			
			if (objectList.size() < 1) {
				objectList.add(Context.getMessageSourceService().getMessage("general.noMatchesFoundInLocale",
				    new Object[] { "<b>" + phrase + "</b>", OpenmrsUtil.join(searchLocales, ", ") }, Context.getLocale()));
			} else {
				// turn searchResults into concept list items
				// if user wants drug concepts included, append those
				//for (ConceptSearchResult searchResult : searchResults)
				//	objectList.add(new ConceptListItem(searchResult));
			}
		}
		catch (Exception e) {
			log.error("Error while finding concepts + " + e.getMessage(), e);
			objectList.add(Context.getMessageSourceService().getMessage("Concept.search.error") + " - " + e.getMessage());
		}
		
		if (objectList.size() == 0)
			objectList.add(Context.getMessageSourceService().getMessage("general.noMatchesFoundInLocale",
			    new Object[] { "<b>" + phrase + "</b>", defaultLocale }, Context.getLocale()));
		
		return objectList;
	}
	
	private SolrDocumentList getDocumentList(Integer patientId, String searchText) throws Exception {
		SolrQuery query = new SolrQuery(searchText + " AND person_id:" + patientId);
		QueryResponse response = SolrEngine.getInstance().getServer().query(query);
		return response.getResults();
	}
}
