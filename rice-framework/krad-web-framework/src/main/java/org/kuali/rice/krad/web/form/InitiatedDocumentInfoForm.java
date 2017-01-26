/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.web.form;

import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Controller for <code>MaintenanceDocumentView</code> screens which operate on
 * <code>MaintenanceDocument</code> instances
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitiatedDocumentInfoForm extends UifFormBase {

    public InitiatedDocumentInfoForm() {
        super();
        view = KRADServiceLocatorWeb.getViewService().getViewById(KRADConstants.KRAD_INITIATED_DOCUMENT_VIEW_NAME);
    }

}
