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
package edu.iu.uis.eden.test.stress;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;

public class WorkflowUtilityTest extends AbstractTest {
    
    private int calls;
    
    public boolean doWork() throws Exception {
        Long routeHeaderId = new Long(0);
        String routeHeaderIdValue = (String)getParameters().get("routeHeaderId");
        String networkId = (String)getParameters().get("networkId");
        calls++;
        if (routeHeaderIdValue != null && !routeHeaderIdValue.trim().equals("")) {
            routeHeaderId = Long.valueOf(routeHeaderIdValue);
        } else {
            routeHeaderId = TestInfo.getRandomRouteHeaderId();
            if (routeHeaderId == null && calls < 10) {
                return false;
            } else {
                return true;
            }
        }
        if (networkId == null || networkId.trim().equals("")) {
            // For simplicities sake, we will assume NetworkIdVO since that's all we are using
            networkId = TestInfo.getRandomUser().toString();
            if (networkId == null && calls < 10) {
                return false;
            }
        }
        NetworkIdVO networkIdVO = new NetworkIdVO(networkId);
        WorkflowInfo workflowInfo = new WorkflowInfo();
        TestInfo.markCallToServer();
        
        TestInfo.markCallToServer();
        RouteHeaderVO routeHeader = workflowInfo.getRouteHeader(routeHeaderId);
        TestInfo.markCallToServer();
        workflowInfo.getRouteHeader(networkIdVO, routeHeaderId);
        TestInfo.markCallToServer();
        workflowInfo.getActionRequests(routeHeaderId);
        TestInfo.markCallToServer();
        workflowInfo.getActionsTaken(routeHeaderId);
        TestInfo.markCallToServer();
        DocumentTypeVO documentType = workflowInfo.getDocType(routeHeader.getDocTypeName());
        TestInfo.markCallToServer();
        workflowInfo.getDocType(documentType.getDocTypeId());
        TestInfo.markCallToServer();
        workflowInfo.getNewResponsibilityId();
        TestInfo.markCallToServer();
        workflowInfo.getRoute(routeHeader.getDocTypeName());
        TestInfo.markCallToServer();
        workflowInfo.getUserWorkgroups(networkIdVO);
        TestInfo.markCallToServer();
        WorkgroupVO workgroup = workflowInfo.getWorkgroup("WorkflowAdmin");
        TestInfo.markCallToServer();
        workflowInfo.getWorkgroup(workgroup.getWorkgroupId());
        TestInfo.markCallToServer();
        workflowInfo.getWorkgroup(new WorkgroupNameIdVO("WorkflowAdmin"));
        TestInfo.markCallToServer();
        workflowInfo.getWorkgroup(new WorkflowGroupIdVO(workgroup.getWorkgroupId()));
        TestInfo.markCallToServer();
        workflowInfo.isFinalApprover(routeHeaderId, networkIdVO);
        TestInfo.markCallToServer();
        workflowInfo.isUserAuthenticatedByRouteLog(routeHeaderId, networkIdVO, true);
        TestInfo.markCallToServer();
        
        return true;
    }
    
}
