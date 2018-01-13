/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.impl.document.search

import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.module.RunMode
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.api.resourceloader.ResourceLoader
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl
import org.kuali.rice.core.impl.datetime.DateTimeServiceImpl
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.kuali.rice.kew.api.KEWPropertyConstants
import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria
import org.kuali.rice.kew.docsearch.service.impl.DocumentSearchServiceImpl
import org.kuali.rice.kew.doctype.service.DocumentTypeService
import org.kuali.rice.kew.service.KEWServiceLocator
import org.kuali.rice.krad.UserSession
import org.kuali.rice.krad.util.GlobalVariables

import javax.xml.namespace.QName
import java.text.SimpleDateFormat
import java.util.concurrent.Callable

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
/**
 * Tests parsing of document search criteria form
 */
class DocumentSearchCriteriaBoLookupableHelperServiceTest {
    def lookupableHelperService = new DocumentSearchCriteriaBoLookupableHelperService()

    static class FakeUserSession extends UserSession {
        public FakeUserSession(String s) { super(s); }
        @Override
        protected void initPerson(String principalName) { }
    }

    @Before
    void setupFakeEnv() {
        def config = new JAXBConfigImpl();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");

        ConfigContext.init(config);
        GlobalResourceLoader.stop();

        def dts = new DateTimeServiceImpl()
        dts.afterPropertiesSet()

        DocumentTypeService documentTypeService = { null } as DocumentTypeService;

        ParameterService ps = [
                getParameterValueAsBoolean: { String namespaceCode, String componentCode, String parameterName ->
                    null
                }
        ] as ParameterService


        GlobalResourceLoader.addResourceLoader([
            getName: { -> new QName("Foo", "Bar") },
            getService: { QName name ->
                [ dateTimeService: dts, enDocumentTypeService: documentTypeService, parameterService: ps ][name.getLocalPart()]
            },
            stop: {}
        ] as ResourceLoader)

        ConfigContext.getCurrentContextConfig().putProperty(KEWServiceLocator.KEW_RUN_MODE_PROPERTY, RunMode.LOCAL.name())
    }


    @Before
    void init() {
        lookupableHelperService.setDocumentSearchService(new DocumentSearchServiceImpl() {
            @Override // stub this out
            DocumentSearchCriteria getSavedSearchCriteria(String principalId, String searchName) {
                return null
            }
        });
        lookupableHelperService.setDocumentSearchCriteriaTranslator(new DocumentSearchCriteriaTranslatorImpl())
    }

    /**
     * Tests that the doc statuses selected on the document search form are properly parsed into
     * the DocumentSearchCriteria
     */
    @Test
    void testLoadCriteriaDocStatuses() {
        // form fields
        def fields = new HashMap<String, String>()
        // parameters not captured by form fields (?)
        def params = new HashMap<String, String[]>()
        params.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                   [ DocumentStatus.INITIATED.code,
                     DocumentStatus.PROCESSED.code,
                     DocumentStatus.FINAL.code,
                     "category:" + DocumentStatusCategory.SUCCESSFUL.getCode(),
                     "category:" + DocumentStatusCategory.UNSUCCESSFUL.getCode()] as String[])

        lookupableHelperService.setParameters(params)
        
        
        GlobalVariables.doInNewGlobalVariables(new FakeUserSession(), new Callable() {
            public Object call() {
                def crit = lookupableHelperService.loadCriteria(fields)
                assertNotNull(crit)

                assertEquals([ DocumentStatus.INITIATED, DocumentStatus.PROCESSED, DocumentStatus.FINAL ], crit.getDocumentStatuses())
                assertEquals([ DocumentStatusCategory.SUCCESSFUL, DocumentStatusCategory.UNSUCCESSFUL ], crit.getDocumentStatusCategories())
            }
        })
    }

    @Test
    void testCheckForAdditionalFieldsSetsRows() {
        def DOC_TYPE = "DOC TYPE"
        def setRowsCalledWith = ""
        new DocumentSearchCriteriaBoLookupableHelperService() {
            protected void setRows(String doctype) {
                setRowsCalledWith = doctype
            }
        }.checkForAdditionalFields([documentTypeName: DOC_TYPE])
        assertEquals("checkForAdditionalFields did not initialize rows for document type argument: $DOC_TYPE", DOC_TYPE, setRowsCalledWith)
    }

    @Test
    void testDateRangeFloorAndCeiling() {
        def fields = new HashMap<String, String>()
        fields.put("dateCreated", "11/11/11..12/12/12")
        lookupableHelperService.setParameters([:]) // otherwise NPE
        GlobalVariables.doInNewGlobalVariables(new FakeUserSession(), new Callable() {
            public Object call() {
                def crit = lookupableHelperService.loadCriteria(fields)
                assertEquals(new DateTime(new SimpleDateFormat("MM/dd/yy").parse("11/11/11")).withMillisOfDay(0), crit.dateCreatedFrom)
                assertEquals(new DateTime(new DateTime(new SimpleDateFormat("MM/dd/yy").parse("12/13/12")).toDateMidnight()).minusMillis(1), crit.dateCreatedTo)
            }
        });
    }
}