/*
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

package org.kuali.rice.kew.stats;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.stats.service.StatsService;
import org.kuali.rice.kew.test.KEWTestCase;

import static junit.framework.Assert.assertTrue;

/**
 * Test the Stats Service
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StatsServiceTest extends KEWTestCase{

    @Test
    public void testStatsService() throws  Exception{
        StatsService statsService = KEWServiceLocator.getService(KEWServiceLocator.STATS_SERVICE);

        Stats stats = new Stats();
        statsService.NumActiveItemsReport(stats);
        statsService.DocumentsRoutedReport(stats, new java.util.Date(System.currentTimeMillis()),new java.util.Date(System.currentTimeMillis()));
        statsService.NumberOfDocTypesReport(stats);
        statsService.NumInitiatedDocsByDocTypeReport(stats);
        statsService.NumUsersReport(stats);

        assertTrue("Stats object populated", stats != null &&
                StringUtils.equals(stats.getNumActionItems(),"0") &&
                stats.getNumInitiatedDocsByDocType().size() == 0);
        assertTrue("Number of document types", StringUtils.equals(stats.getNumDocTypes(),"6"));
    }


}
