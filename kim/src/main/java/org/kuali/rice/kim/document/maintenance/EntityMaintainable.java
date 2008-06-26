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
package org.kuali.rice.kim.document.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.KualiModuleUserPropertyService;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.rice.kim.service.EntityService;
import org.kuali.rice.kim.service.NamespaceService;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.NamespaceDefaultAttribute;



public class EntityMaintainable extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntityMaintainable.class);

    private static EntityService entityService;
    private static NamespaceService namespaceService;
 // private static EntityAttributeService entityAttributeService;
    
    @Override
    public List getSections(Maintainable oldMaintainable) {
        List sections = new ArrayList();
        sections.addAll(getCoreSections(oldMaintainable));
        sections.addAll(getNamespaceSections(oldMaintainable));
        return sections;
    }


    private List getNamespaceSections(Maintainable oldMaintainable) {
        initStatics();
        List<Section> sections = new ArrayList<Section>();
        Entity entity = (Entity)getBusinessObject();
        
        List<Namespace> namespaces = namespaceService.getNamespaces();
      //  DataDictionaryDefinitionBase.isParsingFile = false; // prevents from attempting to retrieve file name and line number (which throws an exception)
            
      // iterate over Namespaces - create a section for each        
        for ( Namespace namespace : namespaces ) {
            MaintainableSectionDefinition sectionDef = new MaintainableSectionDefinition();
            MaintainableSectionDefinition entitySubsectionDef = new MaintainableSectionDefinition();
            sectionDef.setTitle( namespace.getName() + " Attributes " );
                   
        /** NamespaceDefaultAttribute namespaceAttribute = null;
            if ( entity != null ) {
              namespaceAttribute = entity.getNamespaceAttribute( namespace.getName() );
            } **/
            
            List<NamespaceDefaultAttribute> namespaceAttributes = namespace.getNamespaceAttributes();
            
            List<String>  namespaceAttributePropertyNames = namespace.getNamespaceAttributeService().getPropertyList();
                 
            if ( namespaceAttributes.size() == 0 ) {
                continue;
            }
        // this bo should come from its service object   
            EntityAttribute entityaAtribute = new EntityAttribute();  // just for testing
        //property names should come from the spring bean file    
            List<String>  entityAttributePropertyNames = new ArrayList<String>(); // just for testing
            entityAttributePropertyNames.add("attributeTypeId");
            entityAttributePropertyNames.add("attributeName");
            entityAttributePropertyNames.add("value");
                                   
            for(String namespaceAttributePropertyName : namespaceAttributePropertyNames){
                MaintainableFieldDefinition fieldDef = new MaintainableFieldDefinition();
                fieldDef.setName(namespaceAttributePropertyName); 
                sectionDef.getMaintainableItems().add(fieldDef);
            }
            
            MaintainableSubSectionHeaderDefinition subSecDef = new MaintainableSubSectionHeaderDefinition();
            subSecDef.setName("Entity Attribute");
            entitySubsectionDef.getMaintainableItems().add(subSecDef);
            
            for(String entityAttributePropertyName : entityAttributePropertyNames){
                MaintainableFieldDefinition fieldDef = new MaintainableFieldDefinition();
                fieldDef.setName(entityAttributePropertyName); 
                entitySubsectionDef.getMaintainableItems().add(fieldDef);
            }
                  
            for (NamespaceDefaultAttribute NamespaceAttribute : namespaceAttributes) {
                            
            try {
                Section section = SectionBridge.toSection(sectionDef,entitySubsectionDef, NamespaceAttribute,entityaAtribute, this, oldMaintainable, getMaintenanceAction(), isGenerateDefaultValues(), isGenerateBlankRequiredValues(), namespaceAttributePropertyNames,entityAttributePropertyNames);
                //Section section = SectionBridge.toSection(sectionDef, NamespaceAttribute, this, oldMaintainable, getMaintenanceAction(), isGenerateDefaultValues(), isGenerateBlankRequiredValues(), namespaceAttributePropertyNames);
               
                LOG.info("Updated Error key for section : " + section.getSectionTitle() + " is " + section.getErrorKey());

                sections.add( section );
            } catch ( IllegalAccessException ex ) {
                // ????
                LOG.error( "Error creating Section for module " + namespace.getId(), ex );
                throw new RuntimeException( "Error creating Section object for form.", ex );
            } catch ( InstantiationException ex ) {
                // ????
                LOG.error( "Error creating Section for module " + namespace.getId(), ex );
                throw new RuntimeException( "Error creating Section object for form.", ex );
            }   }
        }
        
        return sections;
    }

    @Override
    public void saveBusinessObject() {    
    }
    
    private void initStatics() {
        if ( namespaceService == null ) { // they're all set at the same time, so only need one check
            entityService = KNSServiceLocator.getEntityService();
            namespaceService = KNSServiceLocator.getNamespaceService();
        }
    }

    /**
     * @see org.kuali.core.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
           return null;
    }
    
    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
    }
}
