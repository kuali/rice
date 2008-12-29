/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Base class for all TransactionalDocumentAuthorizers.
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase implements TransactionalDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentAuthorizerBase.class);

    
   
    public final Set getEditModes(Document d, Person u, Set<String> editModes) {
        Iterator i = editModes.iterator();
        while(i.hasNext()) {
          String editMode = (String)i.next();
          if(permissionExistsByTemplate(KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_USE_TRANSACTIONAL_DOCUMENT, d) && !isAuthorizedByTemplate(d, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_USE_TRANSACTIONAL_DOCUMENT, u.getPrincipalId())){
        	  editModes.remove(editMode);
          }
        }
        
        return editModes;
    }
}
