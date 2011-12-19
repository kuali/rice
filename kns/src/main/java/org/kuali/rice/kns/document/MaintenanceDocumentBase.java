/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kns.document;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.bo.DocumentAttachment;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

import javax.persistence.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentBase extends org.kuali.rice.krad.maintenance.MaintenanceDocumentBase implements MaintenanceDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentBase.class);

    @Transient
    protected transient FormFile fileAttachment;

    public MaintenanceDocumentBase() {
        super();
    }

    public MaintenanceDocumentBase(String documentTypeName) {
        super(documentTypeName);
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#getDocumentBusinessObject()
     */
    @Override
    public PersistableBusinessObject getDocumentBusinessObject() {
        return (PersistableBusinessObject) super.getDocumentDataObject();
    }

    /**
     * Checks old maintainable bo has key values
     */
    public boolean isOldBusinessObjectInDocument() {
        boolean isOldBusinessObjectInExistence = false;
        if (getOldMaintainableObject() == null || getOldMaintainableObject().getBusinessObject() == null) {
            isOldBusinessObjectInExistence = false;
        } else {
            isOldBusinessObjectInExistence = getOldMaintainableObject().isOldBusinessObjectInDocument();
        }
        return isOldBusinessObjectInExistence;
    }

    public Maintainable getNewMaintainableObject() {
        return (Maintainable) newMaintainableObject;
    }

    public Maintainable getOldMaintainableObject() {
        return (Maintainable) oldMaintainableObject;
    }

    public FormFile getFileAttachment() {
        return this.fileAttachment;
    }

    public void setFileAttachment(FormFile fileAttachment) {
        this.fileAttachment = fileAttachment;
    }

    public void populateDocumentAttachment() {
        refreshAttachment();

        if (fileAttachment != null && StringUtils.isNotEmpty(fileAttachment.getFileName())) {
            //Populate DocumentAttachment BO
            if (attachment == null) {
                attachment = new DocumentAttachment();
            }

            byte[] fileContents;
            try {
                fileContents = fileAttachment.getFileData();
                if (fileContents.length > 0) {
                    attachment.setFileName(fileAttachment.getFileName());
                    attachment.setContentType(fileAttachment.getContentType());
                    attachment.setAttachmentContent(fileAttachment.getFileData());
                    attachment.setDocumentNumber(getDocumentNumber());
                }
            } catch (FileNotFoundException e) {
                LOG.error("Error while populating the Document Attachment", e);
                throw new RuntimeException("Could not populate DocumentAttachment object", e);
            } catch (IOException e) {
                LOG.error("Error while populating the Document Attachment", e);
                throw new RuntimeException("Could not populate DocumentAttachment object", e);
            }
        }
//        else if(attachment != null) {
//            //Attachment has been deleted - Need to delete the Attachment Reference Object
//            deleteAttachment();
//        }
    }
}
