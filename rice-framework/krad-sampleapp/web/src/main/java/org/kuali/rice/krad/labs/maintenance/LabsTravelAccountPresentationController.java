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
package org.kuali.rice.krad.labs.maintenance;

import org.kuali.rice.krad.maintenance.MaintenanceViewPresentationControllerBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Labs demonstration of a maintenance presentation controller.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsTravelAccountPresentationController extends MaintenanceViewPresentationControllerBase {

    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId) {
        if ("TravelAccount-SubAccounts".equals(groupId)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId) {
        if ("TravelAccount-Basic".equals(groupId)) {
            return false;
        } else {
            return true;
        }
    }

}
