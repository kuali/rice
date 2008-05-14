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
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
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
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return super.generateMaintenanceLocks();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getBoClass()
     */
    @Override
    public Class getBoClass() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return super.getBoClass();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getBusinessObject()
     */
    @Override
    public PersistableBusinessObject getBusinessObject() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return super.getBusinessObject();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        super.prepareForSave();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        Role oldRoleBO = (Role)document.getOldMaintainableObject().getBusinessObject();
        populateGroupQualifiedRoles(oldRoleBO);
        
        Role newRoleBO = (Role)document.getNewMaintainableObject().getBusinessObject();
        populateGroupQualifiedRoles(newRoleBO);
        
        super.processAfterEdit(document, parameters);
    }
    
    /**
     * This method ...
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
                gqr.setDescription(g.getDescription());
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
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        super.processAfterNew(document, parameters);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterPost(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        super.processAfterPost(document, parameters);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        super.processAfterRetrieve();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        super.refresh(refreshCaller, fieldValues, document);
    }

    @Override
    public void saveBusinessObject() {
        Role role = (Role)getBusinessObject();

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
        
        super.saveBusinessObject();
    }

    /**
     * @see org.kuali.core.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
        /*
        // need to make sure that the UUID is populated first for later fields
        if ( fieldValues.containsKey( RicePropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) ) {
            ((UniversalUser)getBusinessObject()).setPersonUniversalIdentifier( (String)fieldValues.get( RicePropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) );
        }
        */
        return super.populateBusinessObject( fieldValues );
    }
}
