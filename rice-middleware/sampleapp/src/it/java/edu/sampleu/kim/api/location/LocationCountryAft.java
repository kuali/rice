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
package edu.sampleu.kim.api.location;

import edu.sampleu.admin.AdminTmplMthdAftNavBlanketAppBase;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationCountryAft extends AdminTmplMthdAftNavBlanketAppBase {

    public static final List<String> COUNTRY_CODES = Arrays.asList(
            new String[] {"AW", "AG", "AF", "DZ", "AZ", "AL", "AM", "AD", "AO", "AS", "AR", "AU", "BF", "AT", "AI",
                    "AQ", "BH", "BB", "BW", "BM", "BE", "BS", "BD", "BZ", "BA", "BO", "MM", "BJ", "BY", "SB", "BL",
                    "BR", "MF", "BT", "BG", "BV", "BN", "BI", "CA", "KH", "TD", "LK", "CG", "CD", "CN", "CL", "KY",
                    "CC", "CM", "KM", "CO", "MP", "GS", "CR", "CF", "CU", "CV", "CK", "CY", "DK", "DJ", "DM", "DO",
                    "EC", "EG", "IE", "GQ", "EE", "ER", "SV", "ET", "TL", "CZ", "FK", "GF", "FI", "FJ", "FM", "FO",
                    "PF", "AX", "FR", "TF", "GM", "GA", "GE", "GH", "GI", "GD", "GG", "GL", "DE", "GP", "GU", "GR",
                    "GT", "GN", "GY", "PS", "HT", "HK", "HM", "HN", "HR", "HU", "IS", "ID", "IM", "IN", "IO", "IR",
                    "IL", "IT", "CI", "IQ", "JP", "JE", "JM", "JO", "KE", "KG", "KP", "KI", "KR", "CX", "KW", "KZ",
                    "LA", "LB", "LV", "LT", "LR", "SK", "LI", "LS", "LU", "LY", "MG", "MQ", "MO", "MD", "YT", "MN",
                    "MS", "MW", "MK", "ML", "MC", "MA", "MU", "UM", "MR", "MT", "OM", "MV", "ME", "MX", "MY", "MZ",
                    "AN", "NC", "NU", "NF", "NE", "VU", "NG", "NL", "NO", "NP", "NR", "SR", "NI", "NZ", "ZZ", "PY",
                    "PN", "PE", "PK", "PL", "PA", "PT", "PG", "PW", "GW", "QA", "RE", "MH", "RO", "PH", "PR", "RU",
                    "RW", "SA", "PM", "KN", "SC", "ZA", "SN", "SH", "SI", "SL", "SM", "SG", "SO", "ES", "RS", "LC",
                    "SD", "SJ", "SE", "SY", "CH", "AE", "TT", "TH", "TJ", "TC", "TK", "TO", "TG", "ST", "TN", "TR",
                    "TV", "TW", "TM", "TZ", "UG", "GB", "UA", "US", "UY", "UZ", "VC", "VE", "VG", "VN", "VI", "VA",
                    "NA", "WF", "EH", "WS", "SZ", "YE", "ZM", "ZW"});

    /**
     * ITUtil.PORTAL + "?channelTitle=Country&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.location.impl.country.CountryBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Country&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.location.impl.country.CountryBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Country
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Country";
    }

    @Override
    protected void createNewEnterDetails() throws InterruptedException {
        // Make sure we don't use an existing country abbreviation
        getDescriptionUnique(); // trigger creating of uniqueString
        while (COUNTRY_CODES.contains(uniqueString.substring(5, 7).toUpperCase())) {
            uniqueString = null;
            getDescriptionUnique(); // trigger creating of uniqueString
        }

        waitForElementPresentByName("document.documentHeader.documentDescription");

        jiraAwareClearAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareClearAndTypeByName("document.newMaintainableObject.code", uniqueString.substring(5, 7));
        jiraAwareClearAndTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
    }

    @Override
    protected void createNewLookupDetails() throws InterruptedException {
        createNewEnterDetails(); // no required lookups
    }
}
