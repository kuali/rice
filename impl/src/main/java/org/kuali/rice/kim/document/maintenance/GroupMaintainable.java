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
package org.kuali.rice.kim.document.maintenance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kuali.rice.kim.KIMServiceLocator;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.dto.GroupTypeDTO;
import org.kuali.rice.kim.dto.GroupTypeDefaultAttributeDTO;
import org.kuali.rice.kim.lookup.valuefinder.NextAttributeTypeIdFinder;
import org.kuali.rice.kim.lookup.valuefinder.NextGroupTypeDefaultAttributeIdFinder;
import org.kuali.rice.kim.util.KIMConstants;
import org.kuali.rice.kim.web.form.RoleQualificationForGroup;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This maintainable class helps with the lifecycle of a maintenance document for the Group maintenance screen.  
 * It specifically overrides the parent to handle transferring data about group type choices and their require attribues
 * between UI specific BOs and persistence specific BOs. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupMaintainable extends KualiMaintainableImpl {
	private static final long serialVersionUID = -913792176411079003L;
	
	private NextGroupTypeDefaultAttributeIdFinder gtdaNextIdFinder;
	
	/**
	 * This constructs an instance of the next id finder for group type default attribute ids
	 */
	public GroupMaintainable() {
		super();
		this.gtdaNextIdFinder = new NextGroupTypeDefaultAttributeIdFinder();
	}

	/**
	 * This overridden method handles setting up the group type attributes for brand new maintenance documents since 
	 * data for group type attributes already exists.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> parameters) {
 		Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
		oldGroupBO.setGroupTypeId(KIMConstants.GROUP_TYPE.DEFAULT_GROUP_TYPE);
        populateAttributeFormObjects(oldGroupBO);

        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
        newGroupBO.setGroupTypeId(KIMConstants.GROUP_TYPE.DEFAULT_GROUP_TYPE);
        populateAttributeFormObjects(newGroupBO);
        
		super.processAfterNew(document, parameters);
	}
	
	/**
	 * This overridden method handles hiding the group type attributes section if a group type is chosen that doesn't have 
	 * any default attributes.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.rice.kns.maintenance.Maintainable)
	 */
	@Override
	public List<Section> getCoreSections(Maintainable oldMaintainable) {
		ArrayList<Section> sections = (ArrayList) super.getCoreSections(oldMaintainable);
		
		Group oldGroupBO = null;
		if(oldMaintainable != null) {
			oldGroupBO = (Group)oldMaintainable.getBusinessObject();
		}
		Group newGroupBO = (Group)getBusinessObject();
		
		handleVisibilityOfGroupTypeAttributeSection(oldGroupBO, newGroupBO, sections);
		
		return sections;
	}

	/**
	 * This overridden method handles the set up of the UI after a copy.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterCopy(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
		oldGroupBO.setGroupTypeId(KIMConstants.GROUP_TYPE.DEFAULT_GROUP_TYPE);
        populateAttributeFormObjects(oldGroupBO);
        populateRoleQualifications(oldGroupBO);

        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
        newGroupBO.setGroupTypeId(KIMConstants.GROUP_TYPE.DEFAULT_GROUP_TYPE);
        populateAttributeFormObjects(newGroupBO);
        populateRoleQualifications(oldGroupBO);
        
		super.processAfterCopy(document, parameters);
	}

	/**
     * This overridden method handles populating form objects for displaying in the interface on every load of a maintenance document after the initial 
     * create new action.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
        populateAttributeFormObjects(oldGroupBO);
        populateRoleQualifications(oldGroupBO);

        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
        populateAttributeFormObjects(newGroupBO);
        populateRoleQualifications(newGroupBO);
        
        
        super.processAfterEdit(document, parameters);
    }

	/**
	 * This overridden method handles re-rendering the attributes sections after a change of groupType.
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	public void refresh(String refreshCaller, Map fieldValues,
			MaintenanceDocument document) {
		
		// this comes back from a lookup
		String referencesToRefresh = null;
		if(fieldValues.get("referencesToRefresh") != null) {
			referencesToRefresh = fieldValues.get("referencesToRefresh").toString();
		}
		
		// refresh caller gets populated by the drop down
		if((refreshCaller != null && refreshCaller.equals("groupTypeChanged")) || 
				(referencesToRefresh != null && referencesToRefresh.equals("groupType"))) {
			Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
			if(oldGroupBO.getGroupTypeId() != null) {
				populateAttributeFormObjects(oldGroupBO);
			}

	        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
	        populateAttributeFormObjects(newGroupBO);
		}
		
		super.refresh(refreshCaller, fieldValues, document);
	}

	/**
     * This overridden method deals with translating the data from the maint. doc UI into the appropriate
     * persistable business objects that map down to the ORM level.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
    	Group group = (Group)getBusinessObject();

    	prepareGroupAttributesForSave(group);
    	prepareGroupQualifiedRoleAttributesForSave(group);
        
        super.saveBusinessObject();
    }

	/**
     * This method is responsible for taking the persisted group qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the maintenance document user interface.
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
    	GroupTypeDTO gt = KIMServiceLocator.getGroupTypeService().getGroupType(group.getGroupTypeId());
    	Iterator<Entry<String, GroupTypeDefaultAttributeDTO>> gtdas = gt.getGroupTypeDefaultAttributes().entrySet().iterator();
    	
    	// check to see if the nonGroupTypeAttributes list is empty, if it is, then we need to make sure to populate the 
    	// groupTypeAttributes with "new" values - i.e. ids and an empty string value
    	boolean emptyNonGroupTypeAttributes = group.getNonGroupTypeAttributes().isEmpty();
    	
    	// for each group type default attribute we need to go through and populate the form list
    	while(gtdas.hasNext()) {
    		Entry<String, GroupTypeDefaultAttributeDTO> e = gtdas.next();
    		GroupTypeDefaultAttributeDTO gtda = e.getValue();
    		
    		GroupAttribute ga = new GroupAttribute();
    		ga.setAttributeName(gtda.getAttributeName());
    		ga.setAttributeTypeId(gtda.getAttributeTypeId());
    		
    		if(emptyNonGroupTypeAttributes) {  //since no non-group type attributes exist, we have to make sure that we recognize these as new attributes; therfore generate an id and set the value to empty
    			ga.setId(new NextAttributeTypeIdFinder().getLongValue());
    			ga.setValue(new String(""));
    		}
    		
    		group.getGroupTypeAttributes().add(ga);
    	}
    	
    	ArrayList<GroupAttribute> gtas = group.getGroupTypeAttributes();  //this is the list we just partially set up
    	ArrayList<GroupAttribute> ngtas = group.getNonGroupTypeAttributes(); //this has been pre-populated with the persisted list and we'll widdle down
    	
    	// now loop through both lists comparing the names and using the persisted values if there is a match
    	if(!emptyNonGroupTypeAttributes) {  //we only want to loop through and do a compare if there are values; otherwise, no point
	    	for (GroupAttribute gta : gtas){
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
	    				continue;
	    			}
	    		}
	    		
	    		if(!foundMatch) {
	    			gta.setId(gtdaNextIdFinder.getLongValue());
	    			gta.setValue(new String(""));
	    		}
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
    
    /**
     * This method handles hiding the "Group Type Attributes" section only if the group type chosen for both the old and new maintainable is empty.
     * 
     * @param oldGroup
     * @param newGroup
     * @param sections
     */
    private ArrayList<Section> handleVisibilityOfGroupTypeAttributeSection(Group oldGroup, Group newGroup, ArrayList<Section> sections) {
    	if(newGroup.getGroupTypeId() != null) {
    		GroupTypeDTO newGt = KIMServiceLocator.getGroupTypeService().getGroupType(newGroup.getGroupTypeId());
    	
    	
	    	// only want to consider if both are empty - first check the new maintainable
	    	boolean emptyGroupTypeAttributes = newGt.getGroupTypeDefaultAttributes().isEmpty();
	    	
	    	//then check the old one if not null
	    	if(oldGroup != null) {
	    		GroupTypeDTO oldGt = KIMServiceLocator.getGroupTypeService().getGroupType(oldGroup.getGroupTypeId());
	    		if(oldGt != null) {
	    			emptyGroupTypeAttributes &= oldGt.getGroupTypeDefaultAttributes().isEmpty();
	    		}
	    	}
	    	
	    	if(emptyGroupTypeAttributes) {
	    		//then do the same for the new maintainable
	    		int index = 0;
	    		for (Section section : sections) {
	    			if(section.getSectionTitle().equals("Group Type Attributes")) {
	    				sections.remove(index);
	    			}
	    			index++;
	    		}
	    	}
    	}
    	return sections;
    }

    /**
     * This method will extract the data entered into the UI and populate that into the persistable BOs that will
     * be used for actually storing the object.
     * 
     * @param role
     */
    private void prepareGroupQualifiedRoleAttributesForSave(Group group) {
        ArrayList<RoleQualificationForGroup> roleQualificationsForGroup = group.getRoleQualificationsForGroup();

        group.getRoles().clear();
        group.getGroupQualifiedRoleAttributes().clear();
        
        // construct the list of groups to save through the persistable list of roles on the group BO
        for(RoleQualificationForGroup rqfg : roleQualificationsForGroup) {
            Role r = new Role();
            r.setId(rqfg.getId());
            group.getRoles().add(r);
            
            ArrayList<GroupQualifiedRoleAttribute> gqrAttribs = rqfg.getQualifiedRoleAttributes();
            for(GroupQualifiedRoleAttribute gqrAttrib : gqrAttribs) {
                gqrAttrib.setRoleId(rqfg.getId());
                gqrAttrib.setGroupId(group.getId());
                group.getGroupQualifiedRoleAttributes().add(gqrAttrib);
            }
        }
    }
    
    /**
     * This method just consolidates all of the group attributes from the helper objects back down into 
     * the single group attributes list for persistence.
     * 
     * @param group
     */
    private void prepareGroupAttributesForSave(Group group) {
    	ArrayList<GroupAttribute> nonGroupTypeAttributes = group.getNonGroupTypeAttributes();
    	ArrayList<GroupAttribute> groupTypeAttributes = group.getGroupTypeAttributes();
    	
    	group.getGroupAttributes().clear();
    	
    	for(GroupAttribute ga : nonGroupTypeAttributes) {
    		group.getGroupAttributes().add(ga);
    	}
    	
    	for(GroupAttribute ga : groupTypeAttributes) {
    		group.getGroupAttributes().add(ga);
    	}
    }
 }