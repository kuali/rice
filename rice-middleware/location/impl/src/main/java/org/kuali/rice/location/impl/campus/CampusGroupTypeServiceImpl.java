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
package org.kuali.rice.location.impl.campus;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.kim.group.GroupTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CampusGroupTypeServiceImpl extends GroupTypeServiceBase {

    @Override
    public List<String> getWorkflowRoutingAttributes(String routeLevel) {
        if (StringUtils.isBlank(routeLevel)) {
            throw new RiceIllegalArgumentException("routeLevel was blank or null");
        }

        final List<String> attrs = new ArrayList<String>(super.getWorkflowRoutingAttributes(routeLevel));
        attrs.add(KimConstants.AttributeConstants.CAMPUS_CODE);
        return Collections.unmodifiableList(attrs);
    }
}
