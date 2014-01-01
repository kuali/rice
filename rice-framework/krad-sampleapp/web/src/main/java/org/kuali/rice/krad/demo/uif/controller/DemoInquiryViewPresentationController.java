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
package org.kuali.rice.krad.demo.uif.controller;

import org.kuali.rice.krad.inquiry.InquiryViewPresentationControllerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Created with IntelliJ IDEA.
 * User: jruch
 * Date: 11/4/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DemoInquiryViewPresentationController extends InquiryViewPresentationControllerBase {


    @Override
    public boolean canViewField(View view, ViewModel model, Field field, String propertyName) {

        //hide travel authorization number
        if(propertyName.equals("travelAuthorizationDocumentId"))   {
            return false;
        }

        return super.canViewField(view,model,field,propertyName);
    }

    @Override
    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId) {

        //hide the estimates section
        if(groupId.equals("TravelAccount-InquiryView-Costs"))  {
            return false;
        }

        return super.canViewGroup(view, model,group,groupId);
    }

}
