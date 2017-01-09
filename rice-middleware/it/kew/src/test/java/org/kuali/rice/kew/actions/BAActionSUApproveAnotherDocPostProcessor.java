/*
 * Copyright 2006-2015 The Kuali Foundation
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

package org.kuali.rice.kew.actions;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.framework.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.postprocessor.DefaultPostProcessor;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a post processor class used for a Blanket Approve Test
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BAActionSUApproveAnotherDocPostProcessor extends DefaultPostProcessor {

    private static final String USER_AUTH_ID = "rkirkend";

    public ProcessDocReport doActionTaken(org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent event) throws Exception {
        RouteContext context = RouteContext.getCurrentRouteContext();
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("Test", "Test1");
        context.setParameters(parameterMap);

        Integer docId = Integer.parseInt(event.getDocumentId()) - 1;
        String otherDocId = docId.toString();

        WorkflowDocument otherDocument = WorkflowDocumentFactory.loadDocument(getPrincipalId(USER_AUTH_ID), otherDocId);

        // Super User Approve the document that was created right before this one.
        otherDocument.superUserBlanketApprove("");

        RouteContext contextAfterBAOfAnotherDoc = RouteContext.getCurrentRouteContext();
        HashMap<String, String> parameterMapAfterBlanketApprove =
                (HashMap<String, String>) contextAfterBAOfAnotherDoc.getParameters();

        if (parameterMapAfterBlanketApprove.isEmpty()) {
            throw new WorkflowRuntimeException("parameterMapAfterBlanketApprove should not be empty.");
        }
        for (Map.Entry<String, String> entry : parameterMapAfterBlanketApprove.entrySet()) {
            if (entry.getValue().equals("Test1")) {
                return new ProcessDocReport(true, "");
            }
        }
        throw new WorkflowRuntimeException("The state of the RouteContext is not correct.");
    }

    private String getPrincipalId(String principalName) {
        return KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName).getPrincipalId();
    }
}
