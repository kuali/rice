/**
 * Copyright 2005-2017 The Kuali Foundation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.EqualPredicate;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.GenericQueryResults.Builder;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.MaterializeOption;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataChild;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectRelationshipImpl;
import org.kuali.rice.krad.data.provider.impl.DataObjectWrapperBase;
import org.kuali.rice.krad.data.util.ReferenceLinker;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.NullValueInNestedPathException;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectWrapperBaseTest {

    @Mock private DataObjectService dataObjectService;
	@Mock
	private DataObjectMetadata dataObjectMetadata;
	@Mock
	private DataObjectMetadata dataObject2Metadata;
	@Mock
	private DataObjectMetadata dataObject3Metadata;
	@Mock
	private DataObjectMetadata dataObject4Metadata;
	@Mock
	private DataObjectMetadata dataObject5Metadata;
    @Mock private ReferenceLinker referenceLinker;

    private DataObject dataObject;
    private DataObject2 dataObject2;
	private DataObject4 dataObject4;
	private DataObjectWrapperBase<DataObject> wrap;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpDataObjectMetadataMocks() {
		when(dataObjectService.supports(DataObject.class)).thenReturn(true);
		when(dataObjectMetadata.getType()).thenReturn((Class) DataObject.class);
		when(dataObjectMetadata.getPrimaryKeyAttributeNames()).thenReturn(Collections.singletonList("id"));

		// M:1 relationship, lazy-loaded, not part of parent object tree
		DataObjectRelationshipImpl dataObject2Relationship = new DataObjectRelationshipImpl();
		dataObject2Relationship.setName("dataObject2");
		dataObject2Relationship.setRelatedType(DataObject2.class);
		dataObject2Relationship.setSavedWithParent(false);
		dataObject2Relationship.setLoadedAtParentLoadTime(false);
		dataObject2Relationship.setLoadedDynamicallyUponUse(true);
		dataObject2Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("dataObject2sKey", "one")));

		when(dataObjectMetadata.getRelationship("dataObject2")).thenReturn(dataObject2Relationship);

		// M:1 relationship, eager-loaded, not part of parent object tree
		DataObjectRelationshipImpl eagerDataObject2Relationship = new DataObjectRelationshipImpl();
		eagerDataObject2Relationship.setName("eagerDataObject2");
		eagerDataObject2Relationship.setRelatedType(DataObject2.class);
		eagerDataObject2Relationship.setSavedWithParent(false);
		eagerDataObject2Relationship.setLoadedAtParentLoadTime(true);
		eagerDataObject2Relationship.setLoadedDynamicallyUponUse(false);
		eagerDataObject2Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("dataObject2sKey", "one")));

		when(dataObjectMetadata.getRelationship("eagerDataObject2")).thenReturn(eagerDataObject2Relationship);

		when(dataObjectMetadata.getRelationships()).thenReturn(
				(List) Arrays.asList(dataObject2Relationship, eagerDataObject2Relationship));

		// 1:M relationship, lazy-loaded, saved with parent
		DataObjectCollectionImpl dataObject3Relationship = new DataObjectCollectionImpl();
		dataObject3Relationship.setName("dataObject3s");
		dataObject3Relationship.setRelatedType(DataObject3.class);
		dataObject3Relationship.setSavedWithParent(true);
		dataObject3Relationship.setLoadedAtParentLoadTime(false);
		dataObject3Relationship.setLoadedDynamicallyUponUse(true);
		dataObject3Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("id", "parentId")));

		when(dataObjectMetadata.getCollections()).thenReturn((List) Collections.singletonList(dataObject3Relationship));
		when(dataObjectMetadata.getCollection("dataObject3s")).thenReturn(dataObject3Relationship);
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpDataObject2MetadataMocks() {
		when(dataObjectService.supports(DataObject2.class)).thenReturn(true);
		when(dataObject2Metadata.getType()).thenReturn((Class) DataObject2.class);
		when(dataObject2Metadata.getPrimaryKeyAttributeNames()).thenReturn(Collections.singletonList("one"));

		// M:1 relationship, lazy-loaded, not part of parent object tree
		DataObjectRelationshipImpl dataObject4Relationship = new DataObjectRelationshipImpl();
		dataObject4Relationship.setName("dataObject4");
		dataObject4Relationship.setRelatedType(DataObject4.class);
		dataObject4Relationship.setSavedWithParent(false);
		dataObject4Relationship.setLoadedAtParentLoadTime(false);
		dataObject4Relationship.setLoadedDynamicallyUponUse(true);
		dataObject4Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("two", "pk")));

		when(dataObject2Metadata.getRelationship("dataObject4")).thenReturn(dataObject4Relationship);
		when(dataObject2Metadata.getRelationships()).thenReturn((List) Arrays.asList(dataObject4Relationship));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpDataObject3MetadataMocks() {
		when(dataObjectService.supports(DataObject3.class)).thenReturn(true);
		when(dataObject3Metadata.getType()).thenReturn((Class) DataObject3.class);
		when(dataObject3Metadata.getPrimaryKeyAttributeNames()).thenReturn(Arrays.asList("parentId", "id"));

		// M:1 relationship, lazy-loaded, not part of parent object tree
		DataObjectRelationshipImpl dataObject2Relationship = new DataObjectRelationshipImpl();
		dataObject2Relationship.setName("dataObject2");
		dataObject2Relationship.setRelatedType(DataObject2.class);
		dataObject2Relationship.setSavedWithParent(false);
		dataObject2Relationship.setLoadedAtParentLoadTime(false);
		dataObject2Relationship.setLoadedDynamicallyUponUse(true);
		dataObject2Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("world", "one")));

		when(dataObject3Metadata.getRelationship("dataObject2")).thenReturn(dataObject2Relationship);
		when(dataObject3Metadata.getRelationships()).thenReturn((List) Arrays.asList(dataObject2Relationship));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpDataObject4MetadataMocks() {
		when(dataObjectService.supports(DataObject4.class)).thenReturn(true);
		when(dataObject4Metadata.getType()).thenReturn((Class) DataObject4.class);
		when(dataObject4Metadata.getPrimaryKeyAttributeNames()).thenReturn(Collections.singletonList("pk"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpDataObject5MetadataMocks() {
		when(dataObjectService.supports(DataObject.class)).thenReturn(true);
		when(dataObject5Metadata.getType()).thenReturn((Class) DataObject.class);
		when(dataObject5Metadata.getPrimaryKeyAttributeNames()).thenReturn(Collections.singletonList("id"));

		// M:1 relationship, lazy-loaded, not part of parent object tree
		DataObjectRelationshipImpl dataObject2Relationship = new DataObjectRelationshipImpl();
		dataObject2Relationship.setName("dataObject2");
		dataObject2Relationship.setRelatedType(DataObject2.class);
		dataObject2Relationship.setSavedWithParent(false);
		dataObject2Relationship.setLoadedAtParentLoadTime(false);
		dataObject2Relationship.setLoadedDynamicallyUponUse(true);
		dataObject2Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("dataObject2sKey", null)));

		when(dataObject5Metadata.getRelationship("dataObject2")).thenReturn(dataObject2Relationship);

		// M:1 relationship, eager-loaded, not part of parent object tree
		DataObjectRelationshipImpl eagerDataObject2Relationship = new DataObjectRelationshipImpl();
		eagerDataObject2Relationship.setName("eagerDataObject2");
		eagerDataObject2Relationship.setRelatedType(DataObject2.class);
		eagerDataObject2Relationship.setSavedWithParent(false);
		eagerDataObject2Relationship.setLoadedAtParentLoadTime(true);
		eagerDataObject2Relationship.setLoadedDynamicallyUponUse(false);
		eagerDataObject2Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("dataObject2sKey", "one")));

		when(dataObject5Metadata.getRelationship("eagerDataObject2")).thenReturn(eagerDataObject2Relationship);

		when(dataObject5Metadata.getRelationships()).thenReturn(
				(List) Arrays.asList(dataObject2Relationship, eagerDataObject2Relationship));

		// 1:M relationship, lazy-loaded, saved with parent
		DataObjectCollectionImpl dataObject3Relationship = new DataObjectCollectionImpl();
		dataObject3Relationship.setName("dataObject3s");
		dataObject3Relationship.setRelatedType(DataObject3.class);
		dataObject3Relationship.setSavedWithParent(true);
		dataObject3Relationship.setLoadedAtParentLoadTime(false);
		dataObject3Relationship.setLoadedDynamicallyUponUse(true);
		dataObject3Relationship.setAttributeRelationships((List) Collections
				.singletonList(new DataObjectAttributeRelationshipImpl("id", "parentId")));

		when(dataObject5Metadata.getCollections()).thenReturn((List) Collections.singletonList(dataObject3Relationship));
		when(dataObject5Metadata.getCollection("dataObject3s")).thenReturn(dataObject3Relationship);
	}

    protected void configureMocks() {
		setUpDataObjectMetadataMocks();
		setUpDataObject2MetadataMocks();
		setUpDataObject3MetadataMocks();
		setUpDataObject4MetadataMocks();
		setUpDataObject5MetadataMocks();

		when(dataObjectService.findMatching(any(Class.class), any(QueryByCriteria.class))).thenAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				Class<?> dataObjectType = (Class<?>) invocation.getArguments()[0];
				QueryByCriteria criteria = (QueryByCriteria) invocation.getArguments()[1];
				if (DataObject3.class.isAssignableFrom(dataObjectType)) {
					if (criteria.getPredicate() instanceof EqualPredicate
							&& ((EqualPredicate) criteria.getPredicate()).getPropertyPath().equals("parentId")
							&& ((EqualPredicate) criteria.getPredicate()).getValue().getValue().equals("1")) {
						Builder builder = GenericQueryResults.Builder.create();
						builder.setResults(Arrays.asList(
								new DataObject3("1", "C1", "hello", "world")
								, new DataObject3("1", "C2", "howdy", "Westeros")));
						builder.setTotalRowCount(2);
						return builder.build();
					}
				}

				// return a completely empty object if unknown
				return GenericQueryResults.Builder.create().build();
			}
		});

		when(dataObjectService.find(any(Class.class), any())).thenAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				Class<?> dataObjectType = (Class<?>) invocation.getArguments()[0];
				Object primaryKey = invocation.getArguments()[1];
				if (DataObject2.class.isAssignableFrom(dataObjectType)) {
					if (primaryKey instanceof String && StringUtils.equals((String) primaryKey, "one")) {
						return dataObject2;
					}
				} else if (DataObject4.class.isAssignableFrom(dataObjectType)) {
					if (primaryKey instanceof String && StringUtils.equals((String) primaryKey, "two")) {
						return dataObject4;
					}
				}

				return null;
			}
		});

        // make it so that DataObjectService returns a proper wrap when asked
		when(dataObjectService.wrap(any())).thenAnswer(new Answer() {
            @Override
			public Object answer(InvocationOnMock invocation) {
				Object object = invocation.getArguments()[0];
				if (object instanceof DataObject) {
					return new DataObjectWrapperImpl<DataObject>((DataObject) object, dataObjectMetadata,
							dataObjectService,
							referenceLinker);
				} else if (object instanceof DataObject2) {
					return new DataObjectWrapperImpl<DataObject2>((DataObject2) object, dataObject2Metadata,
							dataObjectService,
							referenceLinker);
				} else if (object instanceof DataObject3) {
					return new DataObjectWrapperImpl<DataObject3>((DataObject3) object, dataObject3Metadata,
							dataObjectService, referenceLinker);
				} else if (object instanceof DataObject4) {
					return new DataObjectWrapperImpl<DataObject4>((DataObject4) object, dataObject4Metadata,
							dataObjectService,
							referenceLinker);
				}
				return new DataObjectWrapperImpl<Object>(object, null, dataObjectService, referenceLinker);
            }
        });
    }
    
    @Before
    public void setup() throws Exception {
		dataObject = new DataObject("1", "FieldOne", 2, "one");
		dataObject2 = new DataObject2("one", "two");
		dataObject.setDataObject2(dataObject2);
		dataObject4 = new DataObject4("two", "some other value");
		wrap = new DataObjectWrapperImpl<DataObject>(dataObject, dataObjectMetadata, dataObjectService,
				referenceLinker);

		configureMocks();
    }

    static final class DataObjectWrapperImpl<T> extends DataObjectWrapperBase<T> {
        private DataObjectWrapperImpl(T dataObject, DataObjectMetadata metadata, DataObjectService dataObjectService,
                ReferenceLinker referenceLinker) {
            super(dataObject, metadata, dataObjectService, referenceLinker);
        }
    }

	@Test
	public void testGetForeignKeyAttributeMap_NoNullChildAttributeName(){
		wrap = new DataObjectWrapperImpl<DataObject>(dataObject, dataObject5Metadata, dataObjectService, referenceLinker);
		final Map<String, Object> result = wrap.getForeignKeyAttributeMap("dataObject2");
		assertTrue(result.get((String)null)==null);
	}

    @Test
    public void testGetType() {
        assertEquals(DataObject.class, wrap.getWrappedClass());
    }

    @Test
    public void testGetMetadata() {
		assertEquals(dataObjectMetadata, wrap.getMetadata());
    }

    @Test
    public void testGetWrappedInstance() {
        assertEquals(dataObject, wrap.getWrappedInstance());
    }

    @Test
    public void testGetPrimaryKeyValues() {
        Map<String, Object> primaryKeyValues = wrap.getPrimaryKeyValues();
        assertEquals(1, primaryKeyValues.size());
        assertTrue(primaryKeyValues.containsKey("id"));
        assertEquals("1", primaryKeyValues.get("id"));

		assertEquals("Mismatch on DataObject2's PKs", Collections.singletonMap("one", "one"),
				dataObjectService.wrap(dataObject2).getPrimaryKeyValues());
    }

    @Test
    public void testEqualsByPrimaryKey() {
        // first check that it's equal to itself
        assertTrue(wrap.equalsByPrimaryKey(dataObject));

        // now create one with an equal primary key but different values for non-pk fields, should be euqual
		assertTrue(wrap.equalsByPrimaryKey(new DataObject("1", "blah", 500, "one")));

        // now create one with a different primary key, should not be equal
		assertFalse(wrap.equalsByPrimaryKey(new DataObject("2", "FieldOne", 2, "one")));

        // let's do some null checking
        assertFalse(wrap.equalsByPrimaryKey(null));

        // verify what happens when primary key is null on object being compared
		assertFalse(wrap.equalsByPrimaryKey(new DataObject(null, null, -1, "one")));

    }

    @Test
    public void testGetPropertyType_Nested() {
        assertEquals(DataObject2.class, wrap.getPropertyType("dataObject2"));
        assertEquals(String.class, wrap.getPropertyType("dataObject2.one"));
        assertEquals(String.class, wrap.getPropertyType("dataObject2.two"));
    }

    @Test
    public void testGetPropertyValueNullSafe() {
		DataObject dataObject = new DataObject("a", "b", 3, "one");
		DataObjectWrapper<DataObject> wrap = new DataObjectWrapperImpl<DataObject>(dataObject, dataObjectMetadata,
				dataObjectService,
                referenceLinker);
        assertNull(wrap.getPropertyValue("dataObject2"));

        //wrap.setPropertyValue("dataObject2.dataObject3", new DataObject3());

        // assert that a NullValueInNestedPathException is thrown
        try {
            wrap.getPropertyValue("dataObject2.dataObject3");
            fail("NullValueInNestedPathException should have been thrown");
        } catch (NullValueInNestedPathException e) {
            // this should be thrown!
        }

        // now do a null-safe check
        assertNull(wrap.getPropertyValueNullSafe("dataObject2.dataObject3"));

    }

    @Test
    public void testGetPropertyType_Collection() {
        // setup stuff
		DataObject dataObject = new DataObject("a", "b", 3, "one");
        DataObject3 do3_1 = new DataObject3();
        do3_1.setHello("hi");
        do3_1.setWorld("Earth");
        DataObject3 do3_2 = new DataObject3();
        do3_2.setHello("howdy");
        do3_2.setWorld("Westeros");
        dataObject.getDataObject3s().add(do3_1);
        dataObject.getDataObject3s().add(do3_2);

        // now check through a collection
		DataObjectWrapper<DataObject> wrap = new DataObjectWrapperImpl<DataObject>(dataObject, dataObjectMetadata,
				dataObjectService,
                referenceLinker);
        Class<?> type = wrap.getPropertyType("dataObject3s[0].hello");
        assertEquals(String.class, type);
        type = wrap.getPropertyType("dataObject3s[1].world");
        assertEquals(String.class, type);
        type = wrap.getPropertyType("dataObject3s[2].world");
        // should be null because we have nothing at this index
        assertNull(type);
    }

	@Test
	public void testMaterializeOptionMatch_Default() {
		Collection<MetadataChild> childRelationships = wrap.getChildrenMatchingOptions();
		assertNotNull("getChildrenMatchingOptions() shoud not return null", childRelationships);
		assertEquals("getChildrenMatchingOptions() returned wrong number of rows", 1, childRelationships.size());
		assertEquals("getChildrenMatchingOptions() returned wrong type of relationship",
				DataObjectRelationshipImpl.class, childRelationships.iterator().next().getClass());
		assertEquals("getChildrenMatchingOptions() relationship was for wrong property", "dataObject2",
				childRelationships.iterator().next().getName());
		assertEquals("getChildrenMatchingOptions() relationship was for wrong data type", DataObject2.class,
				childRelationships.iterator().next().getRelatedType());
	}

	@Test
	public void testMaterializeOptionMatch_WithEager() {
		Collection<MetadataChild> childRelationships = wrap
				.getChildrenMatchingOptions(MaterializeOption.INCLUDE_EAGER_REFS);
		assertNotNull("getChildrenMatchingOptions() shoud not return null", childRelationships);
		assertEquals("getChildrenMatchingOptions() returned wrong number of rows", 2, childRelationships.size());
	}

	@Test
	public void testMaterializeOptionMatch_CollectionsOnly_NonUpdatable() {
		Collection<MetadataChild> childRelationships = wrap.getChildrenMatchingOptions(MaterializeOption.COLLECTIONS);
		assertNotNull("getChildrenMatchingOptions() shoud not return null", childRelationships);
		assertEquals("getChildrenMatchingOptions() returned wrong number of rows", 0, childRelationships.size());
	}

	@Test
	public void testMaterializeOptionMatch_CollectionsOnly_Updatable() {
		Collection<MetadataChild> childRelationships = wrap.getChildrenMatchingOptions(MaterializeOption.COLLECTIONS,
				MaterializeOption.UPDATE_UPDATABLE_REFS);
		assertNotNull("getChildrenMatchingOptions() shoud not return null", childRelationships);
		assertEquals("getChildrenMatchingOptions() returned wrong number of rows", 1, childRelationships.size());
		assertEquals("getChildrenMatchingOptions() returned wrong type of relationship",
				DataObjectCollectionImpl.class, childRelationships.iterator().next().getClass());
		assertEquals("getChildrenMatchingOptions() relationship was for wrong property", "dataObject3s",
				childRelationships.iterator().next().getName());
		assertEquals("getChildrenMatchingOptions() relationship was for wrong data type", DataObject3.class,
				childRelationships.iterator().next().getRelatedType());
	}

	@Test
	public void testMaterializeOptionMatch_Updatable() {
		Collection<MetadataChild> childRelationships = wrap
				.getChildrenMatchingOptions(MaterializeOption.UPDATE_UPDATABLE_REFS);
		assertNotNull("getChildrenMatchingOptions() shoud not return null", childRelationships);
		assertEquals("getChildrenMatchingOptions() returned wrong number of rows", 2, childRelationships.size());
	}

	@Test
	public void testMaterialize_Default() {
		// nulling out reference so it can be reloaded
		dataObject.setDataObject2(null);

		wrap.materializeReferencedObjects();
		verify(dataObjectService).supports(DataObject2.class);
		verify(dataObjectService, never()).supports(DataObject4.class);

		verify(dataObjectService).find(DataObject2.class, "one");
		assertNotNull("dataObject2 should have been loaded", dataObject.getDataObject2());
	}

	@Test
	public void testMaterialize_Recursive() {
		// nulling out reference so it can be reloaded
		dataObject.setDataObject2(null);

		wrap.materializeReferencedObjectsToDepth(2);
		verify(dataObjectService, atLeastOnce()).supports(DataObject2.class);
		verify(dataObjectService).find(DataObject2.class, "one");
		verify(dataObjectService, atLeastOnce()).supports(DataObject4.class);
		verify(dataObjectService).find(DataObject4.class, "two");

		assertNotNull("dataObject2 should have been loaded", dataObject.getDataObject2());
		assertNotNull("dataObject2.dataObject4 should have been loaded", dataObject.getDataObject2().getDataObject4());
	}

	@Test
	public void testMaterialize_Recursive_WithCollections() {
		// nulling out reference so it can be reloaded
		dataObject.setDataObject2(null);
		dataObject.setDataObject3s(null);

		wrap.materializeReferencedObjectsToDepth(2, MaterializeOption.UPDATE_UPDATABLE_REFS);
		verify(dataObjectService, atLeastOnce()).supports(DataObject2.class);
		verify(dataObjectService, atLeastOnce()).supports(DataObject3.class);
		verify(dataObjectService, atLeastOnce()).supports(DataObject4.class);

		verify(dataObjectService).find(DataObject2.class, "one");
		verify(dataObjectService).find(DataObject4.class, "two");

		assertNotNull("dataObject2 should have been loaded", dataObject.getDataObject2());
		assertNotNull("dataObject2.dataObject4 should have been loaded", dataObject.getDataObject2().getDataObject4());

		verify(dataObjectService).findMatching(DataObject3.class,
				QueryByCriteria.Builder.andAttributes(Collections.singletonMap("parentId", "1")).build());

		assertNotNull("The list of DataObject3 should not have been nulled out", dataObject.getDataObject3s());
		assertEquals("The list of DataObject3 should have had records", 2, dataObject.getDataObject3s().size());

		// Confirm that it attempted to load each of the child objects of the child records in the collection
		verify(dataObjectService).find(DataObject2.class, "world");
		verify(dataObjectService).find(DataObject2.class, "Westeros");
	}

	@Test
	public void testMaterialize_InvalidCode_DontNullIt() {
		assertNotNull("dataObject2 should not be null at start of test", dataObject.getDataObject2());

		// setting the foreign key to an invalid value
		dataObject.setDataObject2sKey("SOMETHING_INVALID");

		wrap.materializeReferencedObjects();
		verify(dataObjectService).supports(DataObject2.class);
		verify(dataObjectService).find(DataObject2.class, "SOMETHING_INVALID");
		assertNotNull("dataObject2 should not have been nulled out", dataObject.getDataObject2());
		assertEquals("The object should be the original, with the originals PK", "one", dataObject.getDataObject2()
				.getOne());
	}

	@Test
	public void testMaterialize_InvalidCode_PleaseNullIt() {
		assertNotNull("dataObject2 should not be null at start of test", dataObject.getDataObject2());

		// setting the foreign key to an invalid value
		dataObject.setDataObject2sKey("SOMETHING_INVALID");

		wrap.materializeReferencedObjects(MaterializeOption.NULL_INVALID_REFS);
		verify(dataObjectService).supports(DataObject2.class);
		verify(dataObjectService).find(DataObject2.class, "SOMETHING_INVALID");
		assertNull("dataObject2 should have been nulled out", dataObject.getDataObject2());
	}

	@Test
	public void testMaterialize_UpdateUpdatable() {
		wrap.materializeReferencedObjects(MaterializeOption.UPDATE_UPDATABLE_REFS);
		verify(dataObjectService).supports(DataObject2.class);
		verify(dataObjectService).supports(DataObject3.class);
		verify(dataObjectService).find(DataObject2.class, "one");
		verify(dataObjectService).findMatching(DataObject3.class,
				QueryByCriteria.Builder.andAttributes(Collections.singletonMap("parentId", "1")).build());
		assertNotNull("The list of DataObject3 should not have been nulled out", dataObject.getDataObject3s());
	}

    public static final class DataObject {

        private String id;
        private String fieldOne;
        private Integer fieldTwo;
		private String dataObject2sKey;
        private DataObject2 dataObject2;
		private DataObject2 eagerDataObject2;

        private List<DataObject3> dataObject3s;

		DataObject(String id, String fieldOne, Integer fieldTwo, String dataObject2sKey) {
            this.id = id;
            this.fieldOne = fieldOne;
            this.fieldTwo = fieldTwo;
			this.dataObject2sKey = dataObject2sKey;
            this.dataObject3s = new ArrayList<DataObject3>();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFieldOne() {
            return fieldOne;
        }

        public void setFieldOne(String fieldOne) {
            this.fieldOne = fieldOne;
        }

        public Integer getFieldTwo() {
            return fieldTwo;
        }

        public void setFieldTwo(Integer fieldTwo) {
            this.fieldTwo = fieldTwo;
        }

        public DataObject2 getDataObject2() {
            return dataObject2;
        }

        public void setDataObject2(DataObject2 dataObject2) {
            this.dataObject2 = dataObject2;
        }

        public List<DataObject3> getDataObject3s() {
            return dataObject3s;
        }

        public void setDataObject3s(List<DataObject3> dataObject3s) {
            this.dataObject3s = dataObject3s;
        }

		public String getDataObject2sKey() {
			return dataObject2sKey;
		}

		public void setDataObject2sKey(String dataObject2sKey) {
			this.dataObject2sKey = dataObject2sKey;
		}

		public DataObject2 getEagerDataObject2() {
			return eagerDataObject2;
		}

		public void setEagerDataObject2(DataObject2 eagerDataObject2) {
			this.eagerDataObject2 = eagerDataObject2;
		}
    }

    public static final class DataObject2 {
        private String one;
		private String two; // FK for data object 4
		private DataObject4 dataObject4;

        public DataObject2() {}

        public DataObject2(String one, String two) {
            this.one = one;
            this.two = two;
        }

        public String getOne() {
            return one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        public String getTwo() {
            return two;
        }

        public void setTwo(String two) {
            this.two = two;
        }

		public DataObject4 getDataObject4() {
			return dataObject4;
        }

		public void setDataObject4(DataObject4 dataObject3) {
			this.dataObject4 = dataObject3;
        }
    }

	public static final class DataObject4 {
		private String pk;
		private String notPk;

		public DataObject4(String pk, String notPk) {
			this.pk = pk;
			this.notPk = notPk;
		}

		public String getPk() {
			return pk;
		}

		public void setPk(String pk) {
			this.pk = pk;
		}

		public String getNotPk() {
			return notPk;
		}

		public void setNotPk(String notPk) {
			this.notPk = notPk;
		}
	}

    public static final class DataObject3 {

		private String parentId;
		private String id;
        private String hello;
		private String world; // FK for data object 2
		private DataObject2 dataObject2;

		public DataObject3() {
		}

		public DataObject3(String parentId, String id, String hello, String world) {
			super();
			this.parentId = parentId;
			this.id = id;
			this.hello = hello;
			this.world = world;
		}

		public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }

        public String getWorld() {
            return world;
        }

        public void setWorld(String world) {
            this.world = world;
        }

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public DataObject2 getDataObject2() {
			return dataObject2;
		}

		public void setDataObject2(DataObject2 dataObject2) {
			this.dataObject2 = dataObject2;
		}

    }
}
