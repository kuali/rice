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
package org.kuali.rice.krad.lookup;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.impl.CollectionControllerServiceImpl;
import org.springframework.web.servlet.ModelAndView;

/**
 * Override of the default collection controller service to maintain selected line state.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupCollectionControllerServiceImpl extends CollectionControllerServiceImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView retrieveCollectionPage(UifFormBase form) {
        LookupUtils.refreshLookupResultSelections((LookupForm) form);

        return super.retrieveCollectionPage(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView tableJsonRetrieval(UifFormBase form) {
        LookupUtils.refreshLookupResultSelections((LookupForm) form);

        return super.tableJsonRetrieval(form);
    }
}
