/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.KualiModuleUserPropertyService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

public class UniversalUserMaintainable extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversalUserMaintainable.class);

    private static KualiConfigurationService configService;
    private static String userEditWorkgroupName;
    private static boolean usersMaintainedByKuali;
    private static DocumentService documentService;
    private static UniversalUserService universalUserService;
    private static KualiModuleService kualiModuleService;
    private static KualiModuleUserPropertyService moduleUserPropertyService;
    
    @Override
    public List getSections() {
        List sections = new ArrayList();
        sections.addAll(getCoreSections());
        sections.addAll(getModuleUserSections());
        return sections;
    }


    private List getModuleUserSections() {
        initStatics();
        List<Section> sections = new ArrayList<Section>();
        UniversalUser universalUser = (UniversalUser)getBusinessObject();
        List<KualiModule> modules = kualiModuleService.getInstalledModules(); 
        DataDictionaryDefinitionBase.isParsingFile = false; // prevents from attempting to retrieve file name and line number (which throws an exception)
        
        // iterate over installed modules - create a section for each        
        for ( KualiModule module : modules ) {
            MaintainableSectionDefinition sectionDef = new MaintainableSectionDefinition();
            sectionDef.setTitle( module.getModuleName() + " User Properties" );

            KualiModuleUser moduleUser = null;
            if ( universalUser != null ) {
                moduleUser = universalUser.getModuleUser( module.getModuleId() );
            }
            
            if ( moduleUser == null ) { // not sure when this would happen, but let's avoid an NPE
                continue;
            }
            
            List<String> userPropertyNames = module.getModuleUserService().getPropertyList();

            // don't add a section if no properties have been defined for a module
            if ( userPropertyNames.size() == 0 ) {
                continue;
            }
            
            // add the UUID field to support proper linking of objects
            MaintainableFieldDefinition fieldDef = new MaintainableFieldDefinition();
            fieldDef.setName( "personUniversalIdentifier" );
            sectionDef.addMaintainableItem( fieldDef );

            for ( String propertyName : userPropertyNames ) {
                fieldDef = new MaintainableFieldDefinition();
                fieldDef.setName( propertyName );                
                sectionDef.addMaintainableItem( fieldDef );
            }
            
            try {
                Section section = SectionBridge.toSection(sectionDef, moduleUser, this, getMaintenanceAction(), isGenerateDefaultValues(), isGenerateBlankRequiredValues(), userPropertyNames);

                // update the property names for the form (to map into moduleUser Map property)
                for ( Row row : section.getRows() ) {
                    for ( Field field : row.getFields() ) {
                        field.setPropertyName( "moduleUsers(" + module.getModuleId() + ")." + field.getPropertyName() );
                        
                        // convert the field conversions to have the appropriate prefix for the module user sections
                        Map<String, String> fieldConversions = LookupUtils.translateFieldConversions(field.getFieldConversions());
                        Map<String, String> newFieldConversions = new HashMap<String, String>();
                        for (String fieldConversionSource : fieldConversions.keySet()) {
                            String fieldConversionTarget = fieldConversions.get(fieldConversionSource);
                            String newFieldConversionTarget = "moduleUsers(" + module.getModuleId() + ")." + fieldConversionTarget;
                            newFieldConversions.put(fieldConversionSource, newFieldConversionTarget);
                        }
                        field.setFieldConversions(newFieldConversions);

                        // convert the lookup parameters to have the appropriate prefix for the module user sections
                        Map<String, String> lookupParameters = LookupUtils.translateFieldConversions(field.getLookupParameters());
                        Map<String, String> newLookupParameters = new HashMap<String, String>();
                        for (String lookupParameterSource : lookupParameters.keySet()) {
                            String lookupParameterTarget = lookupParameters.get(lookupParameterSource);
                            String newLookupParameterSource = "moduleUsers(" + module.getModuleId() + ")." + lookupParameterSource;
                            newLookupParameters.put(newLookupParameterSource, lookupParameterTarget);
                        }
                        field.setLookupParameters(newLookupParameters);
                    }
                }
                LOG.info("Updated Error key for section : " + section.getSectionTitle() + " is " + section.getErrorKey());

                sections.add( section );
            } catch ( IllegalAccessException ex ) {
                // ????
                LOG.error( "Error creating Section for module " + module.getModuleId(), ex );
                throw new RuntimeException( "Error creating Section object for form.", ex );
            } catch ( InstantiationException ex ) {
                // ????
                LOG.error( "Error creating Section for module " + module.getModuleId(), ex );
                throw new RuntimeException( "Error creating Section object for form.", ex );
            }
        }
        
        return sections;
    }

    @Override
    public void saveBusinessObject() {
        initStatics();
        // only attempt to save the UU object if the initiator is in the appropriate group
        // get the group name that we need here
        UniversalUser initiator = null;
        try {
            Document doc = documentService.getByDocumentHeaderId( documentNumber );
            if ( doc != null ) {
                String initiatorId = doc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
                if ( initiatorId != null ) {
                    initiator = universalUserService.getUniversalUser( new AuthenticationUserId( initiatorId ) );
                }
            }
        } catch ( WorkflowException ex ) {
            LOG.error( "unable to get initiator ID for document " + documentNumber, ex );
        } catch ( UserNotFoundException ex ) {
            LOG.error( "unable to get initator UniversalUser for for document " + documentNumber, ex );
        }
        // only save the primary UniversalUser business object if the conditions are met
        if ( usersMaintainedByKuali && initiator != null && initiator.isMember( userEditWorkgroupName ) ) {        
            super.saveBusinessObject();
        }

        // save module user properties
        UniversalUser user = (UniversalUser)getBusinessObject();
        // retrieve the property objects from the database
        Collection<KualiModuleUserProperty> props = moduleUserPropertyService.getPropertiesForUser( user );
        // iterate over the property map
        
        for ( Map.Entry<String,Map<String,String>> moduleEntry : user.getModuleProperties().entrySet() ) {
            String moduleId = moduleEntry.getKey();
            for ( Map.Entry<String,String> entry : moduleEntry.getValue().entrySet() ) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                boolean propertyFound = false;
                // find the associated business object
                for ( KualiModuleUserProperty prop : props ) {
                    if ( prop.getModuleId().equals( moduleId ) 
                            && prop.getName().equals( propertyName ) ) {
                        propertyFound = true;
                        String oldPropertyValue = prop.getValue(); 
                        // compare to value in the business object
                        // if changed, set the value in the BO and save it
                        if ( oldPropertyValue == null || !oldPropertyValue.equals( propertyValue ) ) {
                            prop.setValue( propertyValue );
                            moduleUserPropertyService.save( prop );
                        }
                    }
                }
                // if the property was not found, create a new one and save it
                if ( !propertyFound ) {
                    KualiModuleUserProperty newProp = new KualiModuleUserProperty();
                    newProp.setPersonUniversalIdentifier( user.getPersonUniversalIdentifier() );
                    newProp.setModuleId( moduleId );
                    newProp.setName( propertyName );
                    newProp.setValue( propertyValue );
                    moduleUserPropertyService.save( newProp );
                    props.add( newProp );
                }
            }
        }
    }
    
    private void initStatics() {
        if ( kualiModuleService == null ) { // they're all set at the same time, so only need one check
            configService = KNSServiceLocator.getKualiConfigurationService();
            universalUserService = KNSServiceLocator.getUniversalUserService();
            moduleUserPropertyService = KNSServiceLocator.getKualiModuleUserPropertyService();
            documentService = KNSServiceLocator.getDocumentService();
            userEditWorkgroupName = configService.getApplicationParameterValue(Constants.CoreApcParms.GROUP_CORE_MAINT_EDOCS, Constants.CoreApcParms.UNIVERSAL_USER_EDIT_WORKGROUP);
            // check whether users are editable within Kuali
            usersMaintainedByKuali = configService.getPropertyAsBoolean( Constants.MAINTAIN_USERS_LOCALLY_KEY );
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
    }

    /**
     * @see org.kuali.core.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
        // need to make sure that the UUID is populated first for later fields
        if ( fieldValues.containsKey( PropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) ) {
            ((UniversalUser)getBusinessObject()).setPersonUniversalIdentifier( (String)fieldValues.get( PropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) );
        }
        return super.populateBusinessObject( fieldValues );
    }
    
    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterCopy()
     */
    public void processAfterCopy() {
        UniversalUser user = (UniversalUser) businessObject;
        user.setPersonUserIdentifier("");
    }
}
