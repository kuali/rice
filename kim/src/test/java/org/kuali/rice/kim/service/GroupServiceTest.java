/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.TestBase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * Basic test to verify we can access the GroupService through the GRL.
 *
 * FIXME: This test causes compile failure in CI because it is relying on a class
 * in KNS module test source, which maven does not expose.  Either the KNS test
 * classes need to be moved into KNS proper, or pushed down into a shared module. 
 *
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */

@PerTestUnitTestData(
        @UnitTestData(
            sqlStatements = {
                @UnitTestSql("DELETE FROM KIM_GROUPS_T WHERE ID=1"),
                @UnitTestSql("INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION) VALUES(1, 'KIM Test Group', 'Test case')")
            }
        )
)
public class GroupServiceTest extends TestBase {
    private static final String TEST_GROUP = "KIM Test Group";

    @Test
    public void testGetAllGroupNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupSoapService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> groupNames = groupSoapService.getAllGroupNames();
        assertTrue(groupNames.size() == 1);
        assertTrue(groupNames.get(0).equals(TEST_GROUP));
    }


    @Test
    public void testGetAllGroupN_SyncSOAP() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupSoapService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<String> groupNames = groupSoapService.getAllGroupNames();
        assertTrue(groupNames.size() == 1);
        assertTrue(groupNames.get(0).equals(TEST_GROUP));
   }

}
