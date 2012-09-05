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
package org.openmrs.module.solr;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

/**
 * Managers the solr server connection. It can be an embedded instance or an external one.
 */
public class SolrEngine {
	
	private static Log log = LogFactory.getLog(SolrEngine.class);
	
	private SolrServer solrServer;
	
	private SolrEngine() {
		
	}
	
	private static class SolrEngineHolder {
		
		private static SolrEngine INSTANCE = null;
	}
	
	private void init() throws Exception {
		String solrHome = Context.getAdministrationService().getGlobalProperty("solr.home",
		    new File(OpenmrsUtil.getApplicationDataDirectory(), "solr").getAbsolutePath());
		
		System.setProperty("solr.solr.home", solrHome);
		
		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		solrServer = new EmbeddedSolrServer(coreContainer, "");
	}
	
	/**
	 * Get the static/singular instance of the module class loader
	 * 
	 * @return OpenmrsClassLoader
	 */
	public static SolrEngine getInstance() throws Exception {
		if (SolrEngineHolder.INSTANCE == null) {
			SolrEngineHolder.INSTANCE = new SolrEngine();
			SolrEngineHolder.INSTANCE.init();
		}
		
		return SolrEngineHolder.INSTANCE;
	}
	
	/**
	 * Gets an instance of the solr server
	 * 
	 * @return SolrServer
	 */
	public SolrServer getServer() {
		return solrServer;
	}
}
