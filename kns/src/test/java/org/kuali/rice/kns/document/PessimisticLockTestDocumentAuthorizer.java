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
package org.kuali.rice.kns.document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;

/**
 * This class is used to allow a mock {@link Document} object to specify whether or not to use pessimistic locking without
 * requiring a {@link DocumentEntry} in the kuali data dictionary
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PessimisticLockTestDocumentAuthorizer extends DocumentAuthorizerBase {

    public static boolean USES_PESSIMISTIC_LOCKING = true;
    
    protected boolean usesPessimisticLocking(Document document) {
        return USES_PESSIMISTIC_LOCKING;
    }
}

