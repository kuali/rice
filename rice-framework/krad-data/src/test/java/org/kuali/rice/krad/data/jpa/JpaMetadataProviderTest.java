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
package org.kuali.rice.krad.data.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.jpa.eclipselink.EclipseLinkJpaMetadataProviderImpl;
import org.kuali.rice.krad.data.jpa.testbo.CollectionDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectExtension;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObjectTwoPkFields;
import org.kuali.rice.krad.data.jpa.testbo.TestNonPersistableObject;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataChild;

import static org.junit.Assert.*;

public class JpaMetadataProviderTest {
	static EclipseLinkJpaMetadataProviderImpl metadataProvider;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		metadataProvider = new EclipseLinkJpaMetadataProviderImpl();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("krad-data-unit-test");
		metadataProvider.setEntityManager(entityManagerFactory.createEntityManager());
	}

	@Test
	public void testGetMetadataForType() throws Exception {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		assertNotNull("Returned metadata should not be null", metadata);
		metadata = metadataProvider.getMetadataForType(Class.forName("org.kuali.rice.krad.data.jpa.testbo.TestDataObject"));
		assertNotNull("Returned metadata should not be null", metadata);
	}

	@Test
	public void testGetAllMetadata() throws Exception {
		Map<Class<?>, DataObjectMetadata> metadata = metadataProvider.provideMetadata();
		assertNotNull("Returned metadata should not be null", metadata);
		assertFalse("metadata map should not have been empty", metadata.isEmpty());
		assertTrue("Should have had an entry for TestDataObject", metadata.containsKey(TestDataObject.class));
		assertTrue("Should have had an entry for TestDataObject (when class name specified)", metadata.containsKey(
                Class.forName("org.kuali.rice.krad.data.jpa.testbo.TestDataObject")));
	}

	@Test
	public void testGetMetadataForClass_ClassData() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		System.err.println(metadata);
		assertEquals("Incorrect Data Object Type", TestDataObject.class, metadata.getType());
		assertEquals("Incorrect Type Label", "Test Data Object", metadata.getLabel());
		assertEquals("Table name not set as the backing object name", "KRTST_TEST_TABLE_T",
                metadata.getBackingObjectName());
	}

	@Test
	public void testGetMetadataForClass_VersionAttribute() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		assertEquals("Incorrect Version Setting on TestDataObject", Boolean.FALSE,
                metadata.isSupportsOptimisticLocking());

		metadata = metadataProvider.getMetadataForType(TestDataObjectTwoPkFields.class);
		assertEquals("Incorrect Version Setting on TestDataObjectTwoPkFields", Boolean.TRUE,
                metadata.isSupportsOptimisticLocking());
	}

	@Test
	public void testGetMetadataForClass_Attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		List<DataObjectAttribute> attributes = metadata.getAttributes();
		System.err.println(attributes);
		assertNotNull("Returned attributes should not have been null", attributes);
		assertFalse("Returned attributes should not have been empty", attributes.isEmpty());
		DataObjectAttribute firstAttribute = metadata.getAttribute("primaryKeyProperty");
		assertEquals("property name incorrect", "primaryKeyProperty", firstAttribute.getName());
		assertEquals("Property label incorrect", "Primary Key Property", firstAttribute.getLabel());
		assertEquals("Column name not set as the backing object name", "PK_PROP", firstAttribute.getBackingObjectName());
	}

	@Test
	public void testGetMetadataForClass_Attribute_Types() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("stringProperty");

		assertEquals("property DataType incorrect", DataType.STRING, attribute.getDataType());

		attribute = metadata.getAttribute("dateProperty");

		assertEquals("property DataType incorrect", DataType.DATE, attribute.getDataType());

		attribute = metadata.getAttribute("currencyProperty");

		assertEquals("property DataType incorrect", DataType.CURRENCY, attribute.getDataType());
	}

	@Test
	public void testGetMetadataForClass_Attribute_Type_Unknown() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("nonStandardDataType");

		assertEquals("nonStandardDataType property DataType incorrect", DataType.STRING, attribute.getDataType());
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

        // TestDataObject has 4 collections
		assertNotNull("Collections object should not be null", collections);
		assertEquals("Collections size incorrect", 4, collections.size());

		DataObjectCollection collection = collections.get(0);
		assertEquals("property name incorrect", "collectionProperty", collection.getName());
		assertEquals("collection backing object incorrect", "KRTST_TEST_COLL_T", collection.getBackingObjectName());
		assertEquals("collection label incorrect", "Collection Property", collection.getLabel());
		assertEquals("collection item label incorrect", "Collection Data Object", collection.getElementLabel());
		
		collection = metadata.getCollection("collectionPropertyTwo");
		assertNotNull("Collection object for collectionPropertyTwo should not be null", collection);
		assertEquals("property name incorrect", "collectionPropertyTwo", collection.getName());

		assertNotNull("attribute relationships must not be null", collection.getAttributeRelationships());
		assertEquals("attribute relationships size incorrect", 1, collection.getAttributeRelationships().size());
		DataObjectAttributeRelationship relationship = collection.getAttributeRelationships().get(0);
		assertEquals("parent attribute name mismatch", "stringProperty", relationship.getParentAttributeName());
		assertEquals("child attribute name mismatch", "primaryKeyPropertyUsingDifferentName",
                relationship.getChildAttributeName());

		assertNotNull("collection default sort list must not be null", collection.getDefaultOrdering());
		assertEquals("collection default sort size incorrect", 1, collection.getDefaultOrdering().size());
	}

	@Test
	public void testGetMetadataForCollection_Indirect() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectCollection collection = metadata.getCollection("indirectCollection");
		assertNotNull("Collection object for indirectCollection should not be null", collection);
		assertTrue("Should be labeled as indirect", collection.isIndirectCollection());
		assertTrue("attribute relationship list should be empty: collection.getAttributeRelationships()",
                collection.getAttributeRelationships().isEmpty());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		List<DataObjectRelationship> relationships = metadata.getRelationships();
		System.err.println(relationships);
		assertNotNull("Relationships object should not be null", relationships);
		assertEquals("Relationships size incorrect", 4, relationships.size());
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		assertNotNull("retrieval by property name failed", relationship);
		assertEquals("property name incorrect", "referencedObject", relationship.getName());
		assertEquals("collection backing object incorrect", "KRTST_TEST_REF_OBJ_T", relationship.getBackingObjectName());
		assertEquals("collection label incorrect", "Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		assertTrue("relationship should have been proxied", relationship.isLoadedDynamicallyUponUse());
		assertFalse("loaded with parent should be false", relationship.isLoadedAtParentLoadTime());
		assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_referencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("referencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		assertEquals("attribute relationships size incorrect", 1, relationship.getAttributeRelationships().size());
		DataObjectAttributeRelationship linkingAttributes = relationship.getAttributeRelationships().get(0);
		assertEquals("parent attribute name mismatch", "stringProperty", linkingAttributes.getParentAttributeName());
		assertEquals("child attribute name mismatch", "stringProperty", linkingAttributes.getChildAttributeName());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertEquals("property name incorrect", "anotherReferencedObject", relationship.getName());
		assertEquals("collection backing object incorrect", "KRTST_TEST_ANOTHER_REF_OBJ_T",
                relationship.getBackingObjectName());
		assertEquals("collection label incorrect", "Another Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertFalse("relationship should not have been proxied", relationship.isLoadedDynamicallyUponUse());
		assertTrue("loaded with parent should be true", relationship.isLoadedAtParentLoadTime());
		assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_anotherReferencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		assertEquals("attribute relationships size incorrect", 2, relationship.getAttributeRelationships().size());
		DataObjectAttributeRelationship linkingAttribute = relationship.getAttributeRelationships().get(0);
		assertEquals("first parent attribute name mismatch", "stringProperty",
                linkingAttribute.getParentAttributeName());
		assertEquals("first child attribute name mismatch", "stringProperty", linkingAttribute.getChildAttributeName());

		linkingAttribute = relationship.getAttributeRelationships().get(1);
		assertEquals("second parent attribute name mismatch", "dateProperty", linkingAttribute.getParentAttributeName());
		assertEquals("second child attribute name mismatch", "dateProperty", linkingAttribute.getChildAttributeName());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_main() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertEquals("property name incorrect", "anotherReferencedObject", relationship.getName());
		assertEquals("collection backing object incorrect", "KRTST_TEST_ANOTHER_REF_OBJ_T",
                relationship.getBackingObjectName());
		assertEquals("collection label incorrect", "Another Referenced Object", relationship.getLabel());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_properties() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertFalse("relationship should not have been proxied", relationship.isLoadedDynamicallyUponUse());
		assertTrue("loaded with parent should be true", relationship.isLoadedAtParentLoadTime());
		assertFalse("saved with parent should be false", relationship.isSavedWithParent());
		assertFalse("deleted with parent should be false", relationship.isDeletedWithParent());
		assertTrue("read-only should be true", relationship.isReadOnly());
	}

	@Test
	public void testGetMetadataForRelationship_yetAnotherReferencedObject_attributes() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectRelationship relationship = metadata.getRelationship("anotherReferencedObject");
		assertNotNull("retrieval by property name failed", relationship);

		System.err.println("anotherReferencedObject: " + relationship);

		assertNotNull("attribute relationships must not be null", relationship.getAttributeRelationships());
		assertEquals("attribute relationships size incorrect", 2, relationship.getAttributeRelationships().size());
		DataObjectAttributeRelationship linkingAttribute = relationship.getAttributeRelationships().get(0);
		assertEquals("first parent attribute name mismatch", "stringProperty",
                linkingAttribute.getParentAttributeName());
		assertEquals("first child attribute name mismatch", "stringProperty", linkingAttribute.getChildAttributeName());

		linkingAttribute = relationship.getAttributeRelationships().get(1);
		assertEquals("second parent attribute name mismatch", "dateProperty", linkingAttribute.getParentAttributeName());
		assertEquals("second child attribute name mismatch", "dateProperty", linkingAttribute.getChildAttributeName());
	}

    @Test
    public void testGetMetadataForRelationship_extension() {
        DataObjectMetadata metadata = metadataProvider
                .getMetadataForType(TestDataObject.class);
        DataObjectRelationship relationship = metadata.getRelationship("extension");
        assertNotNull("retrieval by property name failed", relationship);
        assertTrue("Should have no attribute relationships.", relationship.getAttributeRelationships().isEmpty());

        assertTrue("should be loaded with parent", relationship.isLoadedAtParentLoadTime());
        assertFalse("should NOT be proxied", relationship.isLoadedDynamicallyUponUse());
        assertFalse("should NOT be read-only", relationship.isReadOnly());
        assertTrue("should be saved with parent", relationship.isSavedWithParent());
        assertTrue("should be deleted with parent", relationship.isDeletedWithParent());
        assertEquals("Should be related to TestDataObjectExtension", TestDataObjectExtension.class, relationship.getRelatedType());

        MetadataChild inverse = relationship.getInverseRelationship();
        assertNotNull("extension relationship should have an inverse relationship", inverse);
        assertTrue("Inverse should be a relationship and not a collection.", inverse instanceof DataObjectRelationship);

    }

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_stringProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata.getRelationshipByLastAttributeInRelationship("stringProperty");
		assertNotNull("retrieval by last attribute name (stringProperty) failed", relationship);
		assertEquals("retrieval by last attribute name (stringProperty) returned wrong relationship",
                "referencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_dateProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata.getRelationshipByLastAttributeInRelationship("dateProperty");
		assertNotNull("retrieval by last attribute name (dateProperty) failed", relationship);
		assertEquals("retrieval by last attribute name (dateProperty) returned wrong relationship",
                "anotherReferencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byLastAttribute_primaryKeyProperty() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		DataObjectRelationship relationship = metadata
				.getRelationshipByLastAttributeInRelationship("primaryKeyProperty");
		assertNotNull("retrieval by last attribute name (primaryKeyProperty) failed", relationship);
		assertEquals("retrieval by last attribute name (primaryKeyProperty) returned wrong relationship",
                "yetAnotherReferencedObject", relationship.getName());
	}

	@Test
	public void testGetMetadataForRelationship_byInvolvedAttribute() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);

		List<DataObjectRelationship> rels = metadata.getRelationshipsInvolvingAttribute("stringProperty");
		assertNotNull("retrieval by attribute name (stringProperty) failed", rels);
		assertEquals("retrieval by attribute name (stringProperty): wrong number returned", 2, rels.size());

		rels = metadata.getRelationshipsInvolvingAttribute("dateProperty");
		assertNotNull("retrieval by attribute name (dateProperty) failed", rels);
		assertEquals("retrieval by attribute name (dateProperty): wrong number returned", 1, rels.size());
	}

	@Test
	public void testGetMetadataForClass_PKFields() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		assertEquals("PK field list length incorrect", 1, metadata.getPrimaryKeyAttributeNames().size());
		assertEquals("PK field wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		assertEquals("Primary Display Field Wrong", "primaryKeyProperty", metadata.getPrimaryDisplayAttributeName());
	}

	@Test
	public void testGetMetadataForClass_PKFields_TwoFieldIdClass() {
		DataObjectMetadata metadata = metadataProvider.getMetadataForType(TestDataObjectTwoPkFields.class);
		assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		System.err.println("PK Properties: " + metadata.getPrimaryKeyAttributeNames());
		assertEquals("PK field list length incorrect", 2, metadata.getPrimaryKeyAttributeNames().size());
		assertEquals("PK field 1 wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		assertEquals("PK field 2 wrong", "primaryKeyPropertyTwo", metadata.getPrimaryKeyAttributeNames().get(1));
		assertEquals("Primary Display Field Wrong", "primaryKeyPropertyTwo", metadata.getPrimaryDisplayAttributeName());
	}
	
	@Test
	public void testGetMetadataForClass_PKFields_TwoFieldNoIdClass() {
		DataObjectMetadata metadata = metadataProvider.getMetadataForType(CollectionDataObject.class);
		assertNotNull("PK field list should not have been null", metadata.getPrimaryKeyAttributeNames());
		System.err.println("PK Properties: " + metadata.getPrimaryKeyAttributeNames());
		assertEquals("PK field list length incorrect", 2, metadata.getPrimaryKeyAttributeNames().size());
		assertEquals("PK field 1 wrong", "primaryKeyProperty", metadata.getPrimaryKeyAttributeNames().get(0));
		assertEquals("PK field 2 wrong", "collectionKeyProperty", metadata.getPrimaryKeyAttributeNames().get(1));
		assertEquals("Primary Display Field Wrong", "collectionKeyProperty", metadata.getPrimaryDisplayAttributeName());
	}

	@Test
	public void testIsClassPersistable_ValidType() {
		assertTrue("TestDataObject should have been persistable", metadataProvider.handles(TestDataObject.class));
	}

	@Test
	public void testIsClassPersistable_InvalidType() {
		assertFalse("TestNonPersistableObject should not have been persistable", metadataProvider.handles(
                TestNonPersistableObject.class));
	}

	@Test
	public void testAttributeSecurityIfEncrypted() {
		DataObjectMetadata metadata = metadataProvider
				.getMetadataForType(TestDataObject.class);
		DataObjectAttribute attribute = metadata.getAttribute("encryptedProperty");

		assertNotNull("encryptedProperty Missing", attribute);
		assertTrue("sensitive property not set on encryptedProperty", attribute.isSensitive());
	}
}