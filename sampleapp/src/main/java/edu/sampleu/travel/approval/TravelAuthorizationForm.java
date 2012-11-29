/**
 * Copyright 2005-2012 The Kuali Foundation
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
package edu.sampleu.travel.approval;

import org.kuali.rice.krad.web.form.TransactionalDocumentFormBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAuthorizationForm extends TransactionalDocumentFormBase {

    public TravelAuthorizationForm() {
        super();
    }

    /**
     *   returns default document type  {@link TravelAuthorizationForm#getDefaultDocumentTypeName()}.
     *   @return a String
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "TravelAuthorization";
    }
}
