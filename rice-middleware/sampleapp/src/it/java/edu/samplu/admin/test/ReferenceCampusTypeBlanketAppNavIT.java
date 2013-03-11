/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import org.junit.Test;

import edu.samplu.common.ITUtil;

/**
 * tests that user 'admin', on blanket approving a new Campus Type maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReferenceCampusTypeBlanketAppNavIT extends AdminMenuBlanketAppNavITBase {

    @Test
    public void blanketApprove() throws Exception {
        gotoMenuLinkLocator();
        super.testReferenceCampusTypeBlanketApprove();
    }

    @Override
    protected String getLinkLocator() {
        return "Campus Type";
    }
}
