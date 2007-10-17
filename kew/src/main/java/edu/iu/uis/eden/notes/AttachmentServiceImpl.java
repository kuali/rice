/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.notes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * Implementation of the {@link AttachmentService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AttachmentServiceImpl implements AttachmentService {
	
	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttachmentServiceImpl.class);
	
	private static final String ATTACHMENT_PREPEND = "wf_att_";
	
	private String attachmentDir;

	public void persistAttachedFileAndSetAttachmentBusinessObjectValue(Attachment attachment) throws Exception {
		createStorageDirIfNecessary();
		Long uniqueId = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
		String internalFileIndicator = attachment.getFileName().replace('.', '_');
		String fileName = ATTACHMENT_PREPEND + attachment.getNote().getRouteHeaderId() + "_" + internalFileIndicator + "_" + uniqueId;
		
		File file = File.createTempFile(fileName, null, new File(attachmentDir));
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(file);
            bufferedStreamOut = new BufferedOutputStream(streamOut);
            int c;
            while ((c = attachment.getAttachedObject().read()) != -1) 
                {
                    bufferedStreamOut.write(c);
                }
        } finally {
        	if (bufferedStreamOut != null) {
        		bufferedStreamOut.close();
        	}
            if (streamOut != null) {
            	streamOut.close();
            }
        }
        attachment.setFileLoc(file.getAbsolutePath());
	}

	public File findAttachedFile(Attachment attachment) throws Exception {
		return new File(attachment.getFileLoc());
	}
	
	public void deleteAttachedFile(Attachment attachment) throws Exception {
		File file = new File(attachment.getFileLoc());
		if (! file.delete()) {
			LOG.error("failed to delete file " + attachment.getFileLoc());
		}
	}
	
	private void createStorageDirIfNecessary() {
		if (attachmentDir == null) {
			throw new RuntimeException("Attachment Directory was not set when configuring workflow");
		}
		File attachDir = new File(attachmentDir);
		if (! attachDir.exists()) {
			LOG.warn("No attachment directory found.  Attempting to create directory " + attachmentDir);
			attachDir.mkdirs();
		}
	}

	public String getAttachmentDir() {
		return attachmentDir;
	}

	public void setAttachmentDir(String attachmentDir) {
		this.attachmentDir = attachmentDir;
	}
	
}
