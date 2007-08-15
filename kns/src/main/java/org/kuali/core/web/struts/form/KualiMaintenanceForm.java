/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.form;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class is the base action form for all maintenance documents.
 * 
 * 
 */
public class KualiMaintenanceForm extends KualiDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintenanceForm.class);

    private static final long serialVersionUID = 1L;

    private String businessObjectClassName;
    private String description;
    private boolean readOnly;
    private Map oldMaintainableValues;
    private Map newMaintainableValues;
    private String maintenanceAction;
    
    /**
     * Used to indicate which result set we're using when refreshing/returning from a multi-value lookup
     */
    private String lookupResultsSequenceNumber;
    /**
     * The type of result returned by the multi-value lookup
     * 
     * TODO: to be persisted in the lookup results service instead?
     */
    private String lookupResultsBOClassName;
    
    /**
     * The name of the collection looked up (by a multiple value lookup)
     */
    private String lookedUpCollectionName;
    
    private MaintenanceDocumentAuthorizations authorizations;
    
    /**
     * Override the default method to add the if statement which can't be called until after parameters from a multipart request
     * have been made accessible, but which must be called before the parameter values are used to instantiate and populate business
     * objects.
     * 
     * @param requestParameters
     */
    @Override
    public void postprocessRequestParameters(Map requestParameters) {
        super.postprocessRequestParameters(requestParameters);

        String docTypeName = null;
        String[] docTypeNames = (String[]) requestParameters.get(RiceConstants.DOCUMENT_TYPE_NAME);
        if ((docTypeNames != null) && (docTypeNames.length > 0)) {
            docTypeName = docTypeNames[0];
        }

        if (StringUtils.isNotBlank(docTypeName)) {
            setDocTypeName(docTypeName);
            setDocument(new MaintenanceDocumentBase(docTypeName));
        }
    }

    /**
     * Hook into populate so we can set the maintenance documents and feed the field values to its maintainables.
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);


        // document type name is null on start, otherwise should be here
        if (StringUtils.isNotBlank(getDocTypeName())) {
            Map localOldMaintainableValues = new HashMap();
            Map localNewMaintainableValues = new HashMap();
            Map<String,Object> localNewCollectionValues = new HashMap<String,Object>();
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameter = (String) i.nextElement();
                if (parameter.toUpperCase().startsWith(RiceConstants.MAINTENANCE_OLD_MAINTAINABLE.toUpperCase())) {
                    String propertyName = parameter.substring(RiceConstants.MAINTENANCE_OLD_MAINTAINABLE.length());
                    localOldMaintainableValues.put(propertyName, request.getParameter(parameter));
                }
                if (parameter.toUpperCase().startsWith(RiceConstants.MAINTENANCE_NEW_MAINTAINABLE.toUpperCase())) {
                    String propertyName = parameter.substring(RiceConstants.MAINTENANCE_NEW_MAINTAINABLE.length());
                    localNewMaintainableValues.put(propertyName, request.getParameter(parameter));
                }
            }
            // now, get all add lines and store them to a separate map
            // for use in a separate call to the maintainable
            for ( Object obj : localNewMaintainableValues.entrySet() ) {
                String key = (String)((Map.Entry)obj).getKey(); 
                if ( key.startsWith( RiceConstants.MAINTENANCE_ADD_PREFIX ) ) {
                    localNewCollectionValues.put( key.substring( RiceConstants.MAINTENANCE_ADD_PREFIX.length() ),
                            ((Map.Entry)obj).getValue() );
                }
            }
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "checked for add line parameters - got: " + localNewCollectionValues );
            }
            
            this.newMaintainableValues = localNewMaintainableValues;
            this.oldMaintainableValues = localOldMaintainableValues;

            MaintenanceDocumentBase maintenanceDocument = (MaintenanceDocumentBase) getDocument();

            GlobalVariables.getErrorMap().addToErrorPath("document.oldMaintainableObject");
            maintenanceDocument.getOldMaintainableObject().populateBusinessObject(localOldMaintainableValues);
            GlobalVariables.getErrorMap().removeFromErrorPath("document.oldMaintainableObject");

            GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
            // update the main object
            Map cachedValues = maintenanceDocument.getNewMaintainableObject().populateBusinessObject(localNewMaintainableValues);
            // update add lines
            localNewCollectionValues = KNSServiceLocator.getUniversalUserService().resolveUserIdentifiersToUniversalIdentifiers(maintenanceDocument.getNewMaintainableObject().getBusinessObject(), localNewCollectionValues);
            cachedValues.putAll( maintenanceDocument.getNewMaintainableObject().populateNewCollectionLines( localNewCollectionValues ) );
            GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");

            if (cachedValues.size() > 0) {
                GlobalVariables.getErrorMap().putError(RiceConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_FORMATTING_ERROR);
                for (Iterator iter = cachedValues.keySet().iterator(); iter.hasNext();) {
                    String propertyName = (String) iter.next();
                    String value = (String) cachedValues.get(propertyName);
                    cacheUnconvertedValue(RiceConstants.MAINTENANCE_NEW_MAINTAINABLE + propertyName, value);
                }
            }
        }
    }

    /**
     * Merges rows of old and new for each section (tab) of the ui. Also, renames fields to prevent naming conflicts and does
     * setting of read only fields.
     * 
     * @return Returns the maintenanceSections.
     */
    public List getSections() {
        if (getDocument() == null) {
            throw new RuntimeException("Document not set in maintenance form.");
        }
        if (((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject() == null) {
            throw new RuntimeException("New maintainable not set in document.");
        }
        if ((RiceConstants.MAINTENANCE_EDIT_ACTION.equals(this.getMaintenanceAction()) || RiceConstants.MAINTENANCE_COPY_ACTION.equals(this.getMaintenanceAction())) && ((MaintenanceDocumentBase) getDocument()).getOldMaintainableObject() == null) {
            throw new RuntimeException("Old maintainable not set in document.");
        }

        // if the authorization stuff hasnt been applied yet, then apply it
        if (authorizations == null) {
            applyAuthorizations();
        }

        // get business object being maintained and its keys
        List keyFieldNames = KNSServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject().getBusinessObject().getClass());

        // sections for maintenance document
        Maintainable oldMaintainable = ((MaintenanceDocumentBase) getDocument()).getOldMaintainableObject();
        oldMaintainable.setMaintenanceAction(getMaintenanceAction());
        List oldMaintSections = oldMaintainable.getSections(null);
        
        Maintainable newMaintainable = ((MaintenanceDocumentBase) getDocument()).getNewMaintainableObject();
        newMaintainable.setMaintenanceAction(getMaintenanceAction());
        List newMaintSections = newMaintainable.getSections(oldMaintainable);

        // mesh sections for proper jsp display
        List meshedSections = FieldUtils.meshSections(oldMaintSections, newMaintSections, keyFieldNames, getMaintenanceAction(), isReadOnly(), authorizations);

        return meshedSections;
    }

    protected void applyAuthorizations() {
        Document document = getDocument();
        DocumentAuthorizationService docAuthService = KNSServiceLocator.getDocumentAuthorizationService();
        DocumentAuthorizer documentAuthorizer = docAuthService.getDocumentAuthorizer(document);
        useDocumentAuthorizer(documentAuthorizer);
    }

    @Override
    protected void useDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {

        // init some things we'll need
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) getDocument();
        MaintenanceDocumentAuthorizer maintenanceDocumentAuthorizer = (MaintenanceDocumentAuthorizer) documentAuthorizer;

        // set the overall document editing mode
        setEditingMode(documentAuthorizer.getEditMode(maintenanceDocument, kualiUser));

        // WHY IS THIS READONLY STUFF HERE YOU ASK, GIVEN THE EDITMODE
        //
        // Thats a good question. It's basically just there to make the proper generation
        // of UI stuff in the JSP code easier, as you can just set a simple var on the jsp
        // with the isReadOnly property.
        //
        // ITS IMPORTANT TO NOTE that the readOnly flag is ALWAYS dependent on the EditingMode
        // data. So EditingMode is authoritative, readOnly is there for convenience.
        //

        // set the readOnly flag for this document, default to readOnly = true
        setReadOnly(true);
        String editMode;
        editMode = (String) editingMode.get(AuthorizationConstants.MaintenanceEditMode.APPROVER_EDIT_ENTRY);
        if ("TRUE".equalsIgnoreCase(editMode)) {
            setReadOnly(false);
        }
        editMode = (String) editingMode.get(AuthorizationConstants.MaintenanceEditMode.FULL_ENTRY);
        if ("TRUE".equalsIgnoreCase(editMode)) {
            setReadOnly(false);
        }

        // set field permissions
        setAuthorizations(maintenanceDocumentAuthorizer.getFieldAuthorizations(maintenanceDocument, kualiUser));

        // set the overall document action flags
        setDocumentActionFlags(maintenanceDocumentAuthorizer.getDocumentActionFlags(maintenanceDocument, kualiUser));
    }

    /**
     * @return Returns the maintenanceAction.
     */
    public String getMaintenanceAction() {
        return maintenanceAction;
    }

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the isReadOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly The isReadOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return Returns the newMaintainableValues.
     */
    public Map getNewMaintainableValues() {
        return newMaintainableValues;
    }

    /**
     * @return Returns the oldMaintainableValues.
     */
    public Map getOldMaintainableValues() {
        return oldMaintainableValues;
    }

    /**
     * @param maintenanceAction The maintenanceAction to set.
     */
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

    /**
     * Gets the authorizations attribute.
     * 
     * @return Returns the authorizations.
     */
    public MaintenanceDocumentAuthorizations getAuthorizations() {
        return authorizations;
    }

    /**
     * Sets the authorizations attribute value.
     * 
     * @param authorizations The authorizations to set.
     */
    public void setAuthorizations(MaintenanceDocumentAuthorizations authorizations) {
        this.authorizations = authorizations;
    }

    /**
     * Sets the newMaintainableValues attribute value.
     * 
     * @param newMaintainableValues The newMaintainableValues to set.
     */
    public void setNewMaintainableValues(Map newMaintainableValues) {
        this.newMaintainableValues = newMaintainableValues;
    }


    /**
     * Sets the oldMaintainableValues attribute value.
     * 
     * @param oldMaintainableValues The oldMaintainableValues to set.
     */
    public void setOldMaintainableValues(Map oldMaintainableValues) {
        this.oldMaintainableValues = oldMaintainableValues;
    }


    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }


    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }


    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }


    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }


    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }


    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }


}