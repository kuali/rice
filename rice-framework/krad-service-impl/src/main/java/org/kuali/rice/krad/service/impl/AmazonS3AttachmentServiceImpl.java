/**
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.AttachmentService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

/**
 * An {@link AttachmentService} implementation which utilizes AWS Simple Storage Service.
 * 
 * <p>Must inject a ResourceLoader that is capable of resolving "s3://" urls as well as the name of the bucket this
 * service should use. Also an instance of the AmazonS3 client api should be injected.
 * 
 * The generated id of the attachment will be used as the object key in S3, so the bucket should exist only for storing
 * the attachments managed by this service.
 * 
 */
@Transactional
public class AmazonS3AttachmentServiceImpl implements AttachmentService {

	private static final Logger LOG = Logger.getLogger(AmazonS3AttachmentServiceImpl.class);

	private ResourceLoader resourceLoader;
	private String bucketName;
	private AmazonS3 amazonS3;
    protected DataObjectService dataObjectService;

    /**
     * Retrieves an Attachment by note identifier.
     *
     * @see org.kuali.rice.krad.service.AttachmentService#getAttachmentByNoteId(java.lang.Long)
     */
    @Override
	public Attachment getAttachmentByNoteId(Long noteId) {
        if(noteId == null){
            return null;
        }
        return dataObjectService.find(Attachment.class, noteId);
	}

    /**
     * @see org.kuali.rice.krad.service.AttachmentService#createAttachment(GloballyUnique,
     * String, String, int, java.io.InputStream, String)
     */
    @Override
	public Attachment createAttachment(GloballyUnique parent, String uploadedFileName, String mimeType, int fileSize, InputStream fileContents, String attachmentTypeCode) throws IOException {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("starting to create attachment for document: " + parent.getObjectId());
        }
        if (parent == null) {
            throw new IllegalArgumentException("invalid (null or uninitialized) document");
        }
        if (StringUtils.isBlank(uploadedFileName)) {
            throw new IllegalArgumentException("invalid (blank) fileName");
        }
        if (StringUtils.isBlank(mimeType)) {
            throw new IllegalArgumentException("invalid (blank) mimeType");
        }
        if (fileSize <= 0) {
            throw new IllegalArgumentException("invalid (non-positive) fileSize");
        }
        if (fileContents == null) {
            throw new IllegalArgumentException("invalid (null) inputStream");
        }
        
        String uniqueFileNameGuid = UUID.randomUUID().toString();
        
		String url = s3Url(uniqueFileNameGuid);		
		WritableResource resource = findAttachmentResource(url);
		IOUtils.copy(fileContents, resource.getOutputStream());		

        // create DocumentAttachment
        Attachment attachment = new Attachment();
        attachment.setAttachmentIdentifier(uniqueFileNameGuid);
        attachment.setAttachmentFileName(uploadedFileName);
        attachment.setAttachmentFileSize(new Long(fileSize));
        attachment.setAttachmentMimeTypeCode(mimeType);
        attachment.setAttachmentTypeCode(attachmentTypeCode);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("finished creating attachment for document: " + parent.getObjectId());
        }
        return attachment;
    }


    @Override
	public void moveAttachmentWherePending(Note note) {
    	throw new UnsupportedOperationException("S3 implementation of AttachmentService does not support moveAttachmentWherePending");
    }

    @Override
	public void deleteAttachmentContents(Attachment attachment) {
    	if (attachment.getNote() == null) {
    		throw new RuntimeException("Attachment.note must be set in order to delete the attachment");
    	}
    	amazonS3.deleteObject(new DeleteObjectRequest(this.bucketName, attachment.getAttachmentIdentifier()));
    }
    
    @Override
	public InputStream retrieveAttachmentContents(Attachment attachment) throws IOException {
    	return findAttachmentResource(s3Url(attachment.getAttachmentIdentifier())).getInputStream();
    }

    /**
     * @see org.kuali.rice.krad.service.AttachmentService#deletePendingAttachmentsModifiedBefore(long)
     */
    @Override
	public void deletePendingAttachmentsModifiedBefore(long modificationTime) {        
    	throw new UnsupportedOperationException("S3 implementation of AttachmentService does not support deletePendingAttachmentsModifiedBefore");
    }
    
    private String s3Url(String uniqueFileNameGuid) {
    	if (StringUtils.isBlank(this.bucketName)) {
			throw new NullPointerException("No bucket name available.");
		}
		if (StringUtils.isBlank(uniqueFileNameGuid)) {
			throw new IllegalArgumentException("GUID cannot be null.");
		}

		return "s3://" + this.bucketName + "/" + uniqueFileNameGuid;
    }
    
	private WritableResource findAttachmentResource(String url) {
		if (url == null) {
			throw new IllegalArgumentException("Given attachment url was null");
		}
		return (WritableResource)this.resourceLoader.getResource(url);		
	}	
	
    public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public void setAmazonS3(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	@Required
    public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}

}
