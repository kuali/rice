/*
 * Copyright 2007 The Kuali Foundation
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
package mocks;

import java.sql.Timestamp;
import java.util.Date;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.mail.StyleableEmailContentServiceImpl;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 * This is a class used to substitute for a StyleableEmailContentServiceImpl class 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MockStyleableEmailContentServiceImpl extends StyleableEmailContentServiceImpl {

    /**
     * This overridden method is used in case the action item has an null route header attached
     * 
     * @see edu.iu.uis.eden.mail.StyleableEmailContentServiceImpl#getRouteHeader(edu.iu.uis.eden.actionitem.ActionItem)
     */
    @Override
    public DocumentRouteHeaderValue getRouteHeader(ActionItem actionItem) {
        if (actionItem.getRouteHeader() != null) {
            return super.getRouteHeader(actionItem);
        }
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setDocRouteStatus(EdenConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        return routeHeader;
    }

}
