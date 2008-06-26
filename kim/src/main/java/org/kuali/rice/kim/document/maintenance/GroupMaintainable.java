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
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.GroupTypeDefaultAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.lookup.valuefinder.NextAttributeTypeIdFinder;

/**
 * This maintainable class helps with the lifecycle of a maintenance document for the Group maintenance screen.  
 * It specifically overrides the parent to handle transferring data about group type choices and their require attribues
 * between UI specific BOs and persistence specific BOs. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupMaintainable extends KualiMaintainableImpl {
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.core.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> parameters) {
//		Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
//        populateAttributeFormObjects(oldGroupBO);
//
//        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
//        populateAttributeFormObjects(newGroupBO);
        
		super.processAfterNew(document, parameters);
	}

	/**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        Group oldGroupBO = (Group)document.getOldMaintainableObject().getBusinessObject();
        populateAttributeFormObjects(oldGroupBO);

        Group newGroupBO = (Group)document.getNewMaintainableObject().getBusinessObject();
        populateAttributeFormObjects(newGroupBO);
        
        super.processAfterEdit(document, parameters);
    }
    
    /**
     * This overridden method deals with translating the data from the maint. doc UI into the appropriate
     * persistable business objects that map down to the ORM level.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
//        Role role = (Role)getBusinessObject();
//
//        prepareGroupQualifiedRoleAttributesForSave(role);
//        preparePrincipalQualifiedRoleAttributesForSave(role);
        
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
    	ArrayList<GroupTypeDefaultAttribute> gtdas = group.getGroupType().getGroupTypeDefaultAttributes();
    	for (GroupTypeDefaultAttribute gtda : gtdas) {
    		GroupAttribute ga = new GroupAttribute();
    		ga.setAttributeName(gtda.getAttributeName());
    		ga.setAttributeTypeId(gtda.getAttributeTypeId());
    		
    		group.getGroupTypeAttributes().add(ga);
    	}
    	
    	ArrayList<GroupAttribute> gtas = group.getGroupTypeAttributes();  //this is the list we just partially set up
    	ArrayList<GroupAttribute> ngtas = group.getNonGroupTypeAttributes(); //this has been pre-populated with the persisted list and we'll widdle down
    	
    	// now loop through both lists comparing the names and using the persisted values if there is a match
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
    				break;
    			}
    		}
    		
    		if(!foundMatch) {
    			gta.setId(new NextAttributeTypeIdFinder().getLongValue());
    			gta.setValue(new String(""));
    		}
    	}
    }

    
    
    /**
     * This method will extract the data entered into the UI and populate that into the persistable BOs that will
     * be used for actually storing the object.
     * 
     * @param role
     */
    private void preparePrincipalQualifiedRoleAttributesForSave(Role role) {
        ArrayList<PrincipalQualifiedRole> principalQualifiedRoles = role.getPrincipalQualifiedRoles();

        role.getPrincipals().clear();
        role.getPrincipalQualifiedRoleAttributes().clear();
        
        // construct the list of principals to save through the persistable list of principals on the role BO
        for(PrincipalQualifiedRole pqr : principalQualifiedRoles) {
            Principal p = new Principal();
            p.setId(pqr.getId());
            role.getPrincipals().add(p);
            
            ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = pqr.getQualifiedRoleAttributes();
            for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
            	pqrAttrib.setPrincipalId(pqr.getId());
            	pqrAttrib.setRoleId(role.getId());
                role.getPrincipalQualifiedRoleAttributes().add(pqrAttrib);
            }
        }
    }
}
