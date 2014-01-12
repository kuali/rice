/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.provider;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.provider.spring.SpringMetadataProviderImpl;

import java.util.ArrayList;
import java.util.List;

public class SpringMetadataProviderImplTest {

	SpringMetadataProviderImpl provider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getLogger(SpringMetadataProviderImpl.class).setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		provider = new SpringMetadataProviderImpl();
	}

	@Test
	public void testInitializeMetadata() {
		List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add("classpath:org/kuali/rice/krad/data/provider/spring/krad-metadata-parent-beans.xml");
		springFileLocations.add("classpath:org/kuali/rice/krad/data/provider/spring/*");
		provider.setResourceLocations(springFileLocations);
		provider.initializeMetadata(null);
		Assert.assertNotNull("Metadata map should not be null", provider.provideMetadata());
		Assert.assertFalse("Metadata map should not have been empty", provider.provideMetadata().isEmpty());
		DataObjectMetadata metadata = provider.provideMetadata().get(TestDataObject.class);
		Assert.assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		Assert.assertEquals("Label not read properly from metadata provider", "A Spring-Provided Label",
				metadata.getLabel());
		Assert.assertEquals("backing object name not read properly from metadata provider", "ANOTHER_TABLE_NAME_T",
				metadata.getBackingObjectName());
	}

}
