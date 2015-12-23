package org.kuali.rice.kew.notes.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;

import org.junit.Test;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.springframework.core.io.Resource;

/**
 * In order for this test to run successfully, please ensure that the 's3' profile is passed to spring.
 * 
 * <p>This can be accomplished by passing the following to JVM startup: -Dspring.profiles.active=s3</p>
 */
public class AmazonS3AttachmentServiceTest extends KEWTestCase {
	
	@Test
	public void testSaveAttachments() throws Exception {
		File file = null;
		
		try {
			Note note = new Note();
			note.setDocumentId("12345");
			String ewestfalId = getPrincipalIdForName("ewestfal");
			note.setNoteAuthorWorkflowId(ewestfalId);
			note.setNoteCreateDate(new Timestamp(System.currentTimeMillis()));
			note.setNoteText("This is my note");
		
			// create file1.txt
			file = File.createTempFile("file", "txt");
			FileWriter writer = new FileWriter(file);
			String fileContent = "This is the file text";
			writer.write(fileContent);
			writer.close();		
		
			Attachment attachment = new Attachment();
			attachment.setFileName("file.txt");
			attachment.setMimeType("text/plain");
			attachment.setAttachedObject(new FileInputStream(file));
			attachment.setNote(note);
			note.getAttachments().add(attachment);			
				
			// now let's save the note, it should cascade the save to our attachment
			note = getNoteService().saveNote(note);
						
			Attachment savedAttachment = note.getAttachments().get(0);
			assertTrue(savedAttachment.getFileLoc().startsWith("s3://"));
			assertNotNull(savedAttachment.getAttachmentId());
			
			// the Note api is pretty awful, we will need to reload the attachment resource and reset it on our attachment in order to be
			// able to read it again
			Attachment loadedAttachment = getNoteService().findAttachment(savedAttachment.getAttachmentId());
			loadedAttachment.setAttachedObject(getNoteService().findAttachmentResource(loadedAttachment).getInputStream());			
			
			// read in the file content and let's make sure it matches
			InputStreamReader reader = new InputStreamReader(loadedAttachment.getAttachedObject());
			char[] cbuf = new char[fileContent.length()];
			reader.read(cbuf, 0, fileContent.length());
			assertEquals(fileContent, new String(cbuf));
			
			// now let's delete that old attachment
			Resource attachmentResource = getNoteService().findAttachmentResource(loadedAttachment);
			assertTrue("attachment resource should exist", attachmentResource.exists());
			getNoteService().deleteAttachment(loadedAttachment);
			attachmentResource = getNoteService().findAttachmentResource(loadedAttachment);
			assertFalse("attachment resource has been deleted so should no longer exist", attachmentResource.exists());
			
		} finally {
			file.delete();
		}
					
	}
	
	/**
	 * We use attachments via notes.
	 */
	private NoteService getNoteService() {
		return KEWServiceLocator.getNoteService();
	}
	
}
