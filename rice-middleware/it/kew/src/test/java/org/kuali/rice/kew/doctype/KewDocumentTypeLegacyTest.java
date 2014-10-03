/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.kew.doctype;

import org.junit.Before;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.LegacyUtils;

import java.util.Collections;

/**
 * Tests to confirm legacy (OJB) mapping for the KEW module Document type objects
 *
 * Created by fraferna on 9/3/14.
 */
public class KewDocumentTypeLegacyTest extends KewDocumentTypeBaseTest {

    private DataObjectService dataObjectService;
    private LookupService lookupService;

    @Before
    public void setup() {
        LegacyUtils.beginLegacyContext();
        dataObjectService = KRADServiceLocator.getDataObjectService();
        lookupService = KRADServiceLocatorWeb.getLookupService();
    }

    @Override
    protected DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Override
    protected DocumentType fetchDocumentType(DocumentType dt) {
        return lookupService.findObjectBySearch(dt.getClass(), Collections.singletonMap("documentTypeId", dt.getId()));
    }
}
