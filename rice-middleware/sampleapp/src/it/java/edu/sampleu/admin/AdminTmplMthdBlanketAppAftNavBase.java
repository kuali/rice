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
package edu.sampleu.admin;

import edu.sampleu.common.NavTemplateMethodAftBase;

/**
 * blanket approving a new document, results in a final document
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @Deprecated use AdminTmplMthdAftNavBlanketAppBase
 */
public abstract class AdminTmplMthdBlanketAppAftNavBase extends NavTemplateMethodAftBase {

    @Override
    protected String getMenuLinkLocator() {
        return AdminTmplMthdAftNavBase.ADMIN_LOCATOR;
    }

    @Override
    protected String getCreateNewLinkLocator() {
        return AdminTmplMthdAftNavBase.CREATE_NEW_LOCATOR;
    }
}
