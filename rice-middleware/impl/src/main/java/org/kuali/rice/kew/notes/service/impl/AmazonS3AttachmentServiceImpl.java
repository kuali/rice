package org.kuali.rice.kew.notes.service.impl;

import java.io.File;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.service.AttachmentService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

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
public class AmazonS3AttachmentServiceImpl implements AttachmentService, InitializingBean {

	private ResourceLoader resourceLoader;
	private String bucketName;
	private String folderName;
	private AmazonS3 amazonS3;
	
	@Override
	public void afterPropertiesSet() {
		if (StringUtils.isBlank(folderName)) {
			throw new IllegalStateException("S3 attachment service must be configured with a non-blank folder name");
		}
		if (StringUtils.isBlank(bucketName)) {
			throw new IllegalStateException("S3 attachment service must be configured with a non-blank bucket name");
		}		
	}

	@Override
	public void persistAttachedFileAndSetAttachmentBusinessObjectValue(Attachment attachment) throws Exception {
		if (attachment.getFileLoc() == null) {
			String s3Url = generateS3Url(attachment);
			attachment.setFileLoc(s3Url);
		}
		TransferManager manager = new TransferManager(this.amazonS3);
		ObjectMetadata metadata = new ObjectMetadata();
		if (attachment.getMimeType() != null) {
			metadata.setContentType(attachment.getMimeType());
		}
		if (attachment.getFileName() != null) {
			metadata.setContentDisposition("attachment; filename=" + URLEncoder.encode(attachment.getFileName(), "UTF-8"));
		}
		Upload upload = manager.upload(this.bucketName, parseObjectKey(attachment.getFileLoc()), attachment.getAttachedObject(), metadata);
		upload.waitForCompletion();
	}

	@Override
	public File findAttachedFile(Attachment attachment) throws Exception {
		throw new UnsupportedOperationException("S3 Attachment Service implementation cannot provide a file, please you \"findAttachedResource\" instead.");
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
		amazonS3.deleteObject(new DeleteObjectRequest(this.bucketName, parseObjectKey(attachment.getFileLoc())));
	}
		
	private String generateS3Url(Attachment attachment) {
		return generateS3Prefix() + folderName + "/" + UUID.randomUUID();		
	}
	
	private String generateS3Prefix() {
		return "s3://" + this.bucketName + "/";
	}
	
	private String parseObjectKey(String s3Url) {
		String prefix = generateS3Prefix();
		String objectKey = s3Url.substring(prefix.length());
		return objectKey;
	}
	
	@Required
	@Autowired
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Required
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	@Required
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	@Required
	@Autowired
	public void setAmazonS3(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

}
