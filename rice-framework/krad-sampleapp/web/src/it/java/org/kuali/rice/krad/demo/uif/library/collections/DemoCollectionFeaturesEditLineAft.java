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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesEditLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Edit Line");
    }
    
    protected void testCollectionGroupEditLineDefault() throws Exception {
    	testAllFeatures("Demo-CollectionEditLine-Example1","collection1[0].field1","collection1[0].field2");
    }
    
    protected void testCollectionGroupEditLineCustomDialog1() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Custom Dialog 1");
    	testAllFeatures("Demo-CollectionEditLine-Example2","collection1[0].field1",null);
    }
    
    protected void testCollectionGroupEditLineCustomDialog2() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Custom Dialog 2");
    	testAllFeatures("Demo-CollectionEditLine-Example3","collection1[0].field3",null);
    }
    
    protected void testCollectionGroupEditLineCustomDialogLineAction() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Custom Dialog Line Action");
    	testAllFeatures("Demo-CollectionEditLine-Example4","collection1[0].field1","collection1[0].field2");
    }
    
    protected void testCollectionGroupEditLineCustomDialogSaveAction() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Custom Dialog Save Action");
    	testAllFeatures("Demo-CollectionEditLine-Example5","collection1[0].field1","collection1[0].field2");
    }
    
    protected void testCollectionGroupEditLineReadOnly() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line ReadOnly");
//    	testAllFeatures("Demo-CollectionEditLine-Example6",null,null);
    }
    
    protected void testCollectionGroupEditLineAuthorizationCollection() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Authorization (Collection)");
//    	testAllFeatures("Demo-CollectionEditLine-Example7","collection1[0].field1","collection1[0].field2");
    }
    
    protected void testCollectionGroupEditLineAuthorizationLine() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Authorization (Line)");
    	testAllFeatures("Demo-CollectionEditLine-Example8","collection1[0].field3",null);
    }
    
    protected void testCollectionGroupEditLineLookUp() throws Exception {
    	waitAndSelectByName("exampleShown", "CollectionGroup Edit Line Lookup");
    	testAllFeatures("Demo-CollectionEditLine-Example9","collection1[0].field1","collection1[0].field2");
    }

    protected void testCollectionFeaturesEditLine() throws Exception {
    	testCollectionGroupEditLineDefault();
    	testCollectionGroupEditLineCustomDialog1();
    	testCollectionGroupEditLineCustomDialog2();
    	testCollectionGroupEditLineCustomDialogLineAction();
    	testCollectionGroupEditLineCustomDialogSaveAction();
    	testCollectionGroupEditLineReadOnly();
    	testCollectionGroupEditLineAuthorizationCollection();
    	testCollectionGroupEditLineAuthorizationLine();
//    	testCollectionGroupEditLineLookUp();
    }
    
    protected void testAllFeatures(String sectionDataParent, String editField1Name, String editField2Name) throws Exception{
    	//Generate Random three digit number
    	Double randomNumber = Math.random() * ( 999 - 99 );
    	String randomNumberString = randomNumber.toString();
    	
    	//Add & verify
    	if(sectionDataParent!=null){
    	waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr/td[2]/div/input",randomNumberString);
    	waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr/td[3]/div/input",randomNumberString);
//    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr/td[4]/div/fieldset/div/button");
//    	waitForTextPresent("555");
    	
//    	Delete & verify  (Need commented as functionality is not working properly.)
//    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button");
//    	waitForTextNotPresent("555");
    	}
   
    	//Edit, Save & Verify
    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]");
    	if(editField1Name!=null){
    		waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input",randomNumberString);
    	}
    	if(editField2Name!=null){
    		waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input",randomNumberString);
    	}
    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div[2]/button");
    	waitForTextPresent(randomNumberString);
    	
    	//Edit, Cancel & Verify
    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]");
    	if(editField1Name!=null){
    		waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input",randomNumberString);
    	}
    	if(editField2Name!=null){
    		waitAndTypeByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input",randomNumberString);
    	}
    	waitAndClickByXpath("//section[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div[2]/button[contains(text(),'Cancel')]");
//    	waitForTextNotPresent(randomNumberString);
    }
    
    @Test
    public void testCollectionFeaturesEditLineBookmark() throws Exception {
        testCollectionFeaturesEditLine();
        passed();
    }

//    @Test
    public void testCollectionFeaturesEditLineNav() throws Exception {
        testCollectionFeaturesEditLine();
        passed();
    }  
}