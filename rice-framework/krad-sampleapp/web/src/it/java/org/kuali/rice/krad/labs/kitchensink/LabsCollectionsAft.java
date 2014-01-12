/**
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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsCollectionsAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=ab7fa92d-a2a0-4b94-b349-c00eb81de311&cacheKey=endwmf7mxaohx3lxynk6sm&pageId=UifCompView-Page7#UifCompView-Page7";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Collections");
	}
	
	@Test
    public void testCollectionsBookmark() throws Exception {
        testCollections();
        passed();
    }

    @Test
    public void testCollectionsNav() throws Exception {
        testCollections();
        passed();
    }
    
    protected void testCollections() throws InterruptedException 
    {
    	//Collection Group 1 - CollectionGroupTableLayout
    	assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine']/tbody/tr[@class='uif-collectionAddItem']");
    	assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine']/tbody/tr/td[@class='uif-collection-column-action']");
    
    	//Collection Group 2 - CollectionGroupTableLayout with jQuery table features on
    	assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[@class='uif-collectionAddItem odd']");
    	assertElementPresentByXpath("//div[@class='dataTables_length']/label/select");
    	
    	//Collection Group 6 - CollectionGroupTableLayout with jQuery table features on
    	assertElementPresentByXpath("//div[@class='uif-collectionItem uif-tableCollectionItem uif-collectionAddItem']/table[@class='table table-condensed table-bordered uif-gridLayout uif-table-fixed']");
    	assertElementPresentByXpath("//div[@class='dataTables_scrollBody']/table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr/td[@class='uif-collection-column-action ']");
    	
    	//Collection Group 3 - Stacked
    	assertElementPresentByXpath("//div[@id='collection3_disclosureContent']/div/table");
    	
    	//Collection Group 4 - Stacked Collection with a Table subcollection
    	assertElementPresentByXpath("//div[@id='collection4_disclosureContent']/div/table");
    	waitAndClickByXpath("//span[contains(text(),'SubCollection - (3 lines)')]");
    	assertElementPresentByXpath("//div[@id='subCollection1_line0_disclosureContent']");
    	
    	//Collection Group 5 - Stacked Collection with a Stacked subcollection
    	assertElementPresentByXpath("//ul/li/div[@data-parent='UifCompView-CollectionList']");
    }
}
