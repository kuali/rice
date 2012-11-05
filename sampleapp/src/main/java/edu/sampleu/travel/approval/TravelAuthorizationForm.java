/*
 * Copyright 2006-2012 The Kuali Foundation
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

import edu.sampleu.travel.approval.TravelAuthorizationDocument;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.web.form.DocumentFormBase;

public class TravelAuthorizationForm extends DocumentFormBase {
    private static final long serialVersionUID = -5805825513252498048L;

    protected String dataObjectClassName;
    protected String action;

    public TravelAuthorizationForm() {
        super();
        setViewTypeName(UifConstants.ViewType.DOCUMENT);
    }

    @Override
    public TravelAuthorizationDocument getDocument() {
        return (TravelAuthorizationDocument) super.getDocument();
    }

    // This is to provide a setter with matching type to
    // public MaintenanceDocument getDocument() so that no
    // issues occur with spring 3.1-M2 bean wrappers
    public void setDocument(TravelAuthorizationDocument document) {
        super.setDocument(document);
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}

