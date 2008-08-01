/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.inquiry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.rice.kim.KIMServiceLocator;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.util.KIMConstants;
import org.kuali.rice.kim.web.form.EntityAttributeForm;

/**
 * This class essentially intercepts the request and handles transforming data coming in from the persistence 
 * layer into form objects for rendering appropriately.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityInquirable extends KualiInquirableImpl {
	 /**
	 * This overridden method intercepts the request and takes the persisted values and transforms 
	 * them into form objects for rendering.
	 * 
	 * @see org.kuali.core.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		BusinessObject bo = super.getBusinessObject(fieldValues);
		
		if(bo!=null){
			if(bo instanceof Entity){
				 Entity entity = (Entity)bo;
				 populateEntityAttributeFormObjects(entity);
			 }
		}
		return bo;
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
	 * This overridden method handles building out appropriate sections based on namespace default attributes and any 
	 * custom entity attributes.
	 * 
	 * @see org.kuali.core.inquiry.KualiInquirableImpl#getSections(org.kuali.core.bo.BusinessObject)
	 */
	@Override
	public List<Section> getSections(BusinessObject bo) {
		ArrayList<Section> sections = new ArrayList<Section>();
    	ArrayList<Section> coreSections = (ArrayList<Section>) super.getSections(bo);
        
        for(Section section : coreSections) {
        	// want custom entity attributes to be after the namespace specific ones 
        	if(section.getSectionTitle().equals("Custom Entity Attributes")) {
        		sections.addAll(getNamespaceSections(bo));
        	}
        	
        	sections.add(section);
        }
        return sections;
	}

	/**
     * This method handles creating sections per namespace if the namespace has default attributes set for it. 
     * 
     * @param bo
     * @return ArrayList<Section>
     */
    private ArrayList<Section> getNamespaceSections(BusinessObject bo) {
    	ArrayList<Section> sections = new ArrayList<Section>();
        
        ArrayList<NamespaceDTO> namespaces = (ArrayList<NamespaceDTO>) KIMServiceLocator.getNamespaceService().getAllNamespaces();
        
        Entity newEntity = (Entity) bo;
            
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
}