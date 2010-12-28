/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.impl.XmlObjectSerializerServiceImpl;
import org.kuali.rice.test.BaseRiceTestCase;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class XmlObjectSerializerServiceTest extends BaseRiceTestCase {

	private static final String LEGACY_PBO_NOTE_XML =
		"<org.kuali.rice.kns.service.XmlObjectSerializerServiceTest_-PreNotesChangeBO>" +
		"  <newCollectionRecord>false</newCollectionRecord>" +
	    "  <boNotes>" +
	    "    <org.kuali.rice.kns.bo.Note>" +
	    "      <newCollectionRecord>false</newCollectionRecord>" +
	    "      <noteTypeCode>BO</noteTypeCode>" +
	    "      <noteText>my note text</noteText>" +
	    "      <noteTopicText>the topic</noteTopicText>" +
	    "      <adHocRouteRecipient class=\"org.kuali.rice.kns.bo.AdHocRoutePerson\">" +
	    "        <versionNumber>1</versionNumber>" +
	    "        <newCollectionRecord>false</newCollectionRecord>" +
	    "        <type>0</type>" +
	    "        <actionRequested>A</actionRequested>" +
	    "      </adHocRouteRecipient>" +
	    "    </org.kuali.rice.kns.bo.Note>" +
	    "  </boNotes>" +
	    "</org.kuali.rice.kns.service.XmlObjectSerializerServiceTest_-PreNotesChangeBO>";
	
	private static final String BO_WITH_NOTES_XML =
		"<org.kuali.rice.kns.service.XmlObjectSerializerServiceTest_-BOWithNotes>" +
		"  <newCollectionRecord>false</newCollectionRecord>" +
	    "  <boNotes>" +
	    "    <org.kuali.rice.kns.bo.Note>" +
	    "      <newCollectionRecord>false</newCollectionRecord>" +
	    "      <noteTypeCode>BO</noteTypeCode>" +
	    "      <noteText>my note text</noteText>" +
	    "      <noteTopicText>the topic</noteTopicText>" +
	    "      <adHocRouteRecipient class=\"org.kuali.rice.kns.bo.AdHocRoutePerson\">" +
	    "        <versionNumber>1</versionNumber>" +
	    "        <newCollectionRecord>false</newCollectionRecord>" +
	    "        <type>0</type>" +
	    "        <actionRequested>A</actionRequested>" +
	    "      </adHocRouteRecipient>" +
	    "    </org.kuali.rice.kns.bo.Note>" +
	    "  </boNotes>" +
	    "</org.kuali.rice.kns.service.XmlObjectSerializerServiceTest_-BOWithNotes>";
	
	private XmlObjectSerializerServiceImpl service;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		service = new XmlObjectSerializerServiceImpl();
		service.setPersistenceService(new MockPersistenceService());
	}

	/**
	 * Tests that legacy PersistableBusinessObject xml (which has notes encoded in it) will unmarshall
	 * property after boNotes was removed from PersistableBusinessObjectBase as part of the notes
	 * refactoring in Rice 1.1.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLegacyNoteDeserialization() throws Exception {
		
		Object deserializedObject = service.fromXml(LEGACY_PBO_NOTE_XML);
		
		assertNotNull(deserializedObject);
		assertTrue(deserializedObject instanceof PreNotesChangeBO);
		
		
	}
	
	/**
	 * Tests that an object that actually does have a boNotes list on it will unmarshall properly
	 * and not get it's note information thrown away.
	 */
	@Test
	public void testBOWithLegitNotes() throws Exception {
		Object deserializedObject = service.fromXml(BO_WITH_NOTES_XML);
		
		assertNotNull(deserializedObject);
		assertTrue(deserializedObject instanceof BOWithNotes);
		
		BOWithNotes boWithNotes = (BOWithNotes) deserializedObject;
		
		assertNotNull(boWithNotes.getBoNotes());
		assertEquals(1, boWithNotes.getBoNotes().size());
		
	}
	
	/**
	 * This class would have originally had a List of boNotes on it.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	public static class PreNotesChangeBO extends PersistableBusinessObjectBase {
		
		@Override
		protected LinkedHashMap<String, Object> toStringMapper() {
			return new LinkedHashMap<String, Object>();
		}
		
	}
	
	public static class BOWithNotes extends PersistableBusinessObjectBase {
		
		private List<Note> boNotes;
		
		public List<Note> getBoNotes() {
			return this.boNotes;
		}

		public void setBoNotes(List<Note> boNotes) {
			this.boNotes = boNotes;
		}

		@Override
		protected LinkedHashMap<String, Object> toStringMapper() {
			return new LinkedHashMap<String, Object>();
		}
		
	}
		
	private static class MockPersistenceService implements PersistenceService {

		@Override
		public boolean allForeignKeyValuesPopulatedForReference(PersistableBusinessObject bo, String referenceName) {
			return false;
		}

		@Override
		public void clearCache() {
		}

		@Override
		public String getFlattenedPrimaryKeyFieldValues(Object persistableObject) {
			return null;
		}

		@Override
		public Map getPrimaryKeyFieldValues(Object persistableObject, boolean sortFieldNames) {
			return null;
		}

		@Override
		public Map getPrimaryKeyFieldValues(Object persistableObject) {
			return null;
		}

		@Override
		public boolean isJpaEnabledForKnsClass(Class clazz) {
			return false;
		}

		@Override
		public boolean isProxied(Object object) {
			return false;
		}

		@Override
		public void linkObjects(Object persistableObject) {
		}

		@Override
		public void loadRepositoryDescriptor(String ojbRepositoryFilePath) {
		}

		@Override
		public void refreshAllNonUpdatingReferences(PersistableBusinessObject bo) {
		}

		@Override
		public Object resolveProxy(Object o) {
			return null;
		}

		@Override
		public void retrieveNonKeyFields(Object persistableObject) {
		}

		@Override
		public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
		}

		@Override
		public void retrieveReferenceObjects(List persistableObjects, List referenceObjectNames) {
		}

		@Override
		public void retrieveReferenceObjects(Object persistableObject, List referenceObjectNames) {
		}
		
	}
	
}
