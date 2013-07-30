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
package org.kuali.rice.krad.data.jpa;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.impl.security.DataObjectAttributeMaskFormatterLiteral;
import org.kuali.rice.krad.data.jpa.eclipselink.EclipseLinkJpaMetadataProviderImpl;
import org.kuali.rice.krad.data.jpa.testbo.CollectionDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectTwoPkFields;
import org.kuali.rice.krad.data.jpa.testbo.TestNonPersistableObject;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;

public class JpaMetadataProviderTest {
	static EclipseLinkJpaMetadataProviderImpl metadataProvider;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		metadataProvider = new EclipseLinkJpaMetadataProviderImpl();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("krad-data-unit-test");
		metadataProvider.setEntityManager(entityManagerFactory.createEntityManager());
	}

	@Before
	public void setUp() throws Exception {
		// metadataProvider = new JpaMetadataProviderImpl();
	}

	@Test
	public void testGetMetadataForType() throws Exception {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		Assert.assertNotNull("Returned metadata should not be null", metadata);
		metadata = metadataProvider.getMetadataForType(Class.forName("org.kuali.rice.krad.data.jpa.testbo.TestDataObject"));
		Assert.assertNotNull("Returned metadata should not be null", metadata);
	}

	@Test
	public void testGetAllMetadata() throws Exception {
		Map<Class<?>, DataObjectMetadata> metadata = metadataProvider.provideMetadata();
		Assert.assertNotNull("Returned metadata should not be null", metadata);
		Assert.assertFalse("metadata map should not have been empty", metadata.isEmpty());
		Assert.assertTrue("Should have had an entry for TestDataObject",
				metadata.containsKey(TestDataObject.class));
		Assert.assertTrue("Should have had an entry for TestDataObject (when class name specified)", metadata
.containsKey(Class.forName("org.kuali.rice.krad.data.jpa.testbo.TestDataObject")));
	}

	@Test
	public void testGetMetadataForClass_ClassData() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		System.err.println(metadata);
		Assert.assertEquals("Incorrect Data Object Type", TestDataObject.class,
				metadata.getType());
		Assert.assertEquals("Incorrect Type Label", "Test Data Object", metadata.getLabel());
		Assert.assertEquals("Table name not set as the backing object name", "KRTST_TEST_TABLE_T",
				metadata.getBackingObjectName());
	}

	@Test
	public void testGetMetadataForClass_VersionAttribute() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		Assert.assertEquals("Incorrect Version Setting on TestDataObject", Boolean.FALSE,
				metadata.isSupportsOptimisticLocking());

		metadata = metadataProvider.getMetadataForType(TestDataObjectTwoPkFields.class);
		Assert.assertEquals("Incorrect Version Setting on TestDataObjectTwoPkFields", Boolean.TRUE,
				metadata.isSupportsOptimisticLocking());
	}

	@Test
	public void testGetMetadataForClass_Attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		List<DataObjectAttribute> attributes = metadata.getAttributes();
		System.err.println(attributes);
		Assert.assertNotNull("Returned attributes should not have been null", attributes);
		Assert.assertFalse("Returned attributes should not have been empty", attributes.isEmpty());
		DataObjectAttribute firstAttribute = metadata.getAttribute("primaryKeyProperty");
		Assert.assertEquals("property name incorrect", "primaryKeyProperty", firstAttribute.getName());
		Assert.assertEquals("Property label incorrect", "Primary Key Property", firstAttribute.getLabel());
		Assert.assertEquals("Column name not set as the backing object name", "PK_PROP",
				firstAttribute.getBackingObjectName());
	}

	@Test
	public void testGetMetadataForClass_Attribute_Types() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("stringProperty");

		Assert.assertEquals("property DataType incorrect", DataType.STRING, attribute.getDataType());

		attribute = metadata.getAttribute("dateProperty");

		Assert.assertEquals("property DataType incorrect", DataType.DATE, attribute.getDataType());

		attribute = metadata.getAttribute("currencyProperty");

		Assert.assertEquals("property DataType incorrect", DataType.CURRENCY, attribute.getDataType());
	}

	@Test
	public void testGetMetadataForClass_Attribute_Type_Unknown() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("nonStandardDataType");

		Assert.assertEquals("nonStandardDataType property DataType incorrect", DataType.STRING, attribute.getDataType());
	}

	@Test
	public void testGetMetadataForClass_Attribute_Non_OJB_Property() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("nonPersistedProperty");

		Assert.assertNull("nonPersistedProperty should not exist", attribute);
	}

	@Test
	public void testGetMetadataForCollection() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		List<DataObjectCollection> collections = metadata.getCollections();
		System.err.println(collections);
		Assert.assertNotNull("Collections object should not be null", collections);
		Assert.assertEquals("Collections size incorrect", 3, collections.size());
		DataObjectCollection collection = collections.get(0);
		Assert.assertEquals("property name incorrect", "collectionProperty", collection.getName());
		Assert.assertEquals("collection backing object incorrect", "KRTST_TEST_COLL_T",
				collection.getBackingObjectName());
		Assert.assertEquals("collection label incorrect", "Collection Property", collection.getLabel());
		Assert.assertEquals("collection item label incorrect", "Collection Data Object", collection.getElementLabel());
		
		collection = metadata.getCollection("collectionPropertyTwo");
		Assert.assertNotNull("Collection object for collectionPropertyTwo should not be null", collection);
		Assert.assertEquals("property name incorrect", "collectionPropertyTwo", collection.getName());

		Assert.assertNotNull("attribute relationships must not be null", collection.getAttributeRelationships());
		Assert.assertEquals("attribute relationships size incorrect", 1, collection.getAttributeRelationships().size());
		DataObjectAttributeRelationship relationship = collection.getAttributeRelationships().get(0);
		Assert.assertEquals("parent attribute name mismatch", "primaryKeyProperty",
				relationship.getParentAttributeName());
		Assert.assertEquals("child attribute name mismatch", "primaryKeyPropertyUsingDifferentName",
				relationship.getChildAttributeName());

		Assert.assertNotNull("collection default sort list must not be null", collection.getDefaultOrdering());
		Assert.assertEquals("collection default sort size incorrect", 1, collection.getDefaultOrdering()
				.size());
	}

	@Test
	public void testGetMetadataForCollection_Indirect() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectCollection collection = metadata.getCollection("indirectCollection");
		Assert.assertNotNull("Collection object for indirectCollection should not be null", collection);
		Assert.assertTrue("Should be labeled as indirect", collection.isIndirectCollection());
		Assert.assertTrue("attribute relationship list should be empty: collection.getAttributeRelationships()",
				collection.getAttributeRelationships()
				.isEmpty());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		List<DataObjectRelationship> relationships = metadata.getRelationships();
		System.err.println(relationships);
		Assert.assertNotNull("Relationships object should not be null", relationships);
		Assert.assertEquals("Relationships size incorrect", 3, relationships.size());
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);
		Assert.assertEquals("property name incorrect", "referencedObject", relationship.getName());
		Assert.assertEquals("collection backing object incorrect", "KRTST_TEST_REF_OBJ_T",
				relationship.getBackingObjectName());
		Assert.assertEquals("collection label incorrect", "Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		Assert.assertTrue("relationship should have been proxied", relationship.isLoadedDynamicallyUponUse());
		Assert.assertFalse("loaded with parent should be false", relationship.isLoadedAtParentLoadTime());
		Assert.assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		Assert.assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		Assert.assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		Assert.assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		Assert.assertEquals("attribute relationships size incorrect", 1, relationship.getAttributeRelationships()
				.size());
		DataObjectAttributeRelationship linkingAttributes = relationship.getAttributeRelationships().get(0);
		Assert.assertEquals("parent attribute name mismatch", "stringProperty",
				linkingAttributes.getParentAttributeName());
		Assert.assertEquals("child attribute name mismatch", "stringProperty",
				linkingAttributes.getChildAttributeName());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertEquals("property name incorrect", "anotherReferencedObject", relationship.getName());
		Assert.assertEquals("collection backing object incorrect", "KRTST_TEST_ANOTHER_REF_OBJ_T",
				relationship.getBackingObjectName());
		Assert.assertEquals("collection label incorrect", "Another Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertFalse("relationship should not have been proxied", relationship.isLoadedDynamicallyUponUse());
		Assert.assertTrue("loaded with parent should be true", relationship.isLoadedAtParentLoadTime());
		Assert.assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		Assert.assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		Assert.assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		Assert.assertEquals("attribute relationships size incorrect", 2, relationship.getAttributeRelationships()
				.size());
		DataObjectAttributeRelationship linkingAttribute = relationship.getAttributeRelationships().get(0);
		Assert.assertEquals("first parent attribute name mismatch", "stringProperty",
				linkingAttribute.getParentAttributeName());
		Assert.assertEquals("first child attribute name mismatch", "stringProperty",
				linkingAttribute.getChildAttributeName());

		linkingAttribute = relationship.getAttributeRelationships().get(1);
		Assert.assertEquals("second parent attribute name mismatch", "dateProperty",
				linkingAttribute.getParentAttributeName());
		Assert.assertEquals("second child attribute name mismatch", "dateProperty",
				linkingAttribute.getChildAttributeName());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertEquals("property name incorrect", "anotherReferencedObject", relationship.getName());
		Assert.assertEquals("collection backing object incorrect", "KRTST_TEST_ANOTHER_REF_OBJ_T",
				relationship.getBackingObjectName());
		Assert.assertEquals("collection label incorrect", "Another Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertFalse("relationship should not have been proxied", relationship.isLoadedDynamicallyUponUse());
		Assert.assertTrue("loaded with parent should be true", relationship.isLoadedAtParentLoadTime());
		Assert.assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		Assert.assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		Assert.assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		Assert.assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		Assert.assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		Assert.assertEquals("attribute relationships size incorrect", 2, relationship.getAttributeRelationships()
				.size());
		DataObjectAttributeRelationship linkingAttribute = relationship.getAttributeRelationships().get(0);
		Assert.assertEquals("first parent attribute name mismatch", "stringProperty",
				linkingAttribute.getParentAttributeName());
		Assert.assertEquals("first child attribute name mismatch", "stringProperty",
				linkingAttribute.getChildAttributeName());

		linkingAttribute = relationship.getAttributeRelationships().get(1);
		Assert.assertEquals("second parent attribute name mismatch", "dateProperty",
				linkingAttribute.getParentAttributeName());
		Assert.assertEquals("second child attribute name mismatch", "dateProperty",
				linkingAttribute.getChildAttributeName());
	}

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_stringProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata.getRelationshipByLastAttributeInRelationship("stringProperty");
		Assert.assertNotNull("retrieval by last attribute name (stringProperty) failed", relationship);
		Assert.assertEquals("retrieval by last attribute name (stringProperty) returned wrong relationship",
				"referencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_dateProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata.getRelationshipByLastAttributeInRelationship("dateProperty");
		Assert.assertNotNull("retrieval by last attribute name (dateProperty) failed", relationship);
		Assert.assertEquals("retrieval by last attribute name (dateProperty) returned wrong relationship",
				"anotherReferencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_primaryKeyProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata
				.getRelationshipByLastAttributeInRelationship("primaryKeyProperty");
		Assert.assertNotNull("retrieval by last attribute name (primaryKeyProperty) failed", relationship);
		Assert.assertEquals("retrieval by last attribute name (primaryKeyProperty) returned wrong relationship",
				"yetAnotherReferencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byInvolvedAttribute() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		List<DataObjectRelationship> rels = metadata.getRelationshipsInvolvingAttribute("stringProperty");
		Assert.assertNotNull("retrieval by attribute name (stringProperty) failed", rels);
		Assert.assertEquals("retrieval by attribute name (stringProperty): wrong number returned", 2, rels.size());

		rels = metadata.getRelationshipsInvolvingAttribute("dateProperty");
		Assert.assertNotNull("retrieval by attribute name (dateProperty) failed", rels);
		Assert.assertEquals("retrieval by attribute name (dateProperty): wrong number returned", 1, rels.size());
	}

	@Test
	public void testGetMetadataForClass_PKFields() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		Assert.assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		Assert.assertEquals("PK field list length incorrect", 1, metadata.getPrimaryKeyAttributeNames().size());
		Assert.assertEquals("PK field wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		Assert.assertEquals("Primary Display Field Wrong", "primaryKeyProperty",
				metadata.getPrimaryDisplayAttributeName());
	}

	@Test
	public void testGetMetadataForClass_PKFields_TwoFieldIdClass() {
		DataObjectMetadata metadata = metadataProvider.getMetadataForType(TestDataObjectTwoPkFields.class);
		Assert.assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		System.err.println("PK Properties: " + metadata.getPrimaryKeyAttributeNames());
		Assert.assertEquals("PK field list length incorrect", 2, metadata.getPrimaryKeyAttributeNames().size());
		Assert.assertEquals("PK field 1 wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		Assert.assertEquals("PK field 2 wrong", "primaryKeyPropertyTwo", metadata.getPrimaryKeyAttributeNames().get(1));
		Assert.assertEquals("Primary Display Field Wrong", "primaryKeyPropertyTwo",
				metadata.getPrimaryDisplayAttributeName());
	}
	
	@Test
	public void testGetMetadataForClass_PKFields_TwoFieldNoIdClass() {
		DataObjectMetadata metadata = metadataProvider.getMetadataForType(CollectionDataObject.class);
		Assert.assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		System.err.println("PK Properties: " + metadata.getPrimaryKeyAttributeNames());
		Assert.assertEquals("PK field list length incorrect", 2, metadata.getPrimaryKeyAttributeNames().size());
		Assert.assertEquals("PK field 1 wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		Assert.assertEquals("PK field 2 wrong", "collectionKeyProperty", metadata.getPrimaryKeyAttributeNames().get(1));
		Assert.assertEquals("Primary Display Field Wrong", "collectionKeyProperty",
				metadata.getPrimaryDisplayAttributeName());
	}

	@Test
	public void testIsClassPersistable_ValidType() {
		Assert.assertTrue("TestDataObject should have been persistable",
				metadataProvider.handles(TestDataObject.class));
	}

	@Test
	public void testIsClassPersistable_InvalidType() {
		Assert.assertFalse("TestNonPersistableObject should not have been persistable",
				metadataProvider.handles(TestNonPersistableObject.class));
	}

	@Test
	public void testAttributeSecurityIfEncrypted() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("encryptedProperty");

		Assert.assertNotNull("encryptedProperty Missing", attribute);
		Assert.assertNotNull("attribute security missing on encryptedProperty", attribute.getAttributeSecurity());
		System.err.println(attribute.getAttributeSecurity());
		Assert.assertEquals("attribute security not indicating a full mask", true, attribute.getAttributeSecurity()
				.isMask());
		Assert.assertNotNull("attribute security does not have a mask formatter", attribute.getAttributeSecurity()
				.getMaskFormatter());
		Assert.assertEquals("attribute security mask formatter should have been a MaskFormatterLiteral",
				DataObjectAttributeMaskFormatterLiteral.class, attribute.getAttributeSecurity().getMaskFormatter().getClass());
	}
}