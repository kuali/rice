/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.provider.annotation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.impl.CompositeMetadataProviderImpl;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkJpaMetadataProviderImpl;
import org.kuali.rice.krad.data.provider.jpa.testbo.ReferencedDataObject;
import org.kuali.rice.krad.data.provider.jpa.testbo.SomeOtherCollection;
import org.kuali.rice.krad.data.provider.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.provider.jpa.testbo.TestDataObjectExtension;

public class AnnotationMetadataProviderImplTest {

	static EclipseLinkJpaMetadataProviderImpl jpaMetadataProvider;
	CompositeMetadataProviderImpl compositeProvider;
	AnnotationMetadataProviderImpl annotationMetadataProvider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		Logger.getLogger(CompositeMetadataProviderImpl.class).setLevel(Level.DEBUG);
		Logger.getLogger(AnnotationMetadataProviderImpl.class).setLevel(Level.DEBUG);
		Logger.getLogger(EclipseLinkJpaMetadataProviderImpl.class).setLevel(Level.DEBUG);
		jpaMetadataProvider = new EclipseLinkJpaMetadataProviderImpl();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("krad-data-unit-test");
		jpaMetadataProvider.setEntityManager(entityManagerFactory.createEntityManager());
	}

	@Before
	public void setUp() throws Exception {
		annotationMetadataProvider = new AnnotationMetadataProviderImpl();
		annotationMetadataProvider.setJpaMetadataProvider(jpaMetadataProvider);
		ArrayList<MetadataProvider> providers = new ArrayList<MetadataProvider>();
		providers.add(jpaMetadataProvider);
		providers.add(annotationMetadataProvider);
		compositeProvider = new CompositeMetadataProviderImpl();
		compositeProvider.setProviders(providers);
	}

	@Test
	public void testInitializeMetadataNoTypesProvided() {
		AnnotationMetadataProviderImpl provider = new AnnotationMetadataProviderImpl();
        provider.initializeMetadata(null);
        assertTrue(provider.initializationAttempted);
		assertTrue(provider.getSupportedTypes().isEmpty());
        assertFalse(provider.handles(TestDataObject.class));
	}

	@Test
	public void testProvideMetadata() {
		assertNotNull("Metadata map should not be null", compositeProvider.provideMetadata());
		assertFalse("Metadata map should not have been empty", compositeProvider.provideMetadata().isEmpty());
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		assertEquals("Label not read properly from annotation metadata provider", "Label From Annotation",
				metadata.getLabel());
		assertFalse("Attributes should still be present from the JPA metadata", metadata.getAttributes()
				.isEmpty());
		System.err.println(metadata);
	}

	@Test
	public void testBusinessKeyMetadata() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertEquals("With no definition, the PK list and business key list should have been equal",
				metadata.getPrimaryKeyAttributeNames(), metadata.getBusinessKeyAttributeNames());

		metadata = compositeProvider.provideMetadata().get(ReferencedDataObject.class);
		assertNotEquals("When @BusinessKey used, the PK list and business key list should not have been equal",
				metadata.getPrimaryKeyAttributeNames(), metadata.getBusinessKeyAttributeNames());

		assertEquals(
				"When a business key is defined, the primary display attribute should be the last field on that",
				"someOtherStringProperty", metadata.getPrimaryDisplayAttributeName());
	}

	@Test
	public void testMergedAttribute() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		List<DataObjectAttribute> attributes = metadata.getAttributes();
		assertFalse("Attributes should still be present from the JPA metadata", attributes.isEmpty());

		assertNotNull("getAttribute(stringProperty) should not have returned null",
				metadata.getAttribute("stringProperty"));

		assertEquals("getAttribute(nonPersistedProperty) label incorrect", "Attribute Label From Annotation",
				metadata.getAttribute("stringProperty").getLabel());
	}

	@Test
	public void testNonPersistableProperty() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		System.err.println(metadata);
		DataObjectAttribute attr = metadata.getAttribute("keyAndString");
		assertNotNull("keyAndString property does not exist", attr);
		assertEquals("keyAndString label incorrect", "Test Data Object", attr.getLabel());
	}

	// @Test
	// public void testPersonRelationship() {
	// DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
	// TestDataObject.class);
	// assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
	//
	// DataObjectRelationship relationship = metadata.getRelationship("fiscalOfficer");
	// assertNotNull("getRelationship(fiscalOfficer) should not have returned null", relationship);
	//
	// assertEquals("Incorrect related data object type", "org.kuali.rice.kim.api.identity.Person",
	// relationship.getRelatedObjectType().getClass().getName());
	// System.err.println(relationship);
	// assertEquals("attributes list on relationship has incorrect length", 1, relationship
	// .getAttributeRelationships().size());
	// assertEquals("parent attribute name incorrect", "foId", relationship.getAttributeRelationships().get(0)
	// .getParentAttributeName());
	// assertEquals("child attribute name incorrect", "principalId", relationship.getAttributeRelationships()
	// .get(0).getChildAttributeName());
	// }

	@Test
	public void testExtensionAttribute_metadataRelationship() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		DataObjectMetadata extensionMetadata = compositeProvider.provideMetadata().get(TestDataObjectExtension.class);

		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		assertNotNull("Metadata should have been retrieved for TestDataObjectExtension", extensionMetadata);

		DataObjectRelationship relationship = metadata.getRelationship("extension");
		assertNotNull("A relationship should have been defined for the extension attribute", relationship);
		System.err.println("Extension Relationship: " + relationship);

		assertTrue("should be loaded with parent", relationship.isLoadedAtParentLoadTime());
		assertFalse("should NOT be proxied", relationship.isLoadedDynamicallyUponUse());
		assertFalse("should NOT be read-only", relationship.isReadOnly());
		assertTrue("should be saved with parent", relationship.isSavedWithParent());
		assertTrue("should be deleted with parent", relationship.isDeletedWithParent());

		assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		assertEquals("attribute relationships size incorrect", 1, relationship.getAttributeRelationships()
				.size());
		DataObjectAttributeRelationship linkingAttribute = relationship.getAttributeRelationships().get(0);
		assertEquals("first parent attribute name mismatch", "primaryKeyProperty",
				linkingAttribute.getParentAttributeName());
		assertEquals("first child attribute name mismatch", "primaryKeyProperty",
				linkingAttribute.getChildAttributeName());
	}

	@Test
	public void testInheritedProperties() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		String propName = "referencedObject.someOtherStringProperty";
		DataObjectAttribute attr = metadata.getAttribute(propName);
		assertNotNull("getAttribute(" + propName + ") should not have returned null", attr);
		assertTrue("Attribute should have isInherited", attr.isInherited());
		assertEquals("Inherited data object type not set", ReferencedDataObject.class,
 attr.getInheritedFromType());
		assertEquals("Inherited data object attribute not set", "someOtherStringProperty",
				attr.getInheritedFromAttributeName());
		assertEquals("Label incorrect", "RDOs Business Key", attr.getLabel());
	}

	@Test
	public void testInheritedProperties_labelOverride() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		assertNotNull(
				"getAttribute(anotherReferencedObject.someOtherStringProperty) should not have returned null",
				metadata.getAttribute("anotherReferencedObject.someOtherStringProperty"));
		assertEquals("Label incorrect", "Overridden Inherited Property Label",
				metadata.getAttribute("anotherReferencedObject.someOtherStringProperty").getLabel());
	}

	@Test
	public void testSecurityAnnotation() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		assertNotNull("getAttribute(readOnlyProperty) should not have returned null",
				metadata.getAttribute("readOnlyProperty"));
		assertNotNull("missing AttributeSecurity", metadata.getAttribute("readOnlyProperty")
				.getAttributeSecurity());
		assertEquals("readonly flag not set", true, metadata.getAttribute("readOnlyProperty")
				.getAttributeSecurity().isReadOnly() );
	}

	@Test
	public void testCollectionAnnotation_derivedType() {
		DataObjectMetadata metadata = compositeProvider.provideMetadata().get(
				TestDataObject.class);
		assertNotNull("Metadata should have been retrieved for TestDataObject", metadata);
		DataObjectCollection collection = metadata.getCollection("someOtherCollection");
		assertNotNull("someOtherCollection not defined", collection);
		System.err.println(collection);
		assertEquals("element class is incorrect", SomeOtherCollection.class, collection.getRelatedType());
		assertEquals("collection label incorrect", "Some Other Collection", collection.getLabel());
		assertEquals("collection item label incorrect", "Some Other Collection", collection.getElementLabel());
		assertNotNull("attribute relationships must not be null", collection.getAttributeRelationships());
		assertEquals("Wrong number of relationship columns", 1, collection.getAttributeRelationships().size());
		assertEquals("Parent attribute name incorrect", "dateProperty", collection.getAttributeRelationships().get(0)
				.getParentAttributeName());
		assertEquals("Child attribute name incorrect", "collectionDateProperty", collection.getAttributeRelationships()
				.get(0).getChildAttributeName());
		// assertEquals( "Sort order incorrect when unspecified", SortDirection.ASCENDING,
		// collection.getDefaultCollectionOrdering().get(0).getSortDirection() );
	}
}
