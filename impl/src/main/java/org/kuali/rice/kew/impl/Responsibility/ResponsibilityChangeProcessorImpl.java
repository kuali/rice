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
package org.kuali.rice.kew.impl.Responsibility;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.Responsibility.ResponsibilityChangeProcessor;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.jws.WebParam;
import java.util.Set;

public class ResponsibilityChangeProcessorImpl implements ResponsibilityChangeProcessor {

    @Override
    public void ResponsibilityChangeContents(@WebParam(name = "responsibilities") Set<String> responsibilities) {
        if (responsibilities == null) {
            throw new RiceIllegalArgumentException("responsibilities is null");
        }

        KEWServiceLocator.getActionRequestService().updateActionRequestsForResponsibilityChange(responsibilities);
    }
}
