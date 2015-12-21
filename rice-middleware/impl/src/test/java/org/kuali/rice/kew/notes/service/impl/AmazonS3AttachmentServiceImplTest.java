package org.kuali.rice.kew.notes.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.kew.notes.Attachment;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

@RunWith(MockitoJUnitRunner.class)
public class AmazonS3AttachmentServiceImplTest {
	
	private String bucketName = "bucket";
	private String folderName = "folder";
	
	@Mock
	private ResourceLoader resourceLoader;
	@Mock
	private AmazonS3 amazonS3;
	
	@InjectMocks
	private AmazonS3AttachmentServiceImpl attachmentService;

	
	@Before
	public void setUp() throws Exception {
		attachmentService.setBucketName(this.bucketName);
		attachmentService.setFolderName(this.folderName);
		attachmentService.afterPropertiesSet();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSet_Empty() throws Exception {
		new AmazonS3AttachmentServiceImpl().afterPropertiesSet();		
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSet_NoBucket() throws Exception {
		AmazonS3AttachmentServiceImpl service = new AmazonS3AttachmentServiceImpl();
		service.setFolderName("folder");
		service.afterPropertiesSet();
	}

	@Test(expected = IllegalStateException.class)
	public void testAfterPropertiesSet_NoFolder() throws Exception {
		AmazonS3AttachmentServiceImpl service = new AmazonS3AttachmentServiceImpl();
		service.setBucketName("bucket");
		service.afterPropertiesSet();
	}
	
	@Test
	public void testPersistAttachedFileAndSetAttachmentBusinessObjectValue() throws Exception {
		
		File tmpOutFile = null;
		File tmpInFile = null;
		
		try {
		
			String attachmentId = UUID.randomUUID().toString();
			String fileName = "file1.txt";
			Attachment attachment = new Attachment();
			attachment.setAttachmentId(attachmentId);
			attachment.setFileName(fileName);
		
			// set up the target resource for the resource, we aren't actually using S3 here, so let's use a temp file
			tmpOutFile = File.createTempFile("output_" + getClass().getName(), null);
			String amazonUrl = "s3://bucket/folder/" + attachmentId;
			FileSystemResource resource = new FileSystemResource(tmpOutFile);
			when(resourceLoader.getResource(amazonUrl)).thenReturn(resource);
				
			// set up the attachment object
			String fileContent = "hello";
			tmpInFile = File.createTempFile("input_" + getClass().getName(), null);
			FileWriter writer = new FileWriter(tmpInFile);
			writer.write(fileContent);
			writer.close();
			attachment.setAttachedObject(new FileInputStream(tmpInFile));
		
			attachmentService.persistAttachedFileAndSetAttachmentBusinessObjectValue(attachment);
		
			// now the url on the attachment should be our amazon url
			assertEquals(amazonUrl, attachment.getFileLoc());
			// and the content on our output file should be "hello"
			assertEquals(5, tmpOutFile.length());
			FileReader reader = new FileReader(tmpOutFile);
			char[] charBuffer = new char[5];
			reader.read(charBuffer, 0, 5);
			String outputFileContent = new String(charBuffer);
			assertEquals(fileContent, outputFileContent);
			reader.close();
		} finally {
			if (tmpOutFile != null) {
				tmpOutFile.delete();
			}
			if (tmpInFile != null) {
				tmpInFile.delete();
			}
		}
		
	}
	
	@Test
	public void testFindAttachedFile() throws Exception {
		
		// create the attachment object to point at an S3-style url
		String attachmentId = UUID.randomUUID().toString();
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + attachmentId;
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		attachment.setAttachmentId(attachmentId);
		
		// now set up our resource loader so it will resolve
		File tmpFile = File.createTempFile("testFindAttachedFile_" + getClass().getName(), null);
		assertTrue(tmpFile.exists());		
		when(resourceLoader.getResource(fileUrl)).thenReturn(new FileSystemResource(tmpFile));
		
		try {		
			File attachedFile = attachmentService.findAttachedFile(attachment);
			assertEquals(tmpFile.getAbsolutePath(), attachedFile.getAbsolutePath());			
		} finally {
			tmpFile.delete();
		}
	}
	
	@Test
	public void testFindAttachedResource() throws Exception {
		// create the attachment object to point at an S3-style url
		String attachmentId = UUID.randomUUID().toString();
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + attachmentId;
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		attachment.setAttachmentId(attachmentId);
		
		// now set up our resource loader so it will resolve
		File tmpFile = File.createTempFile("testFindAttachedResource_" + getClass().getName(), null);
		assertTrue(tmpFile.exists());		
		FileWriter writer = new FileWriter(tmpFile);
		String fileContent = "testFindAttachedResource";
		writer.write(fileContent);
		writer.close();
		when(resourceLoader.getResource(fileUrl)).thenReturn(new FileSystemResource(tmpFile));
		
		try {		
			Resource attachedResource = attachmentService.findAttachedResource(attachment);
			// we'll check the resource content to make sure it matches
			InputStreamReader reader = new InputStreamReader(attachedResource.getInputStream());
			char[] charbuf = new char[fileContent.length()];
			reader.read(charbuf, 0, fileContent.length());
			reader.close();
			// the content should be the same
			assertEquals(fileContent, new String(charbuf));
		} finally {
			tmpFile.delete();
		}

	}
	
	@Test public void testDeleteAttachedFile() throws Exception {

		// create the attachment object to point at an S3-style url
		final String attachmentId = UUID.randomUUID().toString();
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + attachmentId;
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		attachment.setAttachmentId(attachmentId);
		
		// execute the deletion
		attachmentService.deleteAttachedFile(attachment);
		
		// verify that the AmazonS3 api gets invoked
		verify(amazonS3).deleteObject(argThat(new ArgumentMatcher<DeleteObjectRequest>() {
			public boolean matches(Object deleteObjectRequest) {
				DeleteObjectRequest request = (DeleteObjectRequest)deleteObjectRequest;
				return bucketName.equals(request.getBucketName()) && request.getKey().equals(folderName + "/" + attachmentId);
		    }
		}));
	}

	
}
