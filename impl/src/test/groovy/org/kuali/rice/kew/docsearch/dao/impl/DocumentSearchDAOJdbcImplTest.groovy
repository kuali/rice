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

package org.kuali.rice.kew.docsearch.dao.impl

import org.junit.Before
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import javax.xml.namespace.QName
import org.kuali.rice.core.api.config.property.ConfigurationService
import org.kuali.rice.core.api.resourceloader.ResourceLoader
import org.kuali.rice.kew.api.KewApiConstants
import org.kuali.rice.krad.util.KRADConstants
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria

import static org.junit.Assert.assertEquals
import org.junit.Test
import org.kuali.rice.coreservice.framework.parameter.ParameterService

/**
 * Tests that DocumentSearchDAOJdbcImpl is properly deriving result limits, specifically
 * that the configured limit can override the default.
 */
class DocumentSearchDAOJdbcImplTest {
    String resultCapValue = null
    DocumentSearchDAOJdbcImpl searchDAO = new DocumentSearchDAOJdbcImpl()

    @Before
    void setupFakeEnv() {
        resultCapValue = null
        def config = new JAXBConfigImpl();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);

        GlobalResourceLoader.stop()
        GlobalResourceLoader.addResourceLoader([
                getName: { -> new QName("Foo", "Bar") },
                getService: { [ getParameterValueAsString: { a, b, c -> resultCapValue } ] as ParameterService },
                stop: {}
        ] as ResourceLoader)
    }

    @Test
    void testMaxResultSetCapUnspecifiedYieldsDefault() {
        assertEquals(KewApiConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP, searchDAO.getMaxResultCap(DocumentSearchCriteria.Builder.create().build()))
    }

    @Test
    void testMaxResultSetCapCriteria() {
        DocumentSearchCriteria.Builder b = DocumentSearchCriteria.Builder.create()
        b.setMaxResults(5)
        assertEquals(5, searchDAO.getMaxResultCap(b.build()))

        b = DocumentSearchCriteria.Builder.create()
        b.setMaxResults(2000)
        assertEquals(2000, searchDAO.getMaxResultCap(b.build()))
    }

    @Test
    void testMaxResultSetCapParameterOnly() {
        DocumentSearchCriteria.Builder b = DocumentSearchCriteria.Builder.create()
        resultCapValue = 100
        assertEquals(100, searchDAO.getMaxResultCap(b.build()))

        b = DocumentSearchCriteria.Builder.create()
        resultCapValue = 2000
        assertEquals(2000, searchDAO.getMaxResultCap(b.build()))
    }

    @Test
    void testMaxResultSetCapParameter() {
        DocumentSearchCriteria.Builder b = DocumentSearchCriteria.Builder.create()
        b.setMaxResults(5)
        resultCapValue = 100
        assertEquals(100, searchDAO.getMaxResultCap(b.build()))

        b = DocumentSearchCriteria.Builder.create()
        b.setMaxResults(2000)
        resultCapValue = 200
        assertEquals(200, searchDAO.getMaxResultCap(b.build()))

        b = DocumentSearchCriteria.Builder.create()
        b.setMaxResults(100)
        resultCapValue = 2000
        assertEquals(2000, searchDAO.getMaxResultCap(b.build()))
    }
}
