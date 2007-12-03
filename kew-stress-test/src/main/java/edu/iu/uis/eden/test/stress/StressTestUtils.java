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

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;

public class StressTestUtils {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StressTestUtils.class);

    public static Object getRandomListObject(List list) {
	return list.get((int) Math.round(Math.random() * (list.size() - 1)));
    }

    public static List<UserIdVO> handleRequests(Long documentId, ActionRequestVO[] requests) throws Exception {
	List<UserIdVO> usersTakingAction = new ArrayList<UserIdVO>();
	if (requests != null && requests.length > 0) {
	    for (int index = 0; index < requests.length; index++) {
		long t1 = System.currentTimeMillis();
		ActionRequestVO request = requests[index];
		if (request.isActivated()) {
		    long t2 = System.currentTimeMillis();
		    UserIdVO userId = determineUser(request);
		    long t3 = System.currentTimeMillis();
		    TestInfo.addUser(userId);
		    WorkflowDocument document = new WorkflowDocument(userId, documentId);
		    long t4 = System.currentTimeMillis();
		    TestInfo.markCallToServer();
		    if (request.getActionRequested().equals(EdenConstants.ACTION_REQUEST_COMPLETE_REQ)) {
			BasicTest.LOG.info("Completing request from stress test " + documentId);
			document.complete("Completing request from stress test.");
			TestInfo.markCallToServer();
			TestInfo.markDocumentApprovals();
		    } else if (request.getActionRequested().equals(EdenConstants.ACTION_REQUEST_APPROVE_REQ)) {
			BasicTest.LOG.info("Approving request from stress test " + documentId);
			document.approve("Approving request from stress test.");
			TestInfo.markCallToServer();
			TestInfo.markDocumentApprovals();
		    } else if (request.getActionRequested().equals(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ)) {
			BasicTest.LOG.info("Acknowledging request from stress test " + documentId);
			document.acknowledge("Acknowledging request from stress test.");
			TestInfo.markCallToServer();
			TestInfo.markDocumentAcks();
		    } else if (request.getActionRequested().equals(EdenConstants.ACTION_REQUEST_FYI_REQ)) {
			BasicTest.LOG.info("FYI request from stress test " + documentId);
			document.fyi();
			TestInfo.markCallToServer();
			TestInfo.markDocumentFYIs();
		    } else {
			String message = "Illegal action request (" + request.getActionRequested() + ") for request "
				+ request.getActionRequestId();
			BasicTest.LOG.error(message);
			throw new Exception(message);
		    }
		    usersTakingAction.add(userId);
		    long t5 = System.currentTimeMillis();
		    LOG.info("Time to determine user: " + (t3 - t2));
		    LOG.info("Time to load document for user: " + (t4 - t3));
		    LOG.info("Time to take action on document : " + (t5 - t4));
		}
		long t6 = System.currentTimeMillis();
		LOG.info("Time to handle single action request: " + (t6 - t1));
	    }
	}
	return usersTakingAction;
    }

    public static UserIdVO determineUser(ActionRequestVO request) throws Exception {
	LOG.info("Determining user for request " + request.getActionRequestId() + ", userVO=" + request.getUserVO()
		+ ", workgroupId=" + request.getWorkgroupId());
	if (request.getUserVO() != null) {
	    return new NetworkIdVO(request.getUserVO().getNetworkId());
	} else if (request.getWorkgroupId() != null) {
	    WorkgroupVO workgroup = new WorkflowInfo().getWorkgroup(new WorkflowGroupIdVO(request.getWorkgroupId()));
	    TestInfo.markCallToServer();
	    int userIndex = (int) Math.round(Math.random() * (workgroup.getMembers().length - 1));
	    return new NetworkIdVO(workgroup.getMembers()[userIndex].getNetworkId());
	}
	String message = "Could not determine user for action request: " + request.getActionRequestId();
	LOG.error(message);
	throw new Exception(message);
    }

}
