/*
 * Copyright 2006-2013 The Kuali Foundation
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

package edu.samplu.krad.demo.uif.library.collections.sequenceColumn;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import edu.samplu.krad.demo.uif.library.DemoLibraryITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoLibraryCollectionSequenceSmokeTestBase extends DemoLibraryITBase {

    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionSequence-View";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void changeSequenceView() throws Exception {
        selectOptionByName("exampleShown","Demo-CollectionSequence-Example2");
        waitForPageToLoad();
        assert(isOptionSelected("exampleShown", "Demo-CollectionSequence-Example2"));
    }

    protected void changeViewTheme() throws Exception {
        selectOptionByName("themeName", "Uif-ClassicKnsTheme");
        waitForPageToLoad();
        assert(isOptionSelected("themeName","Uif-ClassicKnsTheme"));
        selectOptionByName("themeName", "Uif-KradTheme");
        waitForPageToLoad();
        assert(isOptionSelected("themeName","Uif-KradTheme"));
    }

    protected void testCollectionSequenceExamples() throws Exception {
        changeViewTheme();
        changeSequenceView();
    }

    public void testCollectionSequenceNav(Failable failable) throws Exception {
        navigateToLibraryDemo("Collection Features", "Sequence Column");
        testCollectionSequenceExamples();
        passed();
    }

    public void testCollectionSequenceBookmark(Failable failable) throws Exception {
        testCollectionSequenceExamples();
        passed();
    }

    private boolean isOptionSelected(String dropDownName, String optionValue) {
        WebElement select = driver.findElement(By.name(dropDownName));
        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option: options) {
            if (option.getAttribute("selected")!=null) {
                return true;
            }
        }
        return false;
    }
}
