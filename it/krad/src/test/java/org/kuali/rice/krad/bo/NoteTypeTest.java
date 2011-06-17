/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.krad.bo;


import org.junit.Test;
import org.kuali.test.KRADTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NoteTypeTest extends KRADTestCase {

	NoteType dummyNoteType;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyNoteType = new NoteType();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyNoteType = null;
	}
	
	@Test
	public void testNoteTypeCode(){
		dummyNoteType.setNoteTypeCode("plain");
		assertEquals("Testing NoteTypeCode in NoteTypeTest","plain",dummyNoteType.getNoteTypeCode());
	}
	
	@Test
	public void testNoteTypeDescription(){
		dummyNoteType.setNoteTypeDescription("This note is plain");
		assertEquals("Testing NoteTypeDescription in NoteTypeTest","This note is plain",dummyNoteType.getNoteTypeDescription());
	}
	
	@Test
	public void testNoteTypeActiveIndicator(){
		dummyNoteType.setNoteTypeActiveIndicator(true);
		assertTrue("Testing setNoteTypeActiveIndicator in NoteTypeTest",dummyNoteType.isNoteTypeActiveIndicator());
	}
}
