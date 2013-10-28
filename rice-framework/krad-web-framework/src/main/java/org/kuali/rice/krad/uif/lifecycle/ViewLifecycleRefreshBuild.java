/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.uif.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * TODO mark don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

            if (StringUtils.isBlank(lookupCollectionName)) {
                throw new RuntimeException(
                        "Lookup collection name is required for processing multi-value lookup results");
            }

            String selectedLineValues = "";
            if (request.getParameterMap().containsKey(UifParameters.SELECTED_LINE_VALUES)) {
                selectedLineValues = request.getParameter(UifParameters.SELECTED_LINE_VALUES);
            }
            if (!StringUtils.isBlank(flashMapSelectedLineValues)) {
                selectedLineValues = flashMapSelectedLineValues;
            }

            // invoked view helper to populate the collection from lookup results
            ViewLifecycle.getHelper().processMultipleValueLookupResults(form.getPostedView(), form,
                    lookupCollectionName, selectedLineValues);
        }

        // refresh references
        if (request.getParameterMap().containsKey(KRADConstants.REFERENCES_TO_REFRESH)) {
            String referencesToRefresh = request.getParameter(KRADConstants.REFERENCES_TO_REFRESH);

            ViewLifecycle.getHelper().refreshReferences(referencesToRefresh);
        }

        // set focus and jump position for returning from a quickfinder
        if (request.getParameterMap().containsKey(UifParameters.QUICKFINDER_ID)) {
            String quickfinderId = request.getParameter(UifParameters.QUICKFINDER_ID);

            String focusId = (String) view.getViewIndex().getPostContextEntry(quickfinderId,
                    UifConstants.PostContextKeys.QUICKFINDER_FOCUS_ID);
            if (StringUtils.isNotBlank(focusId)) {
                form.setFocusId(focusId);
            }

            String jumpToId = (String) view.getViewIndex().getPostContextEntry(quickfinderId,
                    UifConstants.PostContextKeys.QUICKFINDER_JUMP_TO_ID);
            if (StringUtils.isNotBlank(jumpToId)) {
                form.setJumpToId(jumpToId);
            }
        }

    }
}
