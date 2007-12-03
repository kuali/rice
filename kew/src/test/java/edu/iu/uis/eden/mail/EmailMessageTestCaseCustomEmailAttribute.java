/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
// Created on Jan 18, 2007

package edu.iu.uis.eden.mail;

import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;

/**
 * Test CustomEmailAttribute for EmailMessageTestCase
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailMessageTestCaseCustomEmailAttribute implements CustomEmailAttribute {

    private ActionRequestVO actionRequestVO;
    private RouteHeaderVO routeHeaderVO;
    
    public ActionRequestVO getActionRequestVO() {
        return this.actionRequestVO;
    }

    public String getCustomEmailBody() throws Exception {
        return "CUSTOM EMAIL BODY";
    }

    public String getCustomEmailSubject() throws Exception {
        return "CUSTOM EMAIL SUBJECT";
    }

    public RouteHeaderVO getRouteHeaderVO() {
        return this.routeHeaderVO;
    }

    public void setActionRequestVO(ActionRequestVO actionRequestVO) {
        this.actionRequestVO = actionRequestVO;
        this.actionRequestVO.setActionRequestId(new Long(-1));
    }

    public void setRouteHeaderVO(RouteHeaderVO routeHeaderVO) {
        this.routeHeaderVO = routeHeaderVO;
        this.routeHeaderVO.setAppDocId("setByEmailComp");
    }
}