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
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;

/**
 * This is a description of what this class does - ag266 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleMaintainable extends KualiMaintainableImpl {
    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        Role oldRoleBO = (Role)document.getOldMaintainableObject().getBusinessObject();
        populateGroupQualifiedRoles(oldRoleBO);
        populatePrincipalQualifiedRoles(oldRoleBO);
        
        Role newRoleBO = (Role)document.getNewMaintainableObject().getBusinessObject();
        populateGroupQualifiedRoles(newRoleBO);
        populatePrincipalQualifiedRoles(newRoleBO);
        
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
        Role role = (Role)getBusinessObject();

        prepareGroupQualifiedRoleAttributesForSave(role);
        preparePrincipalQualifiedRoleAttributesForSave(role);
        
        super.saveBusinessObject();
    }
    
    /**
     * This method is responsible for taking the persisted group qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the maintenance document user interface.
     * 
     * @param role
     */
    private void populateGroupQualifiedRoles(Role role) {
            role.getGroupQualifiedRoles().clear();

            ArrayList<Group> groups = role.getGroups();
            for (Group g : groups) {
                GroupQualifiedRole gqr = new GroupQualifiedRole();
                gqr.setId(g.getId());
                gqr.setName(g.getName());
                gqr.setRoleId(role.getId());

                ArrayList<GroupQualifiedRoleAttribute> gqrAttribs = role.getGroupQualifiedRoleAttributes();
                for(GroupQualifiedRoleAttribute gqrAttrib : gqrAttribs) {
                    if(gqrAttrib.getGroupId().equals(gqr.getId())) {
                        gqr.getQualifiedRoleAttributes().add(gqrAttrib);
                    }
                }
                
                role.getGroupQualifiedRoles().add(gqr);
            }
    }

    /**
     * This method is responsible for taking the persisted principal qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the maintenance document user interface.
     * 
     * @param role
     */
    private void populatePrincipalQualifiedRoles(Role role) {
            role.getPrincipalQualifiedRoles().clear();

            ArrayList<Principal> principals = role.getPrincipals();
            for (Principal p : principals) {
                PrincipalQualifiedRole pqr = new PrincipalQualifiedRole();
                pqr.setId(p.getId());
                pqr.setName(p.getName());
                pqr.setRoleId(role.getId());

                ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = role.getPrincipalQualifiedRoleAttributes();
                for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
                    if(pqrAttrib.getPrincipalId().equals(pqr.getId())) {
                        pqr.getQualifiedRoleAttributes().add(pqrAttrib);
                    }
                }
                
                role.getPrincipalQualifiedRoles().add(pqr);
            }
    }

    /**
     * This method will extract the data entered into the UI and populate that into the persistable BOs that will
     * be used for actually storing the object.
     * 
     * @param role
     */
    private void prepareGroupQualifiedRoleAttributesForSave(Role role) {
        ArrayList<GroupQualifiedRole> groupQualifiedRoles = role.getGroupQualifiedRoles();

        role.getGroups().clear();
        role.getGroupQualifiedRoleAttributes().clear();
        
        // construct the list of groups to save through the persistable list of groups on the role BO
        for(GroupQualifiedRole gqr : groupQualifiedRoles) {
            Group g = new Group();
            g.setId(gqr.getId());
            role.getGroups().add(g);
            
            ArrayList<GroupQualifiedRoleAttribute> gqrAttribs = gqr.getQualifiedRoleAttributes();
            for(GroupQualifiedRoleAttribute gqrAttrib : gqrAttribs) {
                role.getGroupQualifiedRoleAttributes().add(gqrAttrib);
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
            
            ArrayList<PrincipalQualifiedRoleAttribute> gqrAttribs = pqr.getQualifiedRoleAttributes();
            for(PrincipalQualifiedRoleAttribute gqrAttrib : gqrAttribs) {
                role.getPrincipalQualifiedRoleAttributes().add(gqrAttrib);
            }
        }
    }
}
