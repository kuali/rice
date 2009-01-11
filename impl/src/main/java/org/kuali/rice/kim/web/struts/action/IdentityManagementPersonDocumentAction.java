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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.PersonDocumentRolePrncpl;
import org.kuali.rice.kim.bo.ui.PersonDocumentRoleQualifier;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupEvent;
import org.kuali.rice.kim.rule.event.ui.AddRoleEvent;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementPersonDocumentForm;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentAction extends KualiTransactionalDocumentActionBase {

	
    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
       // String methodToCall = findMethodToCall(form, request);
		ActionForward forward;
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument)personDocumentForm.getDocument();
        if (findMethodToCall(form, request) == null) {
        	forward = mapping.findForward(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        } else {
    		if (StringUtils.isBlank(personDoc.getPrivacy().getDocumentNumber())) {
    			// need this if submit without saving
    			personDoc.getPrivacy().setDocumentNumber(personDoc.getDocumentNumber());
    		}

        	forward =  super.execute(mapping, form, request, response);
        }
		// move the following to service
		// get set up person document
        String commandParam = request.getParameter(KNSConstants.PARAMETER_COMMAND);
		if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KEWConstants.INITIATE_COMMAND) && StringUtils.isNotBlank(request.getParameter(KimConstants.PropertyNames.PRINCIPAL_ID))) {
	        KimPrincipal principal = KIMServiceLocator.getIdentityService().getPrincipal(request.getParameter(KimConstants.PropertyNames.PRINCIPAL_ID));
	        personDoc.setPrincipalId(principal.getPrincipalId());
	        personDoc.setPrincipalName(principal.getPrincipalName());
	        personDoc.setPassword(principal.getPassword());
			KimEntityImpl entity = (KimEntityImpl)KIMServiceLocator.getIdentityService().getEntity(principal.getEntityId());
			KIMServiceLocator.getUiDocumentService().loadEntityToPersonDoc(personDoc, entity);
			//List<? extends KimGroup> groups = KIMServiceLocator.getIdentityManagementService().getGroupsForPrincipal(principal.getPrincipalId());
			//KIMServiceLocator.getUiDocumentService().loadGroupToPersonDoc(personDoc, groups);
		}
		if (StringUtils.isNotBlank(commandParam) && (commandParam.equals(KEWConstants.DOCSEARCH_COMMAND) || commandParam.equals(KEWConstants.ACTIONLIST_COMMAND))) {
			for (PersonDocumentRole role : personDoc.getRoles()) {
		        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(getKimTypeServiceName(role.getKimRoleType()));
				role.setDefinitions(kimTypeService.getAttributeDefinitions(role.getKimRoleType()));
				// TODO : refactor qualifier key to connect between defn & qualifier
	        	for (PersonDocumentRolePrncpl principal : role.getRolePrncpls()) {
	        		for (PersonDocumentRoleQualifier qualifier : principal.getQualifiers()) {
	    		        for (KimTypeAttributeImpl attrDef : role.getKimRoleType().getAttributeDefinitions()) {
	    		        	if (qualifier.getKimAttrDefnId().equals(attrDef.getKimAttributeId())) {
	    		        		qualifier.setQualifierKey(attrDef.getSortCode());
	    		        	}
	    		        }
	        			
	        		}
	        	}
	        	// when post again, it will need this during populate
	            role.setNewRolePrncpl(new PersonDocumentRolePrncpl());
	            for (String key : role.getDefinitions().keySet()) {
	            	PersonDocumentRoleQualifier qualifier = new PersonDocumentRoleQualifier();
	            	qualifier.setQualifierKey(key);
	            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
	            }

		        KIMServiceLocator.getUiDocumentService().setAttributeEntry(role);

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
		if (path.indexOf(request.getScheme()) != 0 && path.indexOf("lookup.do") > 0) {
			if (request.getServerPort() == 443) {
				path = request.getScheme() + "://" + request.getServerName()+path;
			} else {
				path = request.getScheme() + "://" + request.getServerName()+ ":" + request.getServerPort()+path;
			}
		}
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
        // TODO : error msg if not found ?
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
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddGroupEvent("",personDocumentForm.getPersonDocument(), newGroup))) {
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
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddRoleEvent("",personDocumentForm.getPersonDocument(), newRole))) {
	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(getKimTypeServiceName(newRole.getKimRoleType()));
	        //AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions();
	        // role type populated from form is not a complete record
	        newRole.getKimRoleType().setKimTypeId(newRole.getKimTypeId());
	        newRole.getKimRoleType().refreshReferenceObject("attributeDefinitions");
	        newRole.setDefinitions(kimTypeService.getAttributeDefinitions(newRole.getKimRoleType()));
	        PersonDocumentRolePrncpl newRolePrncpl = new PersonDocumentRolePrncpl();
	        
	        for (String key : newRole.getDefinitions().keySet()) {
	        	PersonDocumentRoleQualifier qualifier = new PersonDocumentRoleQualifier();
	        	qualifier.setQualifierKey(key);
	        	newRolePrncpl.getQualifiers().add(qualifier);
	        }
	        if (newRole.getDefinitions().isEmpty()) {
	        	List<PersonDocumentRolePrncpl> rolePrncpls = new ArrayList<PersonDocumentRolePrncpl>();
	        	rolePrncpls.add(new PersonDocumentRolePrncpl());
	        	newRole.setRolePrncpls(rolePrncpls);
	        }
	        newRole.setNewRolePrncpl(newRolePrncpl);
	        KIMServiceLocator.getUiDocumentService().setAttributeEntry(newRole);
	        personDocumentForm.getPersonDocument().getRoles().add(newRole);
	        personDocumentForm.setNewRole(new PersonDocumentRole());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        PersonDocumentRolePrncpl newRolePrncpl = role.getNewRolePrncpl();
        role.getRolePrncpls().add(newRolePrncpl);
        role.setNewRolePrncpl(new PersonDocumentRolePrncpl());
        for (String key : role.getDefinitions().keySet()) {
        	PersonDocumentRoleQualifier qualifier = new PersonDocumentRoleQualifier();
        	qualifier.setQualifierKey(key);
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
        // TODO : error msg if not found ?
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
	@Override
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDoc = personDocumentForm.getPersonDocument();
//		if (StringUtils.isBlank(personDoc.getPrivacy().getDocumentNumber())) {
//			personDoc.getPrivacy().setDocumentNumber(personDoc.getDocumentNumber());
//		}
		// TODO : refactor this, also probably move to service ?
		for (PersonDocumentRole role : personDoc.getRoles()) {
			for(PersonDocumentRolePrncpl rolePrncpl : role.getRolePrncpls()) {
				rolePrncpl.setDocumentNumber(personDoc.getDocumentNumber());
				for (PersonDocumentRoleQualifier qualifier : rolePrncpl.getQualifiers()) {
					qualifier.setDocumentNumber(personDoc.getDocumentNumber());	
					qualifier.setKimTypId(role.getKimTypeId());
					//qualifier.getQualifierKey().substring(qualifier.getQualifierKey().indexOf(".")+1, qualifier.getQualifierKey().length());
					// TODO : need rework to set attributedefid
					for (KimTypeAttributeImpl attr : role.getKimRoleType().getAttributeDefinitions()) {
						if (attr.getSortCode().equals(qualifier.getQualifierKey())) {
							qualifier.setKimAttrDefnId(attr.getKimAttributeId());
						}
					}
				}
			}
		}
		return super.save(mapping, form, request, response);
	}

	private String getKimTypeServiceName (KimTypeImpl kimType) {
    	String serviceName = kimType.getKimTypeServiceName();
    	if (StringUtils.isBlank(serviceName)) {
    		serviceName = "kimTypeService";
    	}
    	return serviceName;

	}
}
