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
package org.openmrs.module.solr.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.openmrs.module.solr.SolrEngine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Chart Search controller.
 */
@Controller
public class ChartSearchController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/solr/chartSearch", method = RequestMethod.POST)
	public String chartSearch(@RequestParam(value = "patientId", required = true) Integer patientId,
	                          HttpServletRequest request) throws Exception {
		
		String searchText = request.getParameter("searchText") + " AND person_id:" + patientId;
		
		SolrQuery query = new SolrQuery(searchText);
		QueryResponse response = SolrEngine.getInstance().getServer().query(query);
		SolrDocumentList documents = response.getResults();
		System.out.println(documents.getNumFound());
		for (SolrDocument document : documents) {
			Integer obsId = (Integer) document.get("obs_id");
			Integer person_id = (Integer) document.get("person_id");
			String conceptName = (String) document.get("concept_name");
			Date date = (Date) document.get("obs_datetime");
			String text = (String) document.get("value_text");
			//load this obs from the database using its id
		}
		
		return null;
	}
}
