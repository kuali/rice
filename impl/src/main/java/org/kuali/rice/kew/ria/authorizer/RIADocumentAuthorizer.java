package org.kuali.rice.kew.ria.authorizer;

import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.ria.RIAConstants;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kew.ria.service.RIAServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class RIADocumentAuthorizer extends TransactionalDocumentAuthorizerBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RIADocumentAuthorizer.class);
	
	@Override
	public Set<String> getDocumentActions(Document document, Person user,
			Set<String> documentActions) {
    	KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
    	boolean canCopy = isUserInitiator(document);
    	canCopy &= !workflowDocument.stateIsCanceled();
    	canCopy &= !workflowDocument.stateIsException();
    	canCopy &= !workflowDocument.stateIsInitiated();
    	if(canCopy) {
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_COPY);
    	}
    	
    	String documentTypeName = workflowDocument.getDocumentType();
    	try {
    		RIADocTypeMap pdfDocTypeMap = RIAServiceLocator.getRiaDocumentService().getRiaDocTypeMap(documentTypeName);
    		String[] groups = getGroups(pdfDocTypeMap.getInitGroups());
    		
    		// if groups are null fall back to the default implementation
    		if (groups == null) {
    			if(super.canInitiate(RIAConstants.GENERIC_RIA_DOCUMENT, user)) {
    				documentActions.add(KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT);
    			}
    		} else if (isAuthorized(user, groups)) {
    			documentActions.add(KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT);
    		}
    	} catch(WorkflowException we) {
			LOG.error("PROBLEM GETTING PDF DOC TYPE MAP: " + we.toString());
    	}
		return super.getDocumentActions(document, user, documentActions);
	}
	
	public boolean isUserInitiator(Document document) {
		String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
		String netId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
		return initiatorId.equalsIgnoreCase(netId);
	}
	
	// private
	
	/**
	 * Returns set of groups from given String.
	 * 
	 * @param initGroups String which represents groups in format group1,group2,....,groupN
	 * @return Array of String elements which represent group names
	 */
	private String[] getGroups(String initGroups) {
		
		if (StringUtils.isEmpty(initGroups)) {
			return null;
		}
		String[] groups = initGroups.split(",");
		return groups;
	}
	
	private boolean isAuthorized(Person person, String[] authorizedGroups) {
		if ((authorizedGroups != null) && !ArrayUtils.isEmpty(authorizedGroups)) {
			for (String groupName : authorizedGroups) {
				if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(person.getPrincipalId(), RIAConstants.DEFAULT_GROUP_NAMESPACE, groupName)) {
					return true;
				}
	    	}
		}
		return false;
	}
	
}
