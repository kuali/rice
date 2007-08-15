/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.RiceConstants;
import org.kuali.core.bo.Attachment;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.AttachmentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.Guid;
import org.springframework.transaction.annotation.Transactional;

/**
 * Attachment service implementation
 */
@Transactional
public class AttachmentServiceImpl implements AttachmentService {
    private static Logger LOG = Logger.getLogger(AttachmentServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.core.service.DocumentAttachmentService#createAttachment(java.lang.String, java.lang.String, int,
     *      java.io.InputStream, Document)
     */
    public Attachment createAttachment(PersistableBusinessObject parent, String uploadedFileName, String mimeType, int fileSize, InputStream fileContents, String attachmentTypeCode) throws IOException {
        LOG.debug("starting to create attachment for document: " + parent.getObjectId());
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

        String uniqueFileNameGuid = new Guid().toString();
        String fullPathUniqueFileName = getDocumentDirectory(parent.getObjectId()) + File.separator + uniqueFileNameGuid;

        writeInputStreamToFileStorage(fileContents, fullPathUniqueFileName);

        // create DocumentAttachment
        Attachment attachment = new Attachment();
        attachment.setAttachmentIdentifier(uniqueFileNameGuid);
        attachment.setAttachmentFileName(uploadedFileName);
        attachment.setAttachmentFileSize(new Long(fileSize));
        attachment.setAttachmentMimeTypeCode(mimeType);
        attachment.setAttachmentTypeCode(attachmentTypeCode);
        
        LOG.debug("finished creating attachment for document: " + parent.getObjectId());
        return attachment;
    }

    private void writeInputStreamToFileStorage(InputStream fileContents, String fullPathUniqueFileName) throws IOException {
        File fileOut = new File(fullPathUniqueFileName);
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(fileOut);
            bufferedStreamOut = new BufferedOutputStream(streamOut);
            int c;
            while ((c = fileContents.read()) != -1) {
                bufferedStreamOut.write(c);
            }
        }
        finally {
            bufferedStreamOut.close();
            streamOut.close();
        }
    }
    
    public void moveAttachmentsWherePending(List notes, String objectId) {
        for (Object obj : notes) {
            Note note = (Note)obj;
            Attachment attachment = note.getAttachment();
            if(attachment!=null){
                try {
                    moveAttachmentFromPending(attachment, objectId);
                }
                catch (IOException e) {
                    throw new RuntimeException("Problem moving pending attachment to final directory");
                    
                }
            }
        }
    }
    
    private void moveAttachmentFromPending(Attachment attachment, String objectId) throws IOException {
        //This method would probably be more efficient if attachments had a pending flag
        String fullPendingFileName = getPendingDirectory() + File.separator + attachment.getAttachmentIdentifier();
        File pendingFile = new File(fullPendingFileName);
        
        if(pendingFile.exists()) {
            BufferedInputStream bufferedStream = null;
            FileInputStream oldFileStream = null;
            String fullPathNewFile = getDocumentDirectory(objectId) + File.separator + attachment.getAttachmentIdentifier();
            try {
                oldFileStream = new FileInputStream(pendingFile);
                bufferedStream = new BufferedInputStream(oldFileStream);
                writeInputStreamToFileStorage(bufferedStream,fullPathNewFile);
            }
            finally {

                bufferedStream.close();
                oldFileStream.close();
                //this has to come after the close
                pendingFile.delete();
                
            }
        }
        
    }

    public void deleteAttachmentContents(Attachment attachment) {
        String fullPathUniqueFileName = getDocumentDirectory(attachment.getNote().getRemoteObjectIdentifier()) + File.separator + attachment.getAttachmentIdentifier();
        File attachmentFile = new File(fullPathUniqueFileName);
        attachmentFile.delete();
    }
    private String getPendingDirectory() {
        return this.getDocumentDirectory("");
    }

    private String getDocumentDirectory(String objectId) {
        // Create a directory; all ancestor directories must exist
        File documentDirectory = new File(getDocumentFileStorageLocation(objectId));
        if (!documentDirectory.exists()) {
            boolean success = documentDirectory.mkdir();
            if (!success) {
                throw new RuntimeException("Could not generate directory for File at: " + documentDirectory.getAbsolutePath());
            }
        }
        return documentDirectory.getAbsolutePath();
    }

    /**
     * /* (non-Javadoc)
     *
     * @see org.kuali.core.service.DocumentAttachmentService#retrieveAttachmentContents(org.kuali.core.document.DocumentAttachment)
     */
    public InputStream retrieveAttachmentContents(Attachment attachment) throws IOException {
        //refresh to get Note object in case it's not there
        if(attachment.getNoteIdentifier()!=null) {
            attachment.refreshNonUpdateableReferences();
        }
        
        String parentDirectory = "";
        if(attachment.getNote()!=null) {
            parentDirectory = attachment.getNote().getRemoteObjectIdentifier(); 
        }
         
        return new BufferedInputStream(new FileInputStream(getDocumentDirectory(parentDirectory) + File.separator + attachment.getAttachmentIdentifier()));
    }

    private String getDocumentFileStorageLocation(String objectId) {
        String location = null;
        if(StringUtils.isEmpty(objectId)) {
            location = kualiConfigurationService.getPropertyString(RiceConstants.ATTACHMENTS_PENDING_DIRECTORY_KEY);
        } else {
            location = kualiConfigurationService.getPropertyString(RiceConstants.ATTACHMENTS_DIRECTORY_KEY)+ File.separator + objectId;
        }
        return  location;
    }

    /**
     * @see org.kuali.core.service.AttachmentService#deletePendingAttachmentsModifiedBefore(long)
     */
    public void deletePendingAttachmentsModifiedBefore(long modificationTime) {
        String pendingAttachmentDirName = getPendingDirectory();
        if (StringUtils.isBlank(pendingAttachmentDirName)) {
            throw new RuntimeException("Blank pending attachment directory name");
        }
        File pendingAttachmentDir = new File(pendingAttachmentDirName);
        if (!pendingAttachmentDir.exists()) {
            throw new RuntimeException("Pending attachment directory does not exist");
        }
        if (!pendingAttachmentDir.isDirectory()) {
            throw new RuntimeException("Pending attachment directory is not a directory! " + pendingAttachmentDir.getAbsolutePath());
        }
        
        File[] files = pendingAttachmentDir.listFiles();
        for (File file : files) {
            if (!file.getName().equals("placeholder.txt")) {
                if (file.lastModified() < modificationTime) {
                    file.delete();
                }
            }
        }
        
    }

    /**
     * Gets the configService attribute. 
     * @return Returns the configService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService configService) {
        this.kualiConfigurationService = configService;
    }
}
