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
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.web.form.RoleQualificationForPrincipal;

/**
 * This maintainable class helps with the lifecycle of a maintenance document for the Principal maintenance screen.  
 * It specifically overrides the parent to handle transferring data about role qualifications between UI specific BOs and 
 * persistence specific BOs.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalMaintainable extends KualiMaintainableImpl {
    /**
     * This overridden method handles populating the role qualification helper objects for proper UI rendering.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        Principal oldPrincipalBO = (Principal)document.getOldMaintainableObject().getBusinessObject();
        populateRoleQualifications(oldPrincipalBO);
        
        Principal newPrincipalBO = (Principal)document.getNewMaintainableObject().getBusinessObject();
        populateRoleQualifications(newPrincipalBO);
        
        super.processAfterEdit(document, parameters);
    }
    
    /**
	 * This overridden method handles populating the role qualification helper objects for proper UI rendering.
	 * 
	 * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.core.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterCopy(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		Principal oldPrincipalBO = (Principal)document.getOldMaintainableObject().getBusinessObject();
        populateRoleQualifications(oldPrincipalBO);
        
        Principal newPrincipalBO = (Principal)document.getOldMaintainableObject().getBusinessObject();
        populateRoleQualifications(newPrincipalBO);
        
		super.processAfterCopy(document, parameters);
	}



	/**
     * This overridden method deals with translating the data from the maint. doc UI into the appropriate
     * persistable business objects that map down to the ORM level.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        Principal principal = (Principal)getBusinessObject();

        preparePrincipalQualifiedRoleAttributesForSave(principal);
        
        super.saveBusinessObject();
    }
    
    /**
     * This method is responsible for taking the persisted principal qualified role attributes and pushing them into 
     * the appropriate value added helper bos for displaying in the maintenance document user interface.
     * 
     * @param principal
     */
    private void populateRoleQualifications(Principal principal) {
            principal.getRoleQualificationsForPrincipal().clear();

            ArrayList<Role> roles = principal.getRoles();
            for (Role r : roles) {
                RoleQualificationForPrincipal rqfp = new RoleQualificationForPrincipal();
                rqfp.setId(r.getId());
                rqfp.setName(r.getName());
                rqfp.setPrincipalId(principal.getId());

                ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = principal.getPrincipalQualifiedRoleAttributes();
                for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
                    if(pqrAttrib.getRoleId().equals(rqfp.getId())) {
                        rqfp.getQualifiedRoleAttributes().add(pqrAttrib);
                    }
                }
                
                principal.getRoleQualificationsForPrincipal().add(rqfp);
            }
    }

    /**
     * This method will extract the data entered into the UI and populate that into the persistable BOs that will
     * be used for actually storing the object.
     * 
     * @param role
     */
    private void preparePrincipalQualifiedRoleAttributesForSave(Principal principal) {
        ArrayList<RoleQualificationForPrincipal> roleQualificationsForPrincipal = principal.getRoleQualificationsForPrincipal();

        principal.getRoles().clear();
        principal.getPrincipalQualifiedRoleAttributes().clear();
        
        // construct the list of principals to save through the persistable list of roles on the principal BO
        for(RoleQualificationForPrincipal rqfp : roleQualificationsForPrincipal) {
            Role r = new Role();
            r.setId(rqfp.getId());
            principal.getRoles().add(r);
            
            ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = rqfp.getQualifiedRoleAttributes();
            for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
                pqrAttrib.setRoleId(rqfp.getId());
                pqrAttrib.setPrincipalId(principal.getId());
                principal.getPrincipalQualifiedRoleAttributes().add(pqrAttrib);
            }
        }
    }
}
