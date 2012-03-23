/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.krad.bo.PersistableAttachment;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.persistence.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

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

    @Override
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

    @Override
    public void populateAttachmentForBO() {
        refreshAttachment();

        PersistableAttachment boAttachment = (PersistableAttachment) newMaintainableObject.getDataObject();

    	if (ObjectUtils.isNotNull(getAttachmentPropertyName())) {
    		String attachmentPropNm = getAttachmentPropertyName();
    		String attachmentPropNmSetter = "get" + attachmentPropNm.substring(0, 1).toUpperCase() + attachmentPropNm.substring(1, attachmentPropNm.length());
    		FormFile attachmentFromBusinessObject;

    		if((boAttachment.getFileName() == null) && (boAttachment instanceof PersistableAttachment)) {
    			try {
    				Method[] methods = boAttachment.getClass().getMethods();
    				for (Method method : methods) {
    					if (method.getName().equals(attachmentPropNmSetter)) {
    						attachmentFromBusinessObject =  (FormFile)(boAttachment.getClass().getDeclaredMethod(attachmentPropNmSetter).invoke(boAttachment));
    						if (attachmentFromBusinessObject != null) {
    							boAttachment.setAttachmentContent(attachmentFromBusinessObject.getFileData());
    							boAttachment.setFileName(attachmentFromBusinessObject.getFileName());
    							boAttachment.setContentType(attachmentFromBusinessObject.getContentType());
    						}
    						break;
    					}
    				}
    		   } catch (Exception e) {
    				LOG.error("Not able to get the attachment " + e.getMessage());
    				throw new RuntimeException("Not able to get the attachment " + e.getMessage());
    		   }
    	  }
      }

      if((boAttachment.getFileName() == null) && (boAttachment instanceof PersistableAttachment) && (attachment != null)) {
    	  byte[] fileContents;
          fileContents = attachment.getAttachmentContent();
          if (fileContents.length > 0) {
              boAttachment.setAttachmentContent(fileContents);
              boAttachment.setFileName(attachment.getFileName());
              boAttachment.setContentType(attachment.getContentType());
          }
       }
    }
}
