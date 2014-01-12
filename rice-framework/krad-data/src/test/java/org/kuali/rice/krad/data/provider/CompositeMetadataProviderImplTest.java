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
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.impl.CompositeMetadataProviderImpl;
import org.kuali.rice.krad.data.jpa.eclipselink.EclipseLinkJpaMetadataProviderImpl;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.provider.spring.SpringMetadataProviderImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class CompositeMetadataProviderImplTest {

	static EclipseLinkJpaMetadataProviderImpl metadataProvider;
	CompositeMetadataProviderImpl compositeProvider;
	SpringMetadataProviderImpl springProvider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		Logger.getLogger(CompositeMetadataProviderImpl.class).setLevel(Level.DEBUG);
		Logger.getLogger(SpringMetadataProviderImpl.class).setLevel(Level.DEBUG);
		metadataProvider = new EclipseLinkJpaMetadataProviderImpl();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("krad-data-unit-test");
		metadataProvider.setEntityManager(entityManagerFactory.createEntityManager());
	}

	@Before
	public void setUp() throws Exception {
		springProvider = new SpringMetadataProviderImpl();
		List<String> springFileLocations = new ArrayList<String>();
		springFileLocations.add("classpath:org/kuali/rice/krad/data/provider/spring/krad-metadata-parent-beans.xml");
		springFileLocations.add("classpath:org/kuali/rice/krad/data/provider/spring/*.xml");
		springProvider.setResourceLocations(springFileLocations);
		ArrayList<MetadataProvider> providers = new ArrayList<MetadataProvider>();
		providers.add(metadataProvider);
		providers.add(springProvider);
		compositeProvider = new CompositeMetadataProviderImpl();
		compositeProvider.setProviders(providers);
	}

	@Test
	public void testProvideMetadata() {
		Assert.assertNotNull("Metadata map should not be null", compositeProvider.provideMetadata());
		Assert.assertFalse("Metadata map should not have been empty", compositeProvider.provideMetadata().isEmpty());
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(TestDataObject.class);
		Assert.assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		Assert.assertEquals("Label not read properly from metadata provider", "A Spring-Provided Label",
				metadata.getLabel());
		Assert.assertEquals("backing object name not read properly from metadata provider", "ANOTHER_TABLE_NAME_T",
				metadata.getBackingObjectName());
		Assert.assertFalse("Attributes should still be present from the OJB metadata", metadata.getAttributes()
				.isEmpty());
		System.err.println(metadata);
	}

	@Test
	public void testMergedSpringAttribute() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(TestDataObject.class);
		Assert.assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		List<DataObjectAttribute> attributes = metadata.getAttributes();
		Assert.assertFalse("Attributes should still be present from the OJB metadata", attributes.isEmpty());
		boolean listContainsStringProperty = false;
		boolean listContainsNewProperty = false;
		for (DataObjectAttribute attr : attributes) {
			if (attr.getName().equals("stringProperty")) {
				listContainsStringProperty = true;
			}
			if (attr.getName().equals("nonPersistedProperty")) {
				listContainsNewProperty = true;
			}
		}
		System.err.println(attributes);
		Assert.assertTrue("stringProperty should still have been in the attribute list", listContainsStringProperty);
		Assert.assertTrue("nonPersistedProperty should have been added to the attribute list", listContainsNewProperty);

		Assert.assertNotNull("getAttribute(stringProperty) should not have returned null",
				metadata.getAttribute("stringProperty"));
		Assert.assertNotNull("getAttribute(nonPersistedProperty) should not have returned null",
				metadata.getAttribute("nonPersistedProperty"));
		Assert.assertNotNull("getAttribute(nonStandardDataType) should not have returned null",
				metadata.getAttribute("nonStandardDataType"));

		Assert.assertEquals("getAttribute(nonPersistedProperty) label incorrect", "Attribute Added via Spring",
				metadata.getAttribute("nonPersistedProperty").getLabel());
		Assert.assertEquals("getAttribute(nonStandardDataType) label incorrect", "Non Standard Label-Spring", metadata
				.getAttribute("nonStandardDataType").getLabel());

		// test another property of the attribute to see that not changed (proxied to the embedded metadata)
		Assert.assertEquals("getAttribute(nonStandardDataType) backing object incorrect", "NON_STANDARD", metadata
				.getAttribute("nonStandardDataType").getBackingObjectName());

	}

	@Test
	public void testMergedSpringAttribute_Remove() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(TestDataObject.class);
		Assert.assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		List<DataObjectAttribute> attributes = metadata.getAttributes();
		Assert.assertFalse("Attributes should still be present from the OJB metadata", attributes.isEmpty());

		boolean listContainsDateProperty = false;
		for (DataObjectAttribute attr : attributes) {
			if (attr.getName().equals("dateProperty")) {
				listContainsDateProperty = true;
			}
		}
		System.err.println(attributes);
		Assert.assertFalse("dateProperty should not have been in the attribute list", listContainsDateProperty);
		Assert.assertNull("getAttribute(dateProperty) should have returned null", metadata.getAttribute("dateProperty"));
	}
}
