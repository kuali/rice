/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.samplu.travel.krad.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * it tests travel account type lookup screen.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountTypeLookupWDIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return "/portal.do?channelTitle=Travel%20Account%20Type%20Lookup&channelUrl=" +ITUtil.getBaseUrlString()+ "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccountType";
    }
    
    @Test
    public void testTravelAccountTypeLookup() throws Exception {
        super.testTravelAccountTypeLookup();
    }
}
