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
package org.kuali.rice.kim.document.rule;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.document.IdentityManagementKimDocument;
import org.kuali.rice.kim.document.authorization.IdentityManagementKimDocumentAuthorizer;
import org.kuali.rice.kim.rule.event.ui.AddGroupEvent;
import org.kuali.rice.kim.rule.event.ui.AddRoleEvent;
import org.kuali.rice.kim.rule.ui.AddGroupRule;
import org.kuali.rice.kim.rule.ui.AddRoleRule;
import org.kuali.rice.kim.rules.ui.PersonDocumentGroupRule;
import org.kuali.rice.kim.rules.ui.PersonDocumentRoleRule;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentRule extends TransactionalDocumentRuleBase implements AddGroupRule, AddRoleRule {

	private IdentityManagementKimDocumentAuthorizer authorizer;
	
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return true;
    }
    
    
    private boolean isPrincipalNameExist (String principalName, String principalId) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
    	boolean valid = true;
    	KimPrincipal principal = KIMServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName);
    	if (principal != null && (StringUtils.isBlank(principalId) || !principal.getPrincipalId().equals(principalId))) {
            errorMap.putError(KimConstants.PropertyNames.PRINCIPAL_NAME,RiceKeyConstants.ERROR_EXIST_PRINCIPAL_NAME, principalName);
			valid = false;
    	}
    	return valid;
    }

    private boolean validateRoleQualifier (List<PersonDocumentRole> roles ) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
    	//boolean valid = true;
		AttributeSet validationErrors = new AttributeSet();
		// TODO : "kimTypeService.validateAttributes(attributes)" is not working yet 
    	for(PersonDocumentRole role : roles ) {
	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(role.getKimRoleType().getKimTypeServiceName());
	        AttributeSet attributes = new AttributeSet();
	        for (KimTypeAttributeImpl typeAttrImpl : role.getKimRoleType().getAttributeDefinitions()) {
	        	Map<String, String> attr = new HashMap<String, String>();
	        	attr.put(typeAttrImpl.getKimAttribute().getAttributeName(), "");
	        	attributes.putAll(attr);
	        	
	        }
	        validationErrors.putAll(kimTypeService.validateAttributes(attributes));
    	}
    	if (validationErrors.isEmpty()) {
    		return true;
    	} else {
    		return false;
    	}
    }

    private boolean validActiveDatesForRole (List<PersonDocumentRole> roles ) {
    	boolean valid = true;
		int i = 0;
    	for(PersonDocumentRole role : roles ) {
			int j = 0;
    		for (KimDocumentRoleMember principal : role.getRolePrncpls()) {
    			valid &= validateActiveDate("roles["+i+"].rolePrncpls["+j+"].activeToDate",principal.getActiveFromDate(), principal.getActiveToDate());
    			j++;    			
    		}
    		i++;
    	}
    	return valid;
    }
    
    private boolean validActiveDatesForGroup (List<PersonDocumentGroup> groups ) {
    	boolean valid = true;
		int i = 0;
    	for(PersonDocumentGroup group : groups ) {
     		valid &= validateActiveDate("groups["+i+"].activeToDate",group.getActiveFromDate(), group.getActiveToDate());
    		i++;
    	}
    	return valid;
    }
    
	private boolean validateActiveDate(String errorPath, Timestamp activeFromDate, Timestamp activeToDate) {
		// TODO : do not have detail bus rule yet, so just check this for now.
		boolean valid = true;
		if (activeFromDate != null && activeToDate !=null && activeToDate.before(activeFromDate)) {
	        ErrorMap errorMap = GlobalVariables.getErrorMap();
            errorMap.putError(errorPath, RiceKeyConstants.ERROR_ACTIVE_TO_DATE_BEFORE_FROM_DATE);
            valid = false;
			
		}
		return valid;
	}
	
    public boolean processAddGroup(AddGroupEvent addGroupEvent) {
        return new PersonDocumentGroupRule().processAddGroup(addGroupEvent);    
    }

    public boolean processAddRole(AddRoleEvent addRoleEvent) {
        return new PersonDocumentRoleRule().processAddRole(addRoleEvent);    
    }

	public IdentityManagementKimDocumentAuthorizer getAuthorizer(IdentityManagementKimDocument document) {
		if ( authorizer == null ) {
			//authorizer = (IdentityManagementPersonDocumentAuthorizer)KEWServiceLocator.getDocumentTypeService().getDocumentAuthorizer(document);
		}
		return authorizer;
	}

}
