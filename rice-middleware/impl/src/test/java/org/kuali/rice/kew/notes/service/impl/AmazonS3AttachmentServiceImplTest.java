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
package org.kuali.rice.kew.notes.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
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
	
	public void testPersistAttachedFileAndSetAttachmentBusinessObjectValue() throws Exception {	
		// unfortunately we can't unit test this method given that the method internally uses an Amazon S3 TransferManager 			
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testFindAttachedFile() throws Exception {
		
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + UUID.randomUUID().toString();
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		attachmentService.findAttachedFile(attachment);
				
	}
	
	@Test
	public void testFindAttachedResource() throws Exception {
		// create the attachment object to point at an S3-style url
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + UUID.randomUUID().toString();
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		
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
		final String generatedObjectKey = UUID.randomUUID().toString();
		String fileUrl = "s3://" + bucketName + "/" + folderName + "/" + generatedObjectKey;
		Attachment attachment = new Attachment();		
		attachment.setFileLoc(fileUrl);
		
		// execute the deletion
		attachmentService.deleteAttachedFile(attachment);
		
		// verify that the AmazonS3 api gets invoked
		verify(amazonS3).deleteObject(argThat(new ArgumentMatcher<DeleteObjectRequest>() {
			public boolean matches(Object deleteObjectRequest) {
				DeleteObjectRequest request = (DeleteObjectRequest)deleteObjectRequest;
				return bucketName.equals(request.getBucketName()) && request.getKey().equals(folderName + "/" + generatedObjectKey);
		    }
		}));
	}

	
}
