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
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class SolrActivator implements Activator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see Activator#startup()
	 */
	public void startup() {
		log.info("Starting Solr Module");
		
		try {
			//Get the solr home folder
			String solrHome = Context.getAdministrationService().getGlobalProperty("solr.home",
			    new File(OpenmrsUtil.getApplicationDataDirectory(), "solr").getAbsolutePath());
			
			//If user has not setup solr config folder, set them a default one
			if (!new File(solrHome + File.separatorChar + "conf").exists()) {
				URL url = OpenmrsClassLoader.getInstance().getResource("conf");
				File file = new File(url.getFile());
				FileUtils.copyDirectoryToDirectory(file, new File(solrHome));
			}
		}
		catch (Exception ex) {
			log.error("Failed to copy Solr config folder", ex);
		}
	}
	
	/**
	 * @see Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Solr Module");
	}
	
}
