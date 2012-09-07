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
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
			
			//Tell solr that this is our home folder
			System.setProperty("solr.solr.home", solrHome);
			
			//If user has not setup solr config folder, set a default one
			String configFolder = solrHome + File.separatorChar + "conf";
			if (!new File(configFolder).exists()) {
				URL url = OpenmrsClassLoader.getInstance().getResource("conf");
				File file = new File(url.getFile());
				FileUtils.copyDirectoryToDirectory(file, new File(solrHome));
				
				setDataImportConnectionInfo(configFolder);
			}
		}
		catch (Exception ex) {
			log.error("Failed to copy Solr config folder", ex);
		}
	}
	
	private void setDataImportConnectionInfo(String configFolder) throws Exception {
		Properties properties = Context.getRuntimeProperties();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(OpenmrsClassLoader.getInstance().getResourceAsStream("conf/data-config.xml"));
		Element node = (Element) doc.getElementsByTagName("dataSource").item(0);
		
		node.setAttribute("url", properties.getProperty("connection.url"));
		node.setAttribute("user", properties.getProperty("connection.username"));
		node.setAttribute("password", properties.getProperty("connection.password"));
		
		String xml = doc2String(doc);
		File file = new File(configFolder + File.separatorChar + "data-config.xml");
		FileUtils.writeStringToFile(file, xml, "UTF-8");
	}
	
	public static String doc2String(Node doc) throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		StringWriter outStream = new StringWriter();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outStream);
		transformer.transform(source, result);
		return outStream.getBuffer().toString();
	}
	
	/**
	 * @see Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Solr Module");
	}
	
}
