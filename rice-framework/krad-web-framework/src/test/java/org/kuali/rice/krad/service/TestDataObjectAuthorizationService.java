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
package org.kuali.rice.krad.service;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestDataObjectAuthorizationService implements DataObjectAuthorizationService {

    @Override
    public boolean attributeValueNeedsToBeEncryptedOnFormsAndLinks(Class<?> dataObjectClass, String attributeName) {
        return false;
    }

    @Override
    public boolean canCreate(Class<?> dataObjectClass, Person user, String docTypeName) {
        return true;
    }

    @Override
    public boolean canMaintain(Object dataObject, Person user, String docTypeName) {
        return true;
    }
}
