/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.bo.DocumentAttachment;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.MultiDocumentAttachment;
import org.kuali.rice.krad.bo.PersistableAttachment;
import org.kuali.rice.krad.bo.PersistableAttachmentBase;
import org.kuali.rice.krad.bo.PersistableAttachmentList;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectSerializerService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.persistence.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated Use {@link org.kuali.rice.krad.maintenance.MaintenanceDocumentBase}.
 */
@Deprecated
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

    @Override
    public Object getDocumentBusinessObject() {
        return super.getDocumentDataObject();
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

    /**
     * The attachment BO is proxied in OJB.  For some reason when an attachment does not yet exist,
     * refreshReferenceObject is not returning null and the proxy cannot be materialized. So, this method exists to
     * properly handle the proxied attachment BO.  This is a hack and should be removed post JPA migration.
     */
    protected void refreshAttachment() {
        if (KRADUtils.isNull(attachment)) {
            this.refreshReferenceObject("attachment");
            final boolean isProxy = attachment != null && ProxyHelper.isProxy(attachment);
            if (isProxy && ProxyHelper.getRealObject(attachment) == null) {
                attachment = null;
            }
        }
    }

    protected void refreshAttachmentList() {
        if (KRADUtils.isNull(attachments)) {
            this.refreshReferenceObject("attachments");
            final boolean isProxy = attachments != null && ProxyHelper.isProxy(attachments);
            if (isProxy && ProxyHelper.getRealObject(attachments) == null) {
                attachments = null;
            }
        }
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
                    attachment.setObjectId(UUID.randomUUID().toString());
                    PersistableAttachment boAttachment = (PersistableAttachment) newMaintainableObject.getDataObject();
                    boAttachment.setAttachmentContent(null);
                    attachment.setDocumentNumber(getDocumentNumber());
                }
            } catch (FileNotFoundException e) {
                LOG.error("Error while populating the Document Attachment", e);
                throw new RuntimeException("Could not populate DocumentAttachment object", e);
            } catch (IOException e) {
                LOG.error("Error while populating the Document Attachment", e);
                throw new RuntimeException("Could not populate DocumentAttachment object", e);
            }
        } else {
            //fileAttachment isn't filled, populate from bo if it exists
            PersistableAttachment boAttachment = (PersistableAttachment) newMaintainableObject.getDataObject();
            if (attachment == null
                    && boAttachment != null
                    && boAttachment.getAttachmentContent() != null) {
                DocumentAttachment newAttachment = new DocumentAttachment();
                newAttachment.setDocumentNumber(getDocumentNumber());
                newAttachment.setAttachmentContent(boAttachment.getAttachmentContent());
                newAttachment.setContentType(boAttachment.getContentType());
                newAttachment.setFileName(boAttachment.getFileName());
                //null out boAttachment file, will be copied back before final save.
                boAttachment.setAttachmentContent(null);
                attachment = newAttachment;
            }
        }
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
    							//boAttachment.setAttachmentContent(attachmentFromBusinessObject.getFileData());
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
    	  //byte[] fileContents;
          //fileContents = attachment.getAttachmentContent();
          if (attachment.getFileName() != null) {
              boAttachment.setAttachmentContent(null);
              boAttachment.setFileName(attachment.getFileName());
              boAttachment.setContentType(attachment.getContentType());
          }
       }
    }

    @Override
    public void populateAttachmentBeforeSave() {
        PersistableAttachment boAttachment = (PersistableAttachment) newMaintainableObject.getDataObject();
        if (attachment != null
                && attachment.getAttachmentContent() != null) {
            boAttachment.setAttachmentContent(attachment.getAttachmentContent());
        } else {
            boAttachment.setAttachmentContent(null);
            boAttachment.setFileName(null);
            boAttachment.setContentType(null);
        }
    }

    @Override
    public void populateBoAttachmentListBeforeSave() {

        PersistableAttachmentList<PersistableAttachment> boAttachments = (PersistableAttachmentList<PersistableAttachment>) newMaintainableObject.getDataObject();
        if (CollectionUtils.isEmpty(attachments)) {
            //there are no attachments.  Clear out Bo Attachments
            boAttachments.setAttachments(Collections.<PersistableAttachment>emptyList());
            return;
        }
        Map<String, MultiDocumentAttachment> files = new HashMap<String, MultiDocumentAttachment>();
        for (MultiDocumentAttachment multiAttach : attachments) {
            String key = new StringBuffer(multiAttach.getFileName()).append("|").append(multiAttach.getContentType()).toString();
            files.put(key, multiAttach);
        }


        //want to just copy over file if possible, as there can be other fields that are not on PersistableAttachment
        //these arrays should be somewhat synched by the other populate methods
        if (CollectionUtils.isNotEmpty(boAttachments.getAttachments())) {
            for (PersistableAttachment attach : boAttachments.getAttachments()) {
                //try to get a new instance of the correct object...
                String key = new StringBuffer(attach.getFileName()).append("|").append(attach.getContentType()).toString();
                if (files.containsKey(key)) {
                    attach.setAttachmentContent(files.get(key).getAttachmentContent());
                    files.remove(key);
                }
            }
        }
    }

    @Override
    public void populateAttachmentListForBO() {
        refreshAttachmentList();

        PersistableAttachmentList<PersistableAttachment> boAttachments = (PersistableAttachmentList<PersistableAttachment>) newMaintainableObject.getDataObject();

        if (ObjectUtils.isNotNull(getAttachmentListPropertyName())) {
            //String collectionName = getAttachmentCollectionName();
            String attachmentPropNm = getAttachmentListPropertyName();
            String attachmentPropNmSetter = "get" + attachmentPropNm.substring(0, 1).toUpperCase() + attachmentPropNm.substring(1, attachmentPropNm.length());


            for (PersistableAttachment persistableAttachment : boAttachments.getAttachments()) {
                if((persistableAttachment.getFileName() == null)) {
                    try {
                        FormFile attachmentFromBusinessObject =  (FormFile)(persistableAttachment.getClass().getDeclaredMethod(attachmentPropNmSetter).invoke(persistableAttachment));
                        if (attachmentFromBusinessObject != null) {
                            //persistableAttachment.setAttachmentContent(
                            //        attachmentFromBusinessObject.getFileData());
                            persistableAttachment.setFileName(attachmentFromBusinessObject.getFileName());
                            persistableAttachment.setContentType(attachmentFromBusinessObject.getContentType());
                        }
                    } catch (Exception e) {
                        LOG.error("Not able to get the attachment " + e.getMessage());
                        throw new RuntimeException("Not able to get the attachment " + e.getMessage());
                    }
                }
            }
        }
        if((CollectionUtils.isEmpty(boAttachments.getAttachments())
                && (CollectionUtils.isNotEmpty(attachments)))) {

            List<PersistableAttachment> attachmentList = new ArrayList<PersistableAttachment>();
            for (MultiDocumentAttachment multiAttach : attachments) {

                //try to get a new instance of the correct object...
                if (multiAttach.getAttachmentContent().length > 0) {
                    PersistableAttachment persistableAttachment = convertDocToBoAttachment(multiAttach, false);
                    attachmentList.add(persistableAttachment);
                }
            }
            boAttachments.setAttachments(attachmentList);
        }
    }

    private PersistableAttachment convertDocToBoAttachment(MultiDocumentAttachment multiAttach, boolean copyFile) {
        PersistableAttachment persistableAttachment = new PersistableAttachmentBase();

        if (copyFile
                && multiAttach.getAttachmentContent() != null) {
            persistableAttachment.setAttachmentContent(multiAttach.getAttachmentContent());
        }
        persistableAttachment.setFileName(multiAttach.getFileName());
        persistableAttachment.setContentType(multiAttach.getContentType());
        return persistableAttachment;
    }

    @Override
    public void populateDocumentAttachmentList() {
        refreshAttachmentList();

        String attachmentPropNm = getAttachmentListPropertyName();
        String attachmentPropNmSetter = "get" + attachmentPropNm.substring(0, 1).toUpperCase() + attachmentPropNm.substring(1, attachmentPropNm.length());
        //don't have form fields to use to fill, but they should be populated on the DataObject.  grab them from there.
        PersistableAttachmentList<PersistableAttachment> boAttachmentList = (PersistableAttachmentList<PersistableAttachment>) newMaintainableObject.getDataObject();

        if (CollectionUtils.isNotEmpty(boAttachmentList.getAttachments())) {


            //build map for comparison
            Map<String, MultiDocumentAttachment> md5Hashes = new HashMap<String, MultiDocumentAttachment>();
            if (CollectionUtils.isNotEmpty(attachments)) {
                for (MultiDocumentAttachment currentAttachment : attachments) {
                    md5Hashes.put(DigestUtils.md5Hex(currentAttachment.getAttachmentContent()), currentAttachment);
                }
            }

            //Populate DocumentAttachment BO
            attachments = new ArrayList<MultiDocumentAttachment>();

            for (PersistableAttachment persistableAttachment : boAttachmentList.getAttachments()) {
                try {
                    FormFile attachmentFromBusinessObject =  (FormFile)(persistableAttachment.getClass().getDeclaredMethod(attachmentPropNmSetter).invoke(persistableAttachment));
                    if (attachmentFromBusinessObject != null) {
                        //
                        //byte[] fileContents = attachmentFromBusinessObject.getFileData();
                        String md5Hex = DigestUtils.md5Hex(attachmentFromBusinessObject.getInputStream());
                        if (md5Hashes.containsKey(md5Hex)) {
                            String newFileName = attachmentFromBusinessObject.getFileName();
                            MultiDocumentAttachment multiAttach = md5Hashes.get(md5Hex);
                            if (multiAttach.getFileName().equals(newFileName)) {
                                attachments.add(multiAttach);
                            } else {
                                multiAttach.setFileName(attachmentFromBusinessObject.getFileName());
                                multiAttach.setContentType(attachmentFromBusinessObject.getContentType());
                                attachments.add(multiAttach);
                            }
                            md5Hashes.remove(md5Hex);
                        } else {
                            MultiDocumentAttachment attach = new MultiDocumentAttachment();
                            attach.setFileName(attachmentFromBusinessObject.getFileName());
                            attach.setContentType(attachmentFromBusinessObject.getContentType());
                            attach.setAttachmentContent(attachmentFromBusinessObject.getFileData());
                            attach.setDocumentNumber(getDocumentNumber());
                            attachments.add(attach);
                        }
                    } else {
                        if (persistableAttachment.getFileName() != null
                                && persistableAttachment.getAttachmentContent() != null) {
                            MultiDocumentAttachment attach = new MultiDocumentAttachment();
                            attach.setFileName(persistableAttachment.getFileName());
                            attach.setContentType(persistableAttachment.getContentType());
                            attach.setAttachmentContent(persistableAttachment.getAttachmentContent());
                            attach.setDocumentNumber(getDocumentNumber());

                            //set Bo's content to null
                            persistableAttachment.setAttachmentContent(null);
                            attachments.add(attach);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Not able to get the attachment " + e.getMessage());
                    throw new RuntimeException("Not able to get the attachment " + e.getMessage());
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BusinessObjectSerializerService getBusinessObjectSerializerService() {
        return KRADServiceLocator.getBusinessObjectSerializerService();
    }

    /**
     * this needs to happen after the document itself is saved, to preserve consistency of the ver_nbr and in the case
     * of initial save, because this can't be saved until the document is saved initially
     *
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rules.rule.event.DocumentEvent)
     */
    @Override
    public void postProcessSave(DocumentEvent event) {
        Object bo = getNewMaintainableObject().getDataObject();
        if (bo instanceof GlobalBusinessObject) {
            bo = KRADServiceLocatorWeb.getLegacyDataAdapter().save(bo);
            // KRAD/JPA - have to change the handle to object to that just saved
            getNewMaintainableObject().setDataObject(bo);
        }

        //currently only global documents could change the list of what they're affecting during routing,
        //so could restrict this to only happening with them, but who knows if that will change, so safest
        //to always do the delete and re-add...seems a bit inefficient though if nothing has changed, which is
        //most of the time...could also try to only add/update/delete what's changed, but this is easier
        if (!(event instanceof SaveDocumentEvent)) { //don't lock until they route
            getMaintenanceDocumentService().deleteLocks(MaintenanceDocumentBase.this.getDocumentNumber());
            getMaintenanceDocumentService().storeLocks(MaintenanceDocumentBase.this.getNewMaintainableObject().generateMaintenanceLocks());
        }
    }


}
