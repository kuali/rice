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
package org.kuali.rice.krad.inquiry;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizerBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.InquiryForm;

/**
 * Implementation of {@link org.kuali.rice.krad.uif.view.ViewAuthorizer} for
 * {@link org.kuali.rice.krad.uif.view.InquiryView} instances
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryViewAuthorizerBase extends ViewAuthorizerBase {
    private static final long serialVersionUID = 5853518191618440332L;


    /**
     * Augmenting the base Open View check with an additional check against the KR-NS / Inquire Into Records
     * permission template.
     *
     * This check will fail if the user is not allowed by *either* the View
     *
     * @see org.kuali.rice.krad.uif.view.ViewAuthorizerBase#canOpenView(org.kuali.rice.krad.uif.view.View, org.kuali.rice.krad.uif.view.ViewModel, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        boolean canOpenViewPerViewId = super.canOpenView(view, model, user);
        // if the user is blocked out of the view by it's ID, we'll respect that and stop access here
        if ( !canOpenViewPerViewId ) {
            return false;
        }

        // If we get here - then the view permission is not blocking access - so we check the KNS inquiry permission
        if ( model instanceof InquiryForm ) {
            InquiryForm inquiryForm = (InquiryForm) model;
            if ( inquiryForm.getDataObject() != null ) {
                // but - we only block if a permission which handles this data object exists
                // at some level
                if ( permissionExistsByTemplate(inquiryForm.getDataObject(),
                        KRADConstants.KNS_NAMESPACE,
                        KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS ) ) {

                    if ( !isAuthorizedByTemplate( inquiryForm.getDataObject(),
                            KRADConstants.KNS_NAMESPACE,
                            KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
                            GlobalVariables.getUserSession().getPrincipalId() ) ) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
