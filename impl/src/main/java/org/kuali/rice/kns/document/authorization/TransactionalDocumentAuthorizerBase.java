/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Base class for all TransactionalDocumentAuthorizers.
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase implements TransactionalDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentAuthorizerBase.class);

    
   
    public Set getEditMode(Document d, Person u, Set<String> editModes) {
        Iterator i = editModes.iterator();
        while(i.hasNext()) {
          String editMode = (String)i.next();
          if(!canUseEditMode(d, u, editMode)){
        	  editModes.remove(editMode);
          }
        }
        
        return editModes;
    }
    
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    public Set getDocumentActionFlags(Document document, Person user, Set<String> documentActions) {
         Set docActions = super.getDocumentActionFlags(document, user, documentActions);
         if(canErrorCorrect(document, user)){
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_ERROR_CORRECT); 
         }
        //flags.setCanErrorCorrect(canErrorCorrect(document, user));
      
        return docActions;

    }
    
    /**
     * DocumentTypeAuthorizationException can be extended to customize the initiate error message
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    public void canInitiate(String documentTypeName, Person user) {
    	
    	String nameSpaceCode = KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE;
    	
    	AttributeSet permissionDetails = new AttributeSet();
    	permissionDetails.put(KimAttributes.DOCUMENT_TYPE_CODE, documentTypeName);
    	
        if(!getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), nameSpaceCode, KimConstants.PERMISSION_INITIATE_DOCUMENT, permissionDetails, null)){
        	
        	//TODO:
        	// build authorized workgroup list for error message
            Set authorizedWorkgroups = getAuthorizationService().getAuthorizedWorkgroups("initiate", documentTypeName);
            String workgroupList = StringUtils.join(authorizedWorkgroups.toArray(), ",");
            throw new DocumentInitiationAuthorizationException(new String[] {workgroupList,documentTypeName});
        }
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canAnnotate(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    public boolean canErrorCorrect(Document document, Person user){
	     return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_ERROR_CORRECT_DOCUMENT, user.getPrincipalId());
 	  	 
    }
    
    public boolean canUseEditMode(Document document, Person user,  String editMode){
	    return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_USE_TRANSACTIONAL_DOCUMENT, user.getPrincipalId());
	  	    	
    }

}
