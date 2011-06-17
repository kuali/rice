/*
 * Copyright 2006-2011 The Kuali Foundation
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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.test.KRADTestCase;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import static org.junit.Assert.*;


/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttachmentTest extends KRADTestCase {
	
	Attachment dummyAttachment;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyAttachment = new Attachment();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyAttachment = null;
	}

	@Test
	public void testNoteIdentifier(){
		dummyAttachment.setNoteIdentifier((long)12345);
		assertTrue("Testing NoteIdentifier of Attachment in AttachmentTest",12345 == dummyAttachment.getNoteIdentifier());
	}
	
	@Test
	public void testAttachmentMimeTypeCode(){
		dummyAttachment.setAttachmentMimeTypeCode("MIME_TYP");
		assertEquals("Testing AttachmentmimeTypeCode of Attachment in AttachmentTest","MIME_TYP", dummyAttachment.getAttachmentMimeTypeCode());
	}
	
	@Test
	public void testAttachmentFileName(){
		dummyAttachment.setAttachmentFileName("FILE_NM");
		assertEquals("Testing AttchmentFileName of Attachment in AttachmentTest","FILE_NM", dummyAttachment.getAttachmentFileName());
	}
	
	@Test
	public void testAttachmentIdentifier(){
		dummyAttachment.setAttachmentIdentifier("Att_ID");
		assertEquals("Testing Attachment in AttachmentTest","Att_ID", dummyAttachment.getAttachmentIdentifier());
	}
	
	@Test
	public void testAttachmentFileSize(){
		dummyAttachment.setAttachmentFileSize((long)12345);
		assertTrue("Testing AttachmentFileSize of Attachment in AttachmentTest",12345 == dummyAttachment.getAttachmentFileSize());
	}
	

	@Test
	public void testAttachmentTypeCode(){
		dummyAttachment.setAttachmentTypeCode("ATT_TYP_CD");
		assertEquals("Testing AttachmentmimeTypeCode of Attachment in AttachmentTest","ATT_TYP_CD", dummyAttachment.getAttachmentTypeCode());
	}
	

	@Test
	public void testNote(){
		Note dummyNote = new Note();
		dummyNote.setNoteText("Hello");
		dummyAttachment.setNote(dummyNote);
		assertEquals("Testing Note of Attachment in AttachmentTest","Hello", dummyAttachment.getNote().getNoteText());
	}
	
	@Test
	public void testComplete(){
	
		dummyAttachment.setAttachmentIdentifier("Att_ID");
		dummyAttachment.setAttachmentFileName("FILE_NM");
		dummyAttachment.setAttachmentFileSize(new Long(12345));
		dummyAttachment.setAttachmentMimeTypeCode("MIME_TYP");
		assertTrue("Testing Complete of Attachment in AttachmentTest",dummyAttachment.isComplete());
		{
			dummyAttachment.setAttachmentFileName(null);
			assertFalse("Testing Complete of Attachment in AttachmentTest",dummyAttachment.isComplete());
	
		}
		{
			dummyAttachment.setAttachmentFileSize((long)0);
			assertFalse("Testing Complete of Attachment in AttachmentTest",dummyAttachment.isComplete());
		}
		{
			dummyAttachment.setAttachmentIdentifier(null);
			assertFalse("Testing Complete of Attachment in AttachmentTest",dummyAttachment.isComplete());
		}
		{
			dummyAttachment.setAttachmentMimeTypeCode(null);
			assertFalse("Testing Complete of Attachment in AttachmentTest",dummyAttachment.isComplete());
		}
		
	}
	
	
	@Test
	public void testAttachmentContents() throws Exception {
		
		
		try{
			 
			FileWriter out = new FileWriter("dummy.txt");
			out.write("Hello testAttachmentContent");
			out.close();
						
			File dummyFile = new File("dummy.txt");  
			Note dummyNote = new Note();
			InputStream inStream = new FileInputStream("dummy.txt");
		
			GlobalVariables.setUserSession(new UserSession("quickstart"));
			
	        Person kualiUser = GlobalVariables.getUserSession().getPerson();
			PersistableBusinessObject parentNote = KRADServiceLocator.getNoteService().createNote(dummyNote, dummyAttachment, kualiUser.getPrincipalId());
			dummyAttachment = KRADServiceLocator.getAttachmentService().createAttachment( parentNote,
																					   	 "dummy.txt", 
																					     "MimeTypeCode",
																					     (int) (long) dummyFile.length(), 
																					     inStream,
																					     "AttachmentTypeCode");
			String result ="";
			DataInputStream in =  new DataInputStream(dummyAttachment.getAttachmentContents());
		
			while (in.available() != 0) {
				   result += in.readLine();
			}
			inStream.close();
			assertEquals("Testing attachmentContents in AttachmentTest","Hello testAttachmentContent",result );
		}
		finally{
			new File("dummy.txt").delete();
		}
	}
}
