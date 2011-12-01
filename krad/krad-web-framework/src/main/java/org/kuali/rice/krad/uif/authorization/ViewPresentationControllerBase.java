/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.authorization;

import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewPresentationControllerBase implements ViewPresentationController {

    /**
     * @see ViewPresentationController#getActionFlags(org.kuali.rice.krad.web.form.UifFormBase)
     */
    public Set<String> getActionFlags(UifFormBase model) {
        Set<String> actions = new HashSet<String>();

        actions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);

        return actions;
    }

    /**
     * @see ViewPresentationController#getEditModes(org.kuali.rice.krad.web.form.UifFormBase)
     */
    public Set<String> getEditModes(UifFormBase model) {
        return new HashSet<String>();
    }

}
