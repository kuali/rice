package org.kuali.rice.kew.notes.service.impl;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.service.AttachmentService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

/**
 * An {@link AttachmentService} implementation which utilizes AWS Simple Storage Service.
 * 
 * <p>Must inject a ResourceLoader that is capable of resolving "s3://" urls as well as the name of the bucket this
 * service should use. Also an instance of the AmazonS3 client api should be injected.
 * 
 * The generated id of the attachment will be used as the object key is S3, so the bucket should exist only for storing
 * the attachments managed by this service.
 * 
 */
public class AmazonS3AttachmentServiceImpl implements AttachmentService {

	private ResourceLoader resourceLoader;
	private String bucketName;
	private AmazonS3 amazonS3;
	
	@Override
	public void persistAttachedFileAndSetAttachmentBusinessObjectValue(Attachment attachment) throws Exception {
		attachment.setFileLoc(s3Url(attachment));		
		WritableResource resource = (WritableResource)findAttachedResource(attachment);
		IOUtils.copy(attachment.getAttachedObject(), resource.getOutputStream());		
	}

	@Override
	public File findAttachedFile(Attachment attachment) throws Exception {
		return findAttachedResource(attachment).getFile();
	}
	
	@Override
	public Resource findAttachedResource(Attachment attachment) {
		if (attachment == null) {
			throw new IllegalArgumentException("Given attachment was null");
		}
		if (StringUtils.isBlank(attachment.getFileLoc())) {
			throw new IllegalArgumentException("Given attachment has an empty file location");
		}
		return this.resourceLoader.getResource(attachment.getFileLoc());		
	}

	@Override
	public void deleteAttachedFile(Attachment attachment) throws Exception {
		amazonS3.deleteObject(new DeleteObjectRequest(this.bucketName, attachment.getAttachmentId()));
	}
		
	private String s3Url(Attachment attachment) {
		if (StringUtils.isBlank(this.bucketName)) {
			throw new NullPointerException("No bucket name available.");
		}
		if (StringUtils.isBlank(attachment.getAttachmentId())) {
			throw new IllegalArgumentException("Attachment id cannot be null.");
		}

		return "s3://" + this.bucketName + "/" + attachment.getAttachmentId();
	}

	@Required
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}


	@Required
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	@Required
	public void setAmazonS3(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

}
