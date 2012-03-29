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

package org.kuali.rice.kew.impl.document.search

import org.junit.Test
import org.kuali.rice.core.framework.persistence.platform.MySQLDatabasePlatform
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria
import static groovy.util.GroovyTestCase.assertEquals

class DocumentSearchGeneratorImplTest {
    // ensures that proper documentrouteheadervalue columns are getting returned from search sql
    @Test void testDocHeaderFields() {
        def generator = new DocumentSearchGeneratorImpl(dbPlatform: new MySQLDatabasePlatform());
        DocumentSearchCriteria.Builder dscb = DocumentSearchCriteria.Builder.create();
        def final EXPECTED = "Select * from ( select DISTINCT(DOC_HDR.DOC_HDR_ID), DOC_HDR.INITR_PRNCPL_ID, DOC_HDR.DOC_HDR_STAT_CD, DOC_HDR.CRTE_DT, DOC_HDR.TTL, DOC_HDR.APP_DOC_STAT, DOC_HDR.STAT_MDFN_DT, DOC_HDR.APRV_DT, DOC_HDR.FNL_DT, DOC_HDR.APP_DOC_ID, DOC_HDR.RTE_PRNCPL_ID, DOC_HDR.APP_DOC_STAT_MDFN_DT, DOC1.DOC_TYP_NM, DOC1.LBL, DOC1.DOC_HDLR_URL, DOC1.ACTV_IND  from KREW_DOC_TYP_T DOC1 , KREW_DOC_HDR_T DOC_HDR   where DOC_HDR.DOC_HDR_STAT_CD != 'I' and  DOC_HDR.DOC_TYP_ID = DOC1.DOC_TYP_ID  ) FINAL_SEARCH order by FINAL_SEARCH.CRTE_DT desc"
        assertEquals(generator.generateSearchSql(dscb.build(), []), EXPECTED)
    }
}
