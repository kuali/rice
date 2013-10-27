/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travelapplication.accountmultivalue.lookup;

import org.junit.Test;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.ITUtil;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMultivalueParameterRestrictionLookUpAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Multi-Value Lookup");
    }

    private void testParameterRestrictionSearchSelect() throws Exception {
        waitAndClickSearch3();
        assertEquals("Wrong number of search results", 10, getCssCount("div#uLookupResults table tbody tr"));

        setParameter(KRADConstants.KRAD_NAMESPACE, KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_LIMIT, "1");

        navigate();
        waitAndClickSearch3();
        assertEquals("Wrong number of search results", 1, getCssCount("div#uLookupResults table tbody tr"));

        setParameter(KRADConstants.KRAD_NAMESPACE, KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_LIMIT, "100");

        navigate();
        waitAndClickSearch3();
        assertEquals("Wrong number of search results", 10, getCssCount("div#uLookupResults table tbody tr"));
    }

    private void setParameter(String namespaceCode, String componentCode, String parameterName, String parameterValue)
            throws Exception {

        driver.get(ITUtil.LABS_URL);
        waitAndClickByLinkText("Parameter Updater");

        waitAndTypeByName("namespaceCode", namespaceCode);
        waitAndTypeByName("componentCode", componentCode);
        waitAndTypeByName("parameterName", parameterName);
        waitAndTypeByName("parameterValue", parameterValue);
        waitAndClickButtonByText("Update Parameter");

        waitForPageToLoad();
    }

    @Test
    public void testTravelAccountMultivalueParameterRestrictionLookUpSearchSelectBookmark() throws Exception {
        testParameterRestrictionSearchSelect();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueParameterRestrictionLookUpSearchSelectNav() throws Exception {
        testParameterRestrictionSearchSelect();
        passed();
    }
}
