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
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.lookup.valuefinder.NextEntityAttributeIdFinder;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KIMConstants;
import org.kuali.rice.kim.web.form.EntityAttributeForm;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;


/**
 * This class handles building out the appropriate sections for the defined namespace default attributes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EntityMaintainable extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntityMaintainable.class);
    
    private NextEntityAttributeIdFinder nextEntityAttribFinder;
    
    /**
     * This constructs a EntityMaintainable and instantiates a new entity finder.
     *
     */
    public EntityMaintainable() {
    	super();
    	this.nextEntityAttribFinder = new NextEntityAttributeIdFinder();
    }
    
    /**
     * This overridden method handles aggregating all the sections including one section per namespace.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List getSections(Maintainable oldMaintainable) {
    	ArrayList<Section> sections = new ArrayList<Section>();
    	ArrayList<Section> coreSections = (ArrayList<Section>) getCoreSections(oldMaintainable);
        
        for(Section section : coreSections) {
        	// want custom entity attributes to be after the namespace specific ones 
        	if(section.getSectionTitle().equals("Custom Entity Attributes")) {
        		sections.addAll(getNamespaceSections(oldMaintainable));
        	}
        	
        	sections.add(section);
        }
        return sections;
    }

    
    
    /**
	 * This overridden method handles populating the sections correctly with the different entity attributes.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterCopy(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		Maintainable oldMaintainable = document.getOldMaintainableObject();
		Entity oldEntity = (Entity) oldMaintainable.getBusinessObject();
		populateEntityAttributeFormObjects(oldEntity);
		
		Maintainable newMaintainable = document.getNewMaintainableObject();
		Entity newEntity = (Entity) newMaintainable.getBusinessObject();
		populateEntityAttributeFormObjects(newEntity);
		
		super.processAfterCopy(document, parameters);
	}



	/**
	 * This overridden method handles populating the sections correctly with the different entity attributes.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterEdit(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		Maintainable oldMaintainable = document.getOldMaintainableObject();
		Entity oldEntity = (Entity) oldMaintainable.getBusinessObject();
		populateEntityAttributeFormObjects(oldEntity);
		
		Maintainable newMaintainable = document.getNewMaintainableObject();
		Entity newEntity = (Entity) newMaintainable.getBusinessObject();
		populateEntityAttributeFormObjects(newEntity);
		
		super.processAfterEdit(document, parameters);
	}
	
	
	/**
	 * This method handles pushing the persisted entity attributes retrieved from the DB down into the appropriate sections.
	 * 
	 * @param entity
	 */
	private void populateEntityAttributeFormObjects(Entity entity) {
		for(EntityAttribute attribute : entity.getEntityAttributes()) {
			if(StringUtils.contains(attribute.getAttributeName(), KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN)) {
				entity.getNamespaceEntityAttributes().put(attribute.getAttributeName(), attribute.getValue());
			} else {
	    		EntityAttributeForm eaf = new EntityAttributeForm();
	    		eaf.setId(attribute.getId());
	    		eaf.setEntityId(entity.getId());
	    		eaf.setAttributeName(attribute.getAttributeName());
	    		eaf.setAttributeTypeId(attribute.getAttributeTypeId());
	    		eaf.setNamespaceId(KIMConstants.NAMESPACE.KIM_NAMESPACE);  //all custom entity attributes get associated with the OOTB KIM bootstrap namespace
	    		eaf.setValue(attribute.getValue());
	    		eaf.setVersionNumber(attribute.getVersionNumber());
	    		
	    		entity.getEntityAttributeForms().add(eaf);
			}
		}
	}

	/**
     * This method handles creating sections per namespace if the namespace has default attributes set for it. 
     * 
     * @param oldMaintainable
     * @return ArrayList<Section>
     */
    private ArrayList<Section> getNamespaceSections(Maintainable oldMaintainable) {
        ArrayList<Section> sections = new ArrayList<Section>();
        
        ArrayList<NamespaceDTO> namespaces = (ArrayList<NamespaceDTO>) KIMServiceLocator.getNamespaceService().getAllNamespaces();
        
        Entity newEntity = (Entity) getBusinessObject();
            
        // iterate over Namespaces - create a section for each one that has default attributes     
        for ( NamespaceDTO namespace : namespaces ) {
        	if(!namespace.getNamespaceAttributes().isEmpty()) { 
        		Section section = new Section();
	            section.setSectionId(namespace.getId().toString());
	            section.setSectionTitle(namespace.getName() + " Attributes");
	            
	            Iterator<Entry<String, NamespaceDefaultAttributeDTO>> attributes = namespace.getNamespaceAttributes().entrySet().iterator();
	        	
	            ArrayList<Field> fields = new ArrayList<Field>();
	            
	        	// for each group type default attribute we need to go through and populate the form list
	        	while(attributes.hasNext()) {
	        		Entry<String, NamespaceDefaultAttributeDTO> e = attributes.next();
	        		NamespaceDefaultAttributeDTO attribute = e.getValue();
	        		
	        		if(attribute.getActive()) {
	        			String propertyName = KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN + namespace.getId().toString() + "-" + attribute.getId().toString();
	        			Field field = new Field(attribute.getAttributeName(), "", Field.TEXT, true, propertyName, "", attribute.getRequired(), false, null, null, 50, 100);
	        			
	        			//check to see if this is a post and there are form entered values to populate with
	        			if(!newEntity.getNamespaceEntityAttributes().isEmpty()) {
	        				String value = newEntity.getNamespaceEntityAttributes().get(propertyName);
	        				if(value != null) {
	        					field.setPropertyValue(value);
	        				}
	        			}
	        			
	        			field.setCellAlign("left");
	        			fields.add(field);
	        		}
	        	}
	        		
	        	ArrayList<Row> rows = (ArrayList<Row>) FieldUtils.wrapFields(fields);
	        	
	        	section.setRows(rows);
	        	
	        	sections.add(section);
            }
        }
        
        return sections;
    }

	/**
	 * This overridden method takes the input in the dynamically rendered attribute sections, adds it to a temporary HashMap that hangs off of the 
	 * BO, so that it can be maintained post to post.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterPost(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		Entity oldEntity = (Entity) document.getOldMaintainableObject().getBusinessObject();
		Entity newEntity = (Entity) document.getNewMaintainableObject().getBusinessObject();
		
		Iterator<Entry<String, String[]>> attributes = parameters.entrySet().iterator();
        HashMap<String, String> newNamespaceEntityAttributes = new HashMap<String, String>(parameters.size());
        HashMap<String, String> oldNamespaceEntityAttributes = new HashMap<String, String>(parameters.size());
		
    	// for each group type default attribute we need to go through and populate the form list
    	while(attributes.hasNext()) {
    		Entry<String, String[]> e = attributes.next();
    		String key = e.getKey();
    		
    		if(StringUtils.contains(key, KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN)) {
    			String value = e.getValue()[0];
    			if(StringUtils.contains(key, "newMaintainableObject")) {
    				String newKey = KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN + (StringUtils.substringAfter(key, KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN));  //strip off the unnecessary text 
    				newNamespaceEntityAttributes.put(newKey, value);
    			} else { //oldMaintainableObject
    				String oldKey = KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN + (StringUtils.substringAfter(key, KIMConstants.NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN));  //strip off the unnecessary text
    				oldNamespaceEntityAttributes.put(oldKey, value);
    			}
    		}
    	}
    	
    	newEntity.setNamespaceEntityAttributes(newNamespaceEntityAttributes);
    	oldEntity.setNamespaceEntityAttributes(oldNamespaceEntityAttributes);
    	
		super.processAfterPost(document, parameters);
	}
	
	/**
     * This overridden method handles aggregating all of the entity attributes from the different sections down into a single list to persist.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
    	Entity entity = (Entity)getBusinessObject();
    	
    	// this data structure will be used later on for obtaining the Id of the appropriate attribute to prevent conflicts of unique 
    	// keys during save
    	HashMap<String, Long> priorEntityAttributesIds = new HashMap<String, Long>(entity.getEntityAttributes().size());
    	for(EntityAttribute priorAttrib : entity.getEntityAttributes()) {
    		priorEntityAttributesIds.put(priorAttrib.getAttributeName(), priorAttrib.getId());
    	}
    	
    	// now clear out to eliminate any conflicts 
    	entity.getEntityAttributes().clear();	
    	
    	// deal with persisting the custom attributes section
    	ArrayList<EntityAttributeForm> entityAttributeForms = entity.getEntityAttributeForms();
    	for(EntityAttributeForm eaf : entityAttributeForms) {
    		EntityAttribute customAttribute = new EntityAttribute();
    		customAttribute.setId(eaf.getId());
    		customAttribute.setEntityId(entity.getId());
    		customAttribute.setAttributeName(eaf.getAttributeName());
    		if(eaf.getAttributeTypeId() == null) {
    			customAttribute.setAttributeTypeId(KIMConstants.ATTRIBUTE_TYPE.TEXT); // for now, this should just be TEXT
    		} else {
    			customAttribute.setAttributeTypeId(eaf.getAttributeTypeId());
    		}
    		customAttribute.setNamespaceId(KIMConstants.NAMESPACE.KIM_NAMESPACE);  //all custom entity attributes get associated with the OOTB KIM bootstrap namespace
    		customAttribute.setValue(eaf.getValue());
    		customAttribute.setVersionNumber(eaf.getVersionNumber());
    		
    		entity.getEntityAttributes().add(customAttribute);
    	}
    	
    	// deal with persisting the namespace entity attributes sections
    	Iterator<Entry<String, String>> namespaceEntityAttributes = entity.getNamespaceEntityAttributes().entrySet().iterator();
        ArrayList<Field> fields = new ArrayList<Field>();

        while(namespaceEntityAttributes.hasNext()) {
    		Entry<String, String> e = namespaceEntityAttributes.next();
    		String value = e.getValue();
    		
    		EntityAttribute namespaceEntityAttribute = new EntityAttribute();
    		Long priorId = priorEntityAttributesIds.get(e.getKey());
    		if(priorId == null) {
    			priorId = nextEntityAttribFinder.getLongValue();
    		}
    		namespaceEntityAttribute.setId(priorId);
    		namespaceEntityAttribute.setEntityId(entity.getId());
    		namespaceEntityAttribute.setAttributeName(e.getKey());
    		namespaceEntityAttribute.setAttributeTypeId(KIMConstants.ATTRIBUTE_TYPE.TEXT); // for now, this should just be TEXT
    		// need to extract the namespace id from the key
    		String namespaceId = StringUtils.substringBetween(e.getKey(), "-");
    		namespaceEntityAttribute.setNamespaceId(new Long(namespaceId));
    		namespaceEntityAttribute.setValue(e.getValue());
    		
    		entity.getEntityAttributes().add(namespaceEntityAttribute);
        }
    	
    	super.saveBusinessObject();
    }
}