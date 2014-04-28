/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.lifecycle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Lifecycle processing task for encapsulating a view refresh. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycle#encapsulateLifecycle(View, Object, HttpServletRequest, javax.servlet.http.HttpServletResponse, Runnable)
 * @see UifControllerBase#refresh(UifFormBase, org.springframework.validation.BindingResult, HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
public class ViewLifecycleRefreshBuild implements Runnable {

    @Override
    public void run() {
        View view = ViewLifecycle.getView();
        UifFormBase form = (UifFormBase) ViewLifecycle.getModel();
        HttpServletRequest request = ViewLifecycle.getRequest();
        
        String flashMapSelectedLineValues = "";
        if (RequestContextUtils.getInputFlashMap(request) != null) {
            flashMapSelectedLineValues = (String) RequestContextUtils.getInputFlashMap(request).get(
                    UifParameters.SELECTED_LINE_VALUES);
        }

        String refreshCallerType = "";
        if (request.getParameterMap().containsKey(KRADConstants.REFRESH_CALLER_TYPE)) {
            refreshCallerType = request.getParameter(KRADConstants.REFRESH_CALLER_TYPE);
        }

        // process multi-value lookup returns
        if (StringUtils.equals(refreshCallerType, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP)) {
            String lookupCollectionName = "";
            if (request.getParameterMap().containsKey(UifParameters.LOOKUP_COLLECTION_NAME)) {
                lookupCollectionName = request.getParameter(UifParameters.LOOKUP_COLLECTION_NAME);
            }

            String lookupCollectionId = "";
            if (request.getParameterMap().containsKey(UifParameters.LOOKUP_COLLECTION_ID)) {
                lookupCollectionId = request.getParameter(UifParameters.LOOKUP_COLLECTION_ID);
            }

            if (StringUtils.isBlank(lookupCollectionName)) {
                throw new RuntimeException(
                        "Lookup collection name is required for processing multi-value lookup results");
            }

            String multiValueReturnFields ="";
            if (request.getParameterMap().containsKey(UifParameters.MULIT_VALUE_RETURN_FILEDS)) {
                multiValueReturnFields = request.getParameter(UifParameters.MULIT_VALUE_RETURN_FILEDS);
            }

            String selectedLineValues = "";
            if (request.getParameterMap().containsKey(UifParameters.SELECTED_LINE_VALUES)) {
                selectedLineValues = request.getParameter(UifParameters.SELECTED_LINE_VALUES);
            }
            if (!StringUtils.isBlank(flashMapSelectedLineValues)) {
                selectedLineValues = flashMapSelectedLineValues;
            }

            // invoked view helper to populate the collection from lookup results
            ViewLifecycle.getHelper().processMultipleValueLookupResults(form, lookupCollectionId,
                    lookupCollectionName, multiValueReturnFields, selectedLineValues);
        }

        // refresh references
        if (request.getParameterMap().containsKey(KRADConstants.REFERENCES_TO_REFRESH)) {
            String referencesToRefresh = request.getParameter(KRADConstants.REFERENCES_TO_REFRESH);

            ViewLifecycle.getHelper().refreshReferences(referencesToRefresh);
        }

        // set focus and jump position for returning from a quickfinder
        // check and invoke callback method
        if (request.getParameterMap().containsKey(UifParameters.QUICKFINDER_ID)) {
            String quickfinderId = request.getParameter(UifParameters.QUICKFINDER_ID);

            String focusId = (String) form.getViewPostMetadata().getComponentPostData(quickfinderId,
                                UifConstants.PostMetadata.QUICKFINDER_FOCUS_ID);
            if (StringUtils.isNotBlank(focusId)) {
                form.setFocusId(focusId);
            }

            String jumpToId = (String) form.getViewPostMetadata().getComponentPostData(quickfinderId,
                                UifConstants.PostMetadata.QUICKFINDER_JUMP_TO_ID);
            if (StringUtils.isNotBlank(jumpToId)) {
                form.setJumpToId(jumpToId);
            }

            // check for callback method and invoke it if present
            String callbackMethodToCall = ( String ) form.getViewPostMetadata().getComponentPostData( quickfinderId,
                    UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD_TO_CALL );
            MethodInvokerConfig callbackMethod = ( MethodInvokerConfig ) form.getViewPostMetadata().
                    getComponentPostData( quickfinderId, UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD );

            if( StringUtils.isNotBlank( callbackMethodToCall ) || callbackMethod != null ) {
                // if the callbackMethod is not set, then we set it
                if( callbackMethod == null ) {
                    callbackMethod = new MethodInvokerConfig();
                }

                // get additional parameters to be passed to the callback method
                Map<String, String> callbackContext = ( Map<String, String> ) form.getViewPostMetadata().
                        getComponentPostData( quickfinderId, UifConstants.PostMetadata.QUICKFINDER_CALLBACK_CONTEXT );

                // if target class or object not set, use view helper service
                if( ( callbackMethod.getTargetClass() == null ) && ( callbackMethod.getTargetObject() == null ) ) {
                    callbackMethod.setTargetObject( ViewLifecycle.getHelper() );
                }

                callbackMethod.setTargetMethod( callbackMethodToCall );
                Object[] arguments = new Object[3];
                arguments[0] = form;
                arguments[1] = quickfinderId;
                arguments[2] = callbackContext;
                callbackMethod.setArguments( arguments );

                // invoke callback method
                try {
                    callbackMethod.prepare();

                    Class<?> methodReturnType = callbackMethod.getPreparedMethod().getReturnType();
                    if( StringUtils.equals( "void", methodReturnType.getName() ) ) {
                        callbackMethod.invoke();
                    } else {
                        // TODO : can the return type be anything else other than void? if so, what?
                    }
                } catch( Exception e ) {
                    throw new RuntimeException( "Error invoking callback method for quickfinder: " + quickfinderId, e );
                }
            }
        }

    }
}
