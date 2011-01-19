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
package org.kuali.rice.kns.web.spring.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.spring.form.UifFormBase;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class UifControllerBase {

    private Set<String> methodToCallsToNotCheckAuthorization = new HashSet<String>();
    {
        methodToCallsToNotCheckAuthorization.add("performLookup");
        methodToCallsToNotCheckAuthorization.add("performQuestion");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithInput");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithInputAgainBecauseOfErrors");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithoutInput");
        methodToCallsToNotCheckAuthorization.add("performWorkgroupLookup");
    }
    
    /**
     * Use to add a methodToCall to the a list which will not have authorization checks.
     * This assumes that the call will be redirected (as in the case of a lookup) that will perform
     * the authorization.
     */
    protected final void addMethodToCallToUncheckedList( String methodToCall ) {
        methodToCallsToNotCheckAuthorization.add(methodToCall);
    }
    
    /**
     * Returns an immutable Set of methodToCall parameters that should not be checked
     * for authorization.
     * 
     * @return
     */
    public Set<String> getMethodToCallsToNotCheckAuthorization() {
        return Collections.unmodifiableSet(methodToCallsToNotCheckAuthorization);
    }

    /**
     * Override this method to provide controller class-level access controls to the application.
     *
     * @param form
     * @throws AuthorizationException
     */
    public void checkAuthorization(UifFormBase form, String methodToCall) throws AuthorizationException 
    {
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        AttributeSet roleQualifier = new AttributeSet(getRoleQualification(form, methodToCall));
        AttributeSet permissionDetails = KimCommonUtils.getNamespaceAndActionClass(this.getClass());
        
        if (!KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(principalId, KNSConstants.KNS_NAMESPACE, 
                KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, roleQualifier )) 
        {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), 
                    methodToCall,
                    this.getClass().getSimpleName());
        }
    }
    
    /** 
     * override this method to add data from the form for role qualification in the authorization check
     * @param form 
     * @param methodToCall 
     */
    protected Map<String,String> getRoleQualification(UifFormBase form, String methodToCall) {
        return new HashMap<String,String>();
    }

}
