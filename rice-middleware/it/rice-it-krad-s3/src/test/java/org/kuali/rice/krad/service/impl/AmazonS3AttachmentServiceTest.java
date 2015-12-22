package org.kuali.rice.krad.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Test;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;

public class AmazonS3AttachmentServiceTest extends KRADTestCase {
	
	@Test
	public void testSaveAndDeleteAttachments() throws Exception {
		AttachmentService attachmentService = KRADServiceLocator.getAttachmentService();
		
		File file = null;
		
		try {
			GloballyUnique gu = new GloballyUnique() {
				private final String id = UUID.randomUUID().toString();
				public String getObjectId() {
					return this.id;
				}
			};
			
			// create file1.txt
			file = File.createTempFile("file", "txt");
			FileWriter writer = new FileWriter(file);
			String fileContent = "This is the file text";
			writer.write(fileContent);
			writer.close();		

			FileInputStream fileInputStream = new FileInputStream(file);
			
			Note note = new Note();
			Attachment attachment = attachmentService.createAttachment(gu, file.getName(), "plain/text", (int)file.length(), fileInputStream, "F");
			attachment.setNote(note);
			assertEquals(file.getName(), attachment.getAttachmentFileName());
			assertEquals("plain/text", attachment.getAttachmentMimeTypeCode());
			assertEquals(file.length(), attachment.getAttachmentFileSize().longValue());
			assertNotNull(attachment.getAttachmentIdentifier());
			
			// let's retrieve the attachment contents and make sure they match
			InputStream is = attachmentService.retrieveAttachmentContents(attachment);
			InputStreamReader reader = new InputStreamReader(is);
			char[] cbuf = new char[fileContent.length()];
			reader.read(cbuf, 0, fileContent.length());
			assertEquals(fileContent, new String(cbuf));
			reader.close();
			
			// now let's delete the attachment
			attachmentService.deleteAttachmentContents(attachment);
			
			// now if I try to read the attachment contents I should get an exception
			try {
				attachmentService.retrieveAttachmentContents(attachment);
				fail("Should have received an exception when attempting to retrieve attachment contents of a deleted attachment");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} finally {
			file.delete();
		}
	}
	
	
}
