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
import java.util.List;
import java.util.Map;

import org.kuali.core.web.ui.Section;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.GroupType;
import org.kuali.rice.kim.bo.GroupTypeDefaultAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.web.form.RoleQualificationForGroup;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;

/**
 * This class essentially intercepts the request and handles transforming data coming in from the persistence 
 * layer into form objects for rendering appropriately.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupInquirable extends KualiInquirableImpl {
	 /**
	 * This overridden method intercepts the request and takes the persisted values and transforms 
	 * them into form objects for rendering.
	 * 
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		BusinessObject bo = super.getBusinessObject(fieldValues);
		
		if(bo!=null){
			if(bo instanceof Group){
				 Group group = (Group)bo;
				 populateRoleQualifications(group);
				 populateAttributeFormObjects(group);
			 }
		}
		return bo;
	}
	
	
	
	/**
	 * This overridden method checks to see if the assigned group type has default attributes.  If it doesn't, we remove that section 
	 * from displaying.
	 * 
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public List<Section> getSections(BusinessObject bo) {
		ArrayList<Section> sections = (ArrayList) super.getSections(bo);

		Group group = (Group) bo;
		GroupType gt = group.getGroupType();
    	
    	if(gt.getGroupTypeDefaultAttributes().isEmpty()) {
    		int index = 0;
    		for (Section section : sections) {
    			if(section.getSectionTitle().equals("Group Type Attributes")) {
    				sections.remove(index);
    				break;
    			}
    			index++;
    		}
    	}
    	
		return sections;
	}



	/**
     * This method is responsible for taking the persisted group qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the inquiry user interface.
     * 
     * @param role
     */
    private void populateAttributeFormObjects(Group group) {
    	// clear these out each time
    	group.getGroupTypeAttributes().clear();
    	
    	// set to the persisted list, which we'll narrow down later
    	group.setNonGroupTypeAttributes(group.getGroupAttributes());
    	
    	// we want to look at all required attributes for the selected group type and build out the list of attribute fields
    	// to show in the Group Type Attributes section on the form
    	GroupType gt = group.getGroupType();
    	ArrayList<GroupTypeDefaultAttribute> gtdas = gt.getGroupTypeDefaultAttributes();
    	
    	// for each group type default attribute we need to go through and populate the form list
    	for (GroupTypeDefaultAttribute gtda : gtdas) {
    		GroupAttribute ga = new GroupAttribute();
    		ga.setAttributeName(gtda.getAttributeName());
    		ga.setAttributeTypeId(gtda.getAttributeTypeId());
    		
    		group.getGroupTypeAttributes().add(ga);
    	}
    	
    	ArrayList<GroupAttribute> gtas = group.getGroupTypeAttributes();  //this is the list we just partially set up
    	ArrayList<GroupAttribute> ngtas = group.getNonGroupTypeAttributes(); //this has been pre-populated with the persisted list and we'll widdle down
    	
    	// now loop through both lists comparing the names and using the persisted values if there is a match
    	for (GroupAttribute gta : gtas) {
    		boolean foundMatch = false;
    		for (GroupAttribute ngta : ngtas) {
    			if(gta.getAttributeName().equals(ngta.getAttributeName())) {
    				// since equal, we want all of the persisted meta-data transfered for viewing
    				gta.setId(ngta.getId());
    				gta.setValue(ngta.getValue());
    				gta.setObjectId(ngta.getObjectId());
    				gta.setVersionNumber(ngta.getVersionNumber());
    				
    				//now remove this one from the other list
    				ngtas.remove(ngta);
    				
    				//mark that we found a match
    				foundMatch = true;
    				break;
    			}
    		}
    		
    		if(!foundMatch) {
    			gta.setValue(new String(""));
    		}
    	}
    }
	 
    /**
     * This method is responsible for taking the persisted group qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the maintenance document user interface.
     * 
     * @param group
     */
    private void populateRoleQualifications(Group group) {
            group.getRoleQualificationsForGroup().clear();

            ArrayList<Role> roles = group.getRoles();
            for (Role r : roles) {
                RoleQualificationForGroup rqfg = new RoleQualificationForGroup();
                rqfg.setId(r.getId());
                rqfg.setName(r.getName());
                rqfg.setGroupId(group.getId());

                ArrayList<GroupQualifiedRoleAttribute> gqrAttribs = group.getGroupQualifiedRoleAttributes();
                for(GroupQualifiedRoleAttribute gqrAttrib : gqrAttribs) {
                    if(gqrAttrib.getRoleId().equals(rqfg.getId())) {
                    	
                        rqfg.getQualifiedRoleAttributes().add(gqrAttrib);
                    }
                }
                
                group.getRoleQualificationsForGroup().add(rqfg);
            }
    }
}