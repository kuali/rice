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
package org.kuali.core.document;

import java.util.Map;

import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.kns.document.authorization.PessimisticLock;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This Document Authorizer instance is used to test the {@link PessimisticLock} object and it's
 * use of lock descriptors 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class TestDocumentAuthorizerBase extends DocumentAuthorizerBase {

    public static final String USER_SESSION_OBJECT_KEY = "TEST_AUTHORIZER_OBJECT_KEY";

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getCustomLockDescriptor(org.kuali.rice.kns.document.Document, java.util.Map, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    protected String getCustomLockDescriptor(Document document, Map editMode, UniversalUser user) {
        return (String)GlobalVariables.getUserSession().retrieveObject(USER_SESSION_OBJECT_KEY);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#useCustomLockDescriptors()
     */
    @Override
    protected boolean useCustomLockDescriptors() {
        return true;
    }

}
