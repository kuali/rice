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
package org.kuali.rice.krad.web.form;

import org.kuali.rice.krad.uif.UifConstants;

/**
 * Form class for <code>TransactionalView</code> screens
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class TransactionalDocumentFormBase extends DocumentFormBase {
    private static final long serialVersionUID = 1236345613323L;

    public TransactionalDocumentFormBase() {
        super();
        setViewTypeName(UifConstants.ViewType.TRANSACTIONAL);
    }
}
