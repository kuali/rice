/*
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

package edu.samplu.krad.configview;

import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * Selenium test that tests collections
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionsWDIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start";
    }

    /**
     * Test adding a column of values to the Default Tests Table Layout
     */
    @Test
    public void testDefaultTestsTableLayout() throws Exception{
        super.testDefaultTestsTableLayout();
    }

    /**
     * Test adding a column of values to the Add Blank Line Tests Table Layout
     */
    @Test
    public void testAddBlankLine() throws Exception {
        super.testAddBlankLine();
        passed();
    }

  
    /**
     * Test action column placement in table layout collections
     */
    @Test
    public void testActionColumnPlacement() throws Exception {
        super.testActionColumnPlacement();
        passed();
    }
    
    
    @Test
	public void testAddViaLightbox() throws Exception {
		super.testAddViaLightbox();
        passed();
	}
	
	@Test
	public void testColumnSequence() throws Exception {
		super.testColumnSequence();
        passed();
	}

	@Test
	public void testSequencerow() throws Exception {
		super.testSequencerow();
        passed();
	}
}
