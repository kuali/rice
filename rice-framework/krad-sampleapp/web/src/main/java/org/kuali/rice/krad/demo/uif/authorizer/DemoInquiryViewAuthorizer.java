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
package org.kuali.rice.krad.demo.uif.authorizer;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.inquiry.InquiryViewAuthorizerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * The DemoInquiryViewAuthorizer is used to demonstrate the ability to  to control the visibility
 * and masking of sections and fields based on permission checks.
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoInquiryViewAuthorizer extends InquiryViewAuthorizerBase {

    @Override
    public boolean canViewField(View view, ViewModel model, Field field, String propertyName, Person user)
    {
       //hide travel authorization number from admin
        if(propertyName.equals("travelAuthorizationDocumentId") &&
                user.getPrincipalName().equals("admin"))   {
            return false;
        }

        return super.canViewField(view,model,field,propertyName,user);
    }

    @Override
    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId, Person user) {

        //hide the estimates section if the user is admin
        if(groupId.equals("TravelAccount-InquiryView-Costs") &&
                user.getPrincipalName().equals("admin"))  {
            return false;
        }

        return super.canViewGroup(view,model,group,groupId,user);
    }





}
