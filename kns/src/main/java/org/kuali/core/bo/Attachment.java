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
package org.kuali.core.bo;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.KNSServiceLocator;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Attachment extends PersistableBusinessObjectBase {

	private Long noteIdentifier;
	private String attachmentMimeTypeCode;
	private String attachmentFileName;
	private String attachmentIdentifier;
	private Long attachmentFileSize;
	private String attachmentTypeCode;

    private Note note;

	/**
	 * Default constructor.
	 */
	public Attachment() {

	}

	/**
	 * Gets the noteIdentifier attribute.
	 *
	 * @return Returns the noteIdentifier
	 *
	 */
	public Long getNoteIdentifier() {
		return noteIdentifier;
	}

	/**
	 * Sets the noteIdentifier attribute.
	 *
	 * @param noteIdentifier The noteIdentifier to set.
	 *
	 */
	public void setNoteIdentifier(Long noteIdentifier) {
		this.noteIdentifier = noteIdentifier;
	}


	/**
	 * Gets the attachmentMimeTypeCode attribute.
	 *
	 * @return Returns the attachmentMimeTypeCode
	 *
	 */
	public String getAttachmentMimeTypeCode() {
		return attachmentMimeTypeCode;
	}

	/**
	 * Sets the attachmentMimeTypeCode attribute.
	 *
	 * @param attachmentMimeTypeCode The attachmentMimeTypeCode to set.
	 *
	 */
	public void setAttachmentMimeTypeCode(String attachmentMimeTypeCode) {
		this.attachmentMimeTypeCode = attachmentMimeTypeCode;
	}


	/**
	 * Gets the attachmentFileName attribute.
	 *
	 * @return Returns the attachmentFileName
	 *
	 */
	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	/**
	 * Sets the attachmentFileName attribute.
	 *
	 * @param attachmentFileName The attachmentFileName to set.
	 *
	 */
	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}


	/**
	 * Gets the attachmentIdentifier attribute.
	 *
	 * @return Returns the attachmentIdentifier
	 *
	 */
	public String getAttachmentIdentifier() {
		return attachmentIdentifier;
	}

	/**
	 * Sets the attachmentIdentifier attribute.
	 *
	 * @param attachmentIdentifier The attachmentIdentifier to set.
	 *
	 */
	public void setAttachmentIdentifier(String attachmentIdentifier) {
		this.attachmentIdentifier = attachmentIdentifier;
	}


	/**
	 * Gets the attachmentFileSize attribute.
	 *
	 * @return Returns the attachmentFileSize
	 *
	 */
	public Long getAttachmentFileSize() {
		return attachmentFileSize;
	}

	/**
	 * Sets the attachmentFileSize attribute.
	 *
	 * @param attachmentFileSize The attachmentFileSize to set.
	 *
	 */
	public void setAttachmentFileSize(Long attachmentFileSize) {
		this.attachmentFileSize = attachmentFileSize;
	}


	/**
	 * Gets the attachmentTypeCode attribute.
	 *
	 * @return Returns the attachmentTypeCode
	 *
	 */
	public String getAttachmentTypeCode() {
		return attachmentTypeCode;
	}

	/**
	 * Sets the attachmentTypeCode attribute.
	 *
	 * @param attachmentTypeCode The attachmentTypeCode to set.
	 *
	 */
	public void setAttachmentTypeCode(String attachmentTypeCode) {
		this.attachmentTypeCode = attachmentTypeCode;
	}

    /**
     * Gets the note attribute.
     * @return Returns the note.
     */
    public Note getNote() {
        return note;
    }

    /**
     * Sets the note attribute value.
     * @param note The note to set.
     */
    public void setNote(Note note) {
        this.note = note;
    }
    /**
     * @return false if any of the required fields (attachmentId, fileName, fileSize, and mimeType) are blank
     */
    public boolean isComplete() {
        return (StringUtils.isNotBlank(attachmentIdentifier) && StringUtils.isNotBlank(attachmentFileName) && (attachmentFileSize != null) && StringUtils.isNotBlank(attachmentMimeTypeCode));
    }

    /**
     * (non-Javadoc)
     *
     * @see org.kuali.core.service.DocumentAttachmentService#retrieveAttachmentContents(org.kuali.core.document.DocumentAttachment)
     */
    public InputStream getAttachmentContents() throws IOException {
        return KNSServiceLocator.getAttachmentService().retrieveAttachmentContents(this);
    }
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("noteIdentifier", this.noteIdentifier);
        return m;
    }
}
