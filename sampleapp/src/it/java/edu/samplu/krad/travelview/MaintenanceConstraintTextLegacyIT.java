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
package edu.samplu.krad.travelview;

import edu.samplu.common.KradMenuLegacyITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceConstraintTextLegacyIT extends KradMenuLegacyITBase {

    @Override
    protected String getLinkLocator() {
        return "Travel Account Maintenance (New)";
    }
    @Test
    /**
     * Verify constraint text matches specific values
     */
    public void testVerifyConstraintText() throws Exception {
        gotoMenuLinkLocator();
        
        assertEquals("* indicates required field", getText("div.uif-boxLayout.uif-horizontalBoxLayout.clearfix > span.uif-message.uif-requiredInstructionsMessage.uif-boxLayoutHorizontalItem"));
        
        assertEquals("Must be 10 digits", getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        
        assertEquals("Must be 10 digits", getText("div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Sub Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        
        assertEquals("Must be 10 digits", getText("div.uif-group.uif-gridGroup.uif-collectionItem.uif-gridCollectionItem.uif-collectionAddItem div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
                
    }
}
