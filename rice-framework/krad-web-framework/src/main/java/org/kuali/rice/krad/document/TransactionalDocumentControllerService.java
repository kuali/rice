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
package org.kuali.rice.krad.document;

import org.kuali.rice.krad.web.form.TransactionalDocumentFormBase;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller service that extends {@link org.kuali.rice.krad.document.DocumentControllerService} and adds
 * methods specific to transactional documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface TransactionalDocumentControllerService extends DocumentControllerService {

    /**
     * Invoked to copy the current transactional document into a new document.
     *
     * @param form form instance containing the transactional document data
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView copy(TransactionalDocumentFormBase form);
}
