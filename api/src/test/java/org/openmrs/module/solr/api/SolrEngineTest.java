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
package org.openmrs.module.solr.api;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.solr.SolrEngine;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("org.apache.log4j.*")
public class SolrEngineTest {
	
	@Test
	public void testInit() throws Exception {
		
		AdministrationService administrationService = Mockito.mock(AdministrationService.class);
		Mockito.when(
		    administrationService.getGlobalProperty("solr.home",
		        new File(OpenmrsUtil.getApplicationDataDirectory(), "solr").getAbsolutePath())).thenReturn(
		    new File("").getAbsolutePath());
		
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getAdministrationService()).thenReturn(administrationService);
		
		SolrEngine engine = SolrEngine.getInstance();
		Assert.assertNotNull(engine);
	}
}
