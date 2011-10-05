/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view helper for the people flow maintenance document to retrieve the type attribute remotable fields
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowViewHelperServiceImpl extends ViewHelperServiceImpl {

    public List<RemotableAttributeField> retrieveTypeAttributes(View view, Object model, Container container) {
        List<RemotableAttributeField> remoteFields = new ArrayList<RemotableAttributeField>();


        return remoteFields;
    }

}
