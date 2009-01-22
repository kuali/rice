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
package org.kuali.rice.kim.web.struts.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupEvent;
import org.kuali.rice.kim.rule.event.ui.AddRoleEvent;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementPersonDocumentForm;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentAction extends KualiTransactionalDocumentActionBase {

	protected IdentityService identityService;
	protected ResponsibilityService responsibilityService;
	protected UiDocumentService uiDocumentService;
	
    public IdentityService getIdentityService() {
    	if ( identityService == null ) {
    		identityService = KIMServiceLocator.getIdentityService();
    	}
		return identityService;
	}

    public ResponsibilityService getResponsibilityService() {
    	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public UiDocumentService getUiDocumentService() {
		if ( uiDocumentService == null ) {
			uiDocumentService = KIMServiceLocator.getUiDocumentService();
		}
		return uiDocumentService;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward;
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        if (findMethodToCall(form, request) == null) {
        	forward = mapping.findForward(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        } else {
        	if (isSaveRouteMethodCall(findMethodToCall(form, request))) {
        		preSaveSubmitCheck(personDocumentForm.getPersonDocument());
        	}
        	forward =  super.execute(mapping, form, request, response);
        }
		// move the following to service
		// get set up person document
        String commandParam = request.getParameter(KNSConstants.PARAMETER_COMMAND);
		if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KEWConstants.INITIATE_COMMAND) && StringUtils.isNotBlank(request.getParameter(KIMPropertyConstants.Person.PRINCIPAL_ID))) {
			getUiDocumentService().loadEntityToPersonDoc((IdentityManagementPersonDocument)personDocumentForm.getDocument(), request.getParameter(KIMPropertyConstants.Person.PRINCIPAL_ID));
		}
		if (StringUtils.isNotBlank(commandParam) && (commandParam.equals(KEWConstants.DOCSEARCH_COMMAND) || commandParam.equals(KEWConstants.ACTIONLIST_COMMAND))) {
			IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument)personDocumentForm.getDocument();
			for (PersonDocumentRole role : personDoc.getRoles()) {
		        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(getKimTypeServiceName(role.getKimRoleType()));
				role.setDefinitions(kimTypeService.getAttributeDefinitions(role.getKimRoleType()));
	        	// when post again, it will need this during populate
	            role.setNewRolePrncpl(new KimDocumentRoleMember());
	            for (String key : role.getDefinitions().keySet()) {
	            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	            	//qualifier.setQualifierKey(key);
		        	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
	            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
	            }
		        role.setAttributeEntry( getUiDocumentService().getAttributeEntries( role.getDefinitions() ) );
			}
		}

		return forward;
    }
    
    /**
     * 
     * This overridden method is to add 'kim/" to the return path
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	@Override
	public ActionForward performLookup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward =  super.performLookup(mapping, form, request, response);
		String path = forward.getPath();
		// don't need context so can't call getbasepath()
		String basePath = request.getScheme() + "://" + request.getServerName();
		// EBO does not have base path for lookup in rice
		// rice  has 'kr.url' as '/${env}/kr' while kfs is full base path
		// the returnlocalurl may have 'http' so, it should start from the beginning
		// this is kind of hack
//		if (path.indexOf(request.getScheme()) != 0 && path.indexOf("lookup.do") > 0) {
//			if (request.getServerPort() == 443) {
//				path = request.getScheme() + "://" + request.getServerName()+path;
//			} else {
//				path = request.getScheme() + "://" + request.getServerName()+ ":" + request.getServerPort()+path;
//			}
//		}
		path = path.replace("identityManagementPersonDocument.do", "kim/identityManagementPersonDocument.do");
		forward.setPath(path);
		return forward;
	}

	public ActionForward addAffln(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentAffiliation newAffln = personDocumentForm.getNewAffln();
        newAffln.setDocumentNumber(personDocumentForm.getPersonDocument().getDocumentNumber());
        newAffln.refreshReferenceObject("affiliationType");
        personDocumentForm.getPersonDocument().getAffiliations().add(newAffln);
        personDocumentForm.setNewAffln(new PersonDocumentAffiliation());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
	
    public ActionForward deleteAffln(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getAffiliations().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    public ActionForward addCitizenship(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentCitizenship newCitizenship = personDocumentForm.getNewCitizenship();
        personDocumentForm.getPersonDocument().getCitizenships().add(newCitizenship);
        personDocumentForm.setNewCitizenship(new PersonDocumentCitizenship());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteCitizenship(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getCitizenships().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addEmpInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDOc = personDocumentForm.getPersonDocument();
        PersonDocumentAffiliation affiliation = personDOc.getAffiliations().get(getSelectedLine(request));        
        PersonDocumentEmploymentInfo newempInfo = affiliation.getNewEmpInfo();
        newempInfo.setDocumentNumber(personDOc.getDocumentNumber());
        newempInfo.setVersionNumber(new Long(1));
        affiliation.getEmpInfos().add(newempInfo);
        affiliation.setNewEmpInfo(new PersonDocumentEmploymentInfo());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteEmpInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        String selectedIndexes = getSelectedParentChildIdx(request);
        if (selectedIndexes != null) {
	        String [] indexes = StringUtils.split(selectedIndexes,":");
	        PersonDocumentAffiliation affiliation = personDocumentForm.getPersonDocument().getAffiliations().get(Integer.parseInt(indexes[0]));
	        affiliation.getEmpInfos().remove(Integer.parseInt(indexes[1]));
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    private String getSelectedParentChildIdx(HttpServletRequest request) {
    	String lineNumber = null;
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lineNumber = StringUtils.substringBetween(parameterName, ".line", ".");
        }
        return lineNumber;
    }

    public ActionForward addName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentName newName = personDocumentForm.getNewName();
        newName.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getNames().add(newName);
        personDocumentForm.setNewName(new PersonDocumentName());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getNames().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentAddress newAddress = personDocumentForm.getNewAddress();
        newAddress.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getAddrs().add(newAddress);
        personDocumentForm.setNewAddress(new PersonDocumentAddress());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getAddrs().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addPhone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentPhone newPhone = personDocumentForm.getNewPhone();
        newPhone.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getPhones().add(newPhone);
        personDocumentForm.setNewPhone(new PersonDocumentPhone());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deletePhone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getPhones().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentEmail newEmail = personDocumentForm.getNewEmail();
        newEmail.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getEmails().add(newEmail);
        personDocumentForm.setNewEmail(new PersonDocumentEmail());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getEmails().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentGroup newGroup = personDocumentForm.getNewGroup();
        if (getKualiRuleService().applyRules(new AddGroupEvent("",personDocumentForm.getPersonDocument(), newGroup))) {
	        personDocumentForm.getPersonDocument().getGroups().add(newGroup);
	        personDocumentForm.setNewGroup(new PersonDocumentGroup());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getGroups().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentRole newRole = personDocumentForm.getNewRole();
        if (getKualiRuleService().applyRules(new AddRoleEvent("",personDocumentForm.getPersonDocument(), newRole))) {
	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(getKimTypeServiceName(newRole.getKimRoleType()));
	        //AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions();
	        // role type populated from form is not a complete record
	        newRole.getKimRoleType().setKimTypeId(newRole.getKimTypeId());
	        newRole.getKimRoleType().refreshReferenceObject("attributeDefinitions");
	        newRole.setDefinitions(kimTypeService.getAttributeDefinitions(newRole.getKimRoleType()));
	        KimDocumentRoleMember newRolePrncpl = new KimDocumentRoleMember();
	        newRole.refreshReferenceObject("assignedResponsibilities");
	        
	        for (String key : newRole.getDefinitions().keySet()) {
	        	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	        	//qualifier.setQualifierKey(key);
	        	setAttrDefnIdForQualifier(qualifier,newRole.getDefinitions().get(key));
	        	newRolePrncpl.getQualifiers().add(qualifier);
	        }
	        if (newRole.getDefinitions().isEmpty()) {
	        	List<KimDocumentRoleMember> rolePrncpls = new ArrayList<KimDocumentRoleMember>();
	        	setupRoleRspActions(newRole, newRolePrncpl);
	            rolePrncpls.add(newRolePrncpl);
	        	newRole.setRolePrncpls(rolePrncpls);
	        }
	        newRole.setNewRolePrncpl(newRolePrncpl);
	        newRole.setAttributeEntry( getUiDocumentService().getAttributeEntries( newRole.getDefinitions() ) );
	        personDocumentForm.getPersonDocument().getRoles().add(newRole);
	        personDocumentForm.setNewRole(new PersonDocumentRole());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    private void setupRoleRspActions(PersonDocumentRole role, KimDocumentRoleMember rolePrncpl) {
        for (RoleResponsibilityImpl roleResp : role.getAssignedResponsibilities()) {
        	if (getResponsibilityService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
        		KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
        		roleRspAction.setRoleResponsibilityId(roleResp.getRoleResponsibilityId());        		
        		roleRspAction.refreshReferenceObject("roleResponsibility");
        		rolePrncpl.getRoleRspActions().add(roleRspAction);
        	}        	
        }
    }
    
    private void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier,AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		qualifier.setKimAttrDefnId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");
    	} else {
    		qualifier.setKimAttrDefnId(((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");

    	}
    }
    
    public ActionForward deleteRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getRoles().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addRoleQualifier(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDOc = personDocumentForm.getPersonDocument();
        PersonDocumentRole role = personDOc.getRoles().get(getSelectedLine(request));
        KimDocumentRoleMember newRolePrncpl = role.getNewRolePrncpl();
    	setupRoleRspActions(role, newRolePrncpl);
        role.getRolePrncpls().add(newRolePrncpl);
        role.setNewRolePrncpl(new KimDocumentRoleMember());
        for (String key : role.getDefinitions().keySet()) {
        	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
        	//qualifier.setQualifierKey(key);
        	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
        	role.getNewRolePrncpl().getQualifiers().add(qualifier);
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoleQualifier(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        String selectedIndexes = getSelectedParentChildIdx(request);
        if (selectedIndexes != null) {
	        String [] indexes = StringUtils.split(selectedIndexes,":");
	        PersonDocumentRole role = personDocumentForm.getPersonDocument().getRoles().get(Integer.parseInt(indexes[0]));
	        role.getRolePrncpls().remove(Integer.parseInt(indexes[1]));
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
	@Override
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return super.save(mapping, form, request, response);
	}

	private void preSaveSubmitCheck(IdentityManagementPersonDocument personDoc) {

		if (StringUtils.isBlank(personDoc.getPrivacy().getDocumentNumber())) {
			personDoc.getPrivacy().setDocumentNumber(
					personDoc.getDocumentNumber());
		}
		for (PersonDocumentRole role : personDoc.getRoles()) {
			for (KimDocumentRoleMember rolePrncpl : role.getRolePrncpls()) {
				rolePrncpl.setDocumentNumber(personDoc.getDocumentNumber());
				for (KimDocumentRoleQualifier qualifier : rolePrncpl
						.getQualifiers()) {
					qualifier.setDocumentNumber(personDoc.getDocumentNumber());
					qualifier.setKimTypId(role.getKimTypeId());
				}
			}
		}
		
	}
	
	private boolean isSaveRouteMethodCall(String methodToCall) {
		String[] methods = new String[] {"save","route","approve","blanketApprove"};
		return Arrays.asList(methods).contains(methodToCall);   
	}
	
	private String getKimTypeServiceName (KimTypeImpl kimType) {
    	String serviceName = kimType.getKimTypeServiceName();
    	if (StringUtils.isBlank(serviceName)) {
    		serviceName = "kimTypeService";
    	}
    	return serviceName;

	}
}
