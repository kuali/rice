/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.document.Document;

/**
 * Base class for all TransactionalDocumentPresentationControllers.
 */
public class TransactionalDocumentPresentationControllerBase extends DocumentPresentationControllerBase implements TransactionalDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentPresentationControllerBase.class);
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#getEditMode(org.kuali.rice.kns.document.Document)
     */
    public Set<String> getEditModes(Document document){
    	Set<String> editModes = new HashSet();
    	return editModes;
    }
}
