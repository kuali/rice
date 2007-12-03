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
package edu.iu.uis.eden.server;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.AbstractMessageSender;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.codehaus.xfire.util.LoggingHandler;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.DocumentResponse;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.ErrorResponse;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.NoteDetail;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.NoteResponse;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.StandardResponse;
import edu.iu.uis.eden.server.SimpleDocumentActionsWebService.UserInRouteLogResponse;

/**
 * Tests SimpleDocumentActionsWebService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimpleDocumentActionsWebServiceTest extends KEWTestCase {

	private static final String WSDL_URL1 = "http://localhost:9952/en-test/remoting/{KEW}simpleDocumentActionsService?wsdl";

	private static final String WSDL_URL2 = "http://localhost:9952/en-test/wsdl/{KEW}simpleDocumentActionsService.wsdl";

	private static final String ENDPOINT_URL = "http://localhost:9952/en-test/remoting/{KEW}simpleDocumentActionsService";

	private SimpleDocumentActionsWebService service;

	@Override
	protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		// turn off bus security for the service
		ServerSideRemotedServiceHolder holder = KSBServiceLocator.getServiceDeployer().getRemotedServiceHolder(new QName("KEW", "simpleDocumentActionsService"));
		holder.getServiceInfo().getServiceDefinition().setBusSecurity(false);
		((Runnable) KSBServiceLocator.getServiceDeployer()).run();

		service = createStaticBindingService();
	}

	protected void loadTestData() throws Exception {
        loadXmlFile("SimpleDocumentActionsConfig.xml");
    }

	@Test
	public void testWsdlGeneration() throws Exception {


		Client client = new Client(new URL(WSDL_URL1));
		client.setProperty(AbstractMessageSender.MESSAGE_SENDER_CLASS_NAME, EncodedCommonsHttpMessageSender.class.getName());
		client.addInHandler(new DOMInHandler());
		client.addInHandler(new LoggingHandler());

		client.addOutHandler(new DOMOutHandler());
		client.addOutHandler(new LoggingHandler());
		Object[] results = client.invoke("create", new Object[] { "ewestfal", "123", "TestDocumentType", "Testing create via webservices" });
		assertNotNull(results);
		assertTrue(results.length > 0);

		// do it again, this time with the other wsdl
		client = new Client(new URL(WSDL_URL2));
		client.setProperty(AbstractMessageSender.MESSAGE_SENDER_CLASS_NAME, EncodedCommonsHttpMessageSender.class.getName());
		client.addInHandler(new DOMInHandler());
		client.addInHandler(new LoggingHandler());

		client.addOutHandler(new DOMOutHandler());
		client.addOutHandler(new LoggingHandler());
		results = client.invoke("create", new Object[] { "ewestfal", "123", "TestDocumentType", "Testing create via webservices" });
		assertNotNull(results);
		assertTrue(results.length > 0);

	}

	private static final String TEST_USER_DISPLAY_NAME = "User One";
	private static final String TEST_USER = "user1";
	private static final String TEST_USER2 = "ewestfal";
	private static final String TEST_FYI_USER = "user2";
	private static final String TEST_ADHOC_USER = "user2";
	private static final String TEST_ADHOC_GROUP = "TestAdhocWorkgroup";
	private static final String TEST_DOC_TYPE = "servicesTestRequest";
	private static final String TEST_TITLE = "Test Doc";
	private static final String TEST_NOTE_TEXT = "This is a test note.";
	private static final String TEST_NOTE_TEXT2 = "This is an updated test note.";

	protected static ActionRequestVO actionHasBeenRequested(ActionRequestVO[] actionsRequested, String recipient, String action) {
		for (ActionRequestVO actionRequested : actionsRequested) {
			if (recipient != null) {
				if (actionRequested.getUserVO() != null) {
					if (!recipient.equals(actionRequested.getUserVO().getNetworkId()))
						continue;
				} else if (actionRequested.getWorkgroupVO() != null) {
					if (!recipient.equals(actionRequested.getWorkgroupVO().getWorkgroupName()))
						continue;
				} else {
					throw new RuntimeException("Action request not sent to user or workgroup");
				}
			}
			if (action != null) {
				if (!action.equals(actionRequested.getActionRequested()))
					continue;
			}
			return actionRequested;
		}
		return null;
	}

	/**
	 * Tests passing an empty string parameter
	 */
	@Test public void testEmptyStringParameter() throws WorkflowException, InterruptedException {
	    DocumentResponse results = service.create(TEST_USER, "", TEST_DOC_TYPE, TEST_TITLE);
	    assertEquals(EdenConstants.ROUTE_HEADER_INITIATED_CD, results.getDocStatus());
	}

	/**
	 * Tests passing a null string parameter
	 */
	@Test public void testNullStringParameter() throws WorkflowException, InterruptedException {
        DocumentResponse results = service.create(TEST_USER, null, TEST_DOC_TYPE, TEST_TITLE);
        assertEquals(EdenConstants.ROUTE_HEADER_INITIATED_CD, results.getDocStatus());
    }

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#acknowledge(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testAcknowledge() throws WorkflowException, InterruptedException {
	    final int EXPECTED_RESULT_FIELDS = 8;

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		approveTestDoc(routeHeader.getRouteHeaderId(), "test approve");

		StandardResponse results = service.acknowledge(routeHeader.getRouteHeaderId().toString(), TEST_USER, "test acknowledge");

		//assertEquals("There should be " + EXPECTED_RESULT_FIELDS + " elements in the result return set.", EXPECTED_RESULT_FIELDS, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", results.getErrorMessage());
		assertEquals("The document should be in FINAL status", EdenConstants.ROUTE_HEADER_FINAL_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals("We were expecting the initiator to be " + TEST_USER, TEST_USER, (String) results.getInitiatorId());
		assertEquals("We aren't using appDocId so it should be empty", "", (String) results.getAppDocId());
		assertEquals("We were expecting the initiator name to be " + TEST_USER_DISPLAY_NAME, TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals("The docTitle should be " + TEST_TITLE, TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("The docRouteStatus should be F)inal ", EdenConstants.ROUTE_HEADER_FINAL_CD, routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals("We were expecting the initiator to be " + TEST_USER, TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals("We were expecting the initiator name to be " + TEST_USER_DISPLAY_NAME, TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("The last modified date should be after the create date", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals("There should be 3 action requests", 3, actionRequests.length);
		assertNotNull(actionHasBeenRequested(actionRequests, TEST_USER, "A"));
		assertNotNull(actionHasBeenRequested(actionRequests, TEST_USER, "K"));
		assertNotNull(actionHasBeenRequested(actionRequests, TEST_FYI_USER, "F"));
		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "A");
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("We didn't send an annotation so it should be null", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("This should be an approval request", actionRequest.isApprovalRequest());
		assertTrue("The action request should have been done", actionRequest.isDone());
		assertEquals("A", actionRequest.getActionTaken().getActionTaken());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "K");
		assertEquals("K", actionRequest.getActionRequested());
		assertNull("We didn't send an annotation so it should be null", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("This should be an acknowledge request", actionRequest.isAcknowledgeRequest());
		assertTrue("The action request should have been done", actionRequest.isDone());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_FYI_USER, "F");
		assertEquals("F", actionRequest.getActionRequested());
		assertNull("We didn't send an annotation so it should be null", actionRequest.getAnnotation());
		assertEquals(TEST_FYI_USER, actionRequest.getUserVO().getNetworkId());
		assertFalse("The action request should have been done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#approve(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testApprove() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		StandardResponse results = service.approve(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_TITLE, "<key>value</key>", "test approve");

		//assertEquals("We are expecting six entries in the result Map", 6, results.size());
		assertEquals(EdenConstants.ROUTE_HEADER_PROCESSED_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("Create date shouldn't be null", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("P", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("AppDocId should be null since we're not using it.", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after dateCreated", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(3, actionRequests.length);
		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "A");
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an approval request", actionRequest.isApprovalRequest());
		assertTrue("ActionRequest should be Done", actionRequest.isDone());
		assertEquals("A", actionRequest.getActionTaken().getActionTaken());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "K");
		assertEquals("K", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Acknowledge Request", actionRequest.isAcknowledgeRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_FYI_USER, "F");
		assertEquals("F", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_FYI_USER, actionRequest.getUserVO().getNetworkId());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#blanketApprove(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testBlanketApprove() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		StandardResponse results = service.blanketApprove(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_TITLE, "<key>value</key>", "test blanket approve");

		//assertEquals(6, results.size());
		assertEquals("", (String) results.getErrorMessage());
		assertEquals(EdenConstants.ROUTE_HEADER_PROCESSED_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("P", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("lastModifiedDate should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(3, actionRequests.length);
		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "A");
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval Request", actionRequest.isApprovalRequest());
		assertTrue("ActionRequest should be Done", actionRequest.isDone());
		assertEquals("B", actionRequest.getActionTaken().getActionTaken());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "K");
		assertEquals("K", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Acknowledge Request", actionRequest.isAcknowledgeRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_FYI_USER, "F");
		assertEquals("F", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_FYI_USER, actionRequest.getUserVO().getNetworkId());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#cancel(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testCancel() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();

		StandardResponse results = service.cancel(routeHeader.getRouteHeaderId().toString(), TEST_USER, "test cancel");

		//assertEquals(6, results.size());
		assertEquals(EdenConstants.ROUTE_HEADER_CANCEL_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("", (String) results.getErrorMessage());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("X", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().compareTo(routeHeader.getDateCreated()) >= 0);

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(0, actionRequests.length);

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 */
	@Test public void testCreate() throws WorkflowException {

	    DocumentResponse results = service.create(TEST_USER, "1234", TEST_DOC_TYPE, TEST_TITLE);
		//assertEquals(7, results.size());

		assertEquals(EdenConstants.ROUTE_HEADER_INITIATED_CD, (String) results.getDocStatus());

		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("1234", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("", (String) results.getErrorMessage());
		String docId = (String) results.getDocId();
		assertTrue("docId should not be empty", StringUtils.isNotEmpty(docId));
		long docIdLong = Long.parseLong(docId);
		assertTrue("docId should be >= 2000", docIdLong >= 2000);

		RouteHeaderVO routeHeader = getTestDoc(docIdLong);
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("I", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertEquals("1234", routeHeader.getAppDocId());

		deleteTestDoc(docIdLong);
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#disapprove(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testDisapprove() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		StandardResponse results = service.disapprove(routeHeader.getRouteHeaderId().toString(), TEST_USER, "test disapprove");

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD, routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().compareTo(routeHeader.getDateCreated()) >= 0);

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(2, actionRequests.length);
		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "A");
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("ActionRequeset should be Done", actionRequest.isDone());
		assertEquals("D", actionRequest.getActionTaken().getActionTaken());
		actionRequest = actionHasBeenRequested(actionRequests, TEST_USER, "K");
		assertEquals("K", actionRequest.getActionRequested());
		assertEquals("Action ACKNOWLEDGE generated by Workflow because User One took action DISAPPROVED", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Acknowledge request", actionRequest.isAcknowledgeRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#fyi(java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testFyi() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		approveTestDoc(routeHeader.getRouteHeaderId(), "test approve");

		acknowledgeTestDoc(routeHeader.getRouteHeaderId(), "test acknowledge");

		StandardResponse results = service.fyi(routeHeader.getRouteHeaderId().toString(), TEST_FYI_USER);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertTrue("docStatus should be FINAL", ((String) results.getDocStatus()).equals(EdenConstants.ROUTE_HEADER_FINAL_CD));
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("F", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(3, actionRequests.length);

		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, "user1", "A");
//		ActionRequestVO actionRequest = actionRequests[0];
//		assertEquals("A", actionRequest.getActionRequested());

		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("ActionRequest should be Done", actionRequest.isDone());
		assertEquals("A", actionRequest.getActionTaken().getActionTaken());

		actionRequest = actionHasBeenRequested(actionRequests, null, "K");
		assertEquals("K", actionRequest.getActionRequested());

		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Acknowledge request", actionRequest.isAcknowledgeRequest());
		assertTrue("ActionRequest should be Done", actionRequest.isDone());

		actionRequest = actionHasBeenRequested(actionRequests, null, "F");
		assertEquals("F", actionRequest.getActionRequested());

		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_FYI_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("ActionRequest should be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#getDocument(java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	@Test public void testGetDocument() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		routeTestDoc(routeHeader.getRouteHeaderId(), "<key>value</key>", "test doc title", "test route");

		DocumentResponse results = service.getDocument(routeHeader.getRouteHeaderId().toString(), TEST_USER);

		//assertEquals(10, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals(EdenConstants.ROUTE_HEADER_ENROUTE_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("<key>value</key>", (String) results.getDocContent());
		assertEquals("test doc title", (String) results.getTitle());
		assertEquals("APPROVE", (String) results.getActionRequested());
		List<NoteDetail> notes = results.getNotes();
		assertNotNull(notes);
		assertEquals(0, notes.size());

		approveTestDoc(routeHeader.getRouteHeaderId(), "test approve");

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		results = service.getDocument(routeHeader.getRouteHeaderId().toString(), TEST_USER);

		//assertEquals(10, results.size());
		assertEquals(EdenConstants.ROUTE_HEADER_PROCESSED_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("", (String) results.getErrorMessage());
		assertEquals("<key>value</key>", (String) results.getDocContent());
		assertEquals("test doc title", (String) results.getTitle());
		assertEquals("ACKNOWLEDGE", (String) results.getActionRequested());
		notes = results.getNotes();
		assertNotNull(notes);
		assertEquals(0, notes.size());

		acknowledgeTestDoc(routeHeader.getRouteHeaderId(), "test ack");

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		results = service.getDocument(routeHeader.getRouteHeaderId().toString(), TEST_FYI_USER);

		//assertEquals(10, results.size());
		assertEquals(EdenConstants.ROUTE_HEADER_FINAL_CD, (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("", (String) results.getErrorMessage());
		assertEquals("<key>value</key>", (String) results.getDocContent());
		assertEquals("test doc title", (String) results.getTitle());
		assertEquals("FYI", (String) results.getActionRequested());
		notes = results.getNotes();
		assertNotNull(notes);
		assertEquals(0, notes.size());

		// let's cleanup shall we
		deleteTestDoc(routeHeader.getRouteHeaderId());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#isUserInRouteLog(java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 */
	public void testIsUserInRouteLog() throws WorkflowException {

		// create a new document
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument doc = new WorkflowDocument(userIdVO, TEST_DOC_TYPE);
		String docId = doc.getRouteHeaderId().toString();

		UserInRouteLogResponse results = service.isUserInRouteLog(docId, TEST_USER);
		//assertEquals(2, results.size());

		assertTrue("time1-demo should be in the route log for document: 2005", Boolean.parseBoolean((String) results.getIsUserInRouteLog()));
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());

		results = service.isUserInRouteLog(docId, TEST_USER2);
		assertFalse("TEST_USER2 should NOT be in the route log for document: 2005", Boolean.parseBoolean((String) results.getIsUserInRouteLog()));
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocAckToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocAckGroup() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocAckToGroup(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_GROUP, "requesting adhoc acknowledge for " + TEST_ADHOC_GROUP);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - acknowledge for " + TEST_ADHOC_GROUP);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(2, actionRequests.length);
		ActionRequestVO actionRequest = actionHasBeenRequested(actionRequests, TEST_ADHOC_GROUP, "K");
		assertEquals("K", actionRequest.getActionRequested());
		assertEquals("requesting adhoc acknowledge for " + TEST_ADHOC_GROUP, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_GROUP, actionRequest.getWorkgroupVO().getWorkgroupName());
		assertTrue("Should be an Acknowledge request", actionRequest.isAcknowledgeRequest());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionRequests[1];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocAckToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocAckUser() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocAckToUser(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_USER, "requesting adhoc acknowledge for " + TEST_ADHOC_USER);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - acknowledge for " + TEST_ADHOC_USER);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(2, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("K", actionRequest.getActionRequested());
		assertEquals("requesting adhoc acknowledge for " + TEST_ADHOC_USER, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("should be an Acknowledge request", actionRequest.isAcknowledgeRequest());
		assertTrue("should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionRequests[1];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocApproveToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocApproveGroup() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocApproveToGroup(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_GROUP, "requesting adhoc approve for " + TEST_ADHOC_GROUP);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - approve for " + TEST_ADHOC_GROUP);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after dateCreated", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(1, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("requesting adhoc approve for " + TEST_ADHOC_GROUP, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_GROUP, actionRequest.getWorkgroupVO().getWorkgroupName());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		// actionRequest = actionRequests[1];
		// assertEquals("A", actionRequest.getActionRequested());
		// assertNull(actionRequest.getAnnotation());
		// assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		// assertTrue(actionRequest.isApprovalRequest());
		// assertFalse(actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocApproveToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocAproveUser() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocApproveToUser(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_USER, "requesting adhoc approve for " + TEST_ADHOC_USER);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - approve for " + TEST_ADHOC_USER);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(1, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("requesting adhoc approve for " + TEST_ADHOC_USER, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		// actionRequest = actionRequests[1];
		// assertEquals("A", actionRequest.getActionRequested());
		// assertNull(actionRequest.getAnnotation());
		// assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		// assertTrue(actionRequest.isApprovalRequest());
		// assertFalse(actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocApproveToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocApproveUserMulti() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestAdhocDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocApproveToUser(routeHeader.getRouteHeaderId().toString(), TEST_USER2, TEST_ADHOC_USER, "requesting adhoc approve for " + TEST_ADHOC_USER);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER2, null, null, "adhoc route - approve for " + TEST_ADHOC_USER);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER2, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals("Eric Westfall", (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals("test adhoc doc", routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER2, routeHeader.getInitiator().getNetworkId());
		assertEquals("Eric Westfall", routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER2);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(1, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("requesting adhoc approve for " + TEST_ADHOC_USER, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		// actionRequest = actionRequests[1];
		// assertEquals("A", actionRequest.getActionRequested());
		// assertNull(actionRequest.getAnnotation());
		// assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		// assertTrue(actionRequest.isApprovalRequest());
		// assertFalse(actionRequest.isDone());

		results = service.approve(routeHeader.getRouteHeaderId().toString(), TEST_ADHOC_USER, null, null, "test adhoc approve");
		// sleep for three seconds to allow that status to change
		Thread.sleep(3000);
		// test status after approve?
		//assertEquals(6, results.size());
		assertEquals("", (String) results.getErrorMessage());

		// return to previous and test results
		results = service.returnToPreviousNode(routeHeader.getRouteHeaderId().toString(), TEST_USER2, "returning to Adhoc node", "Adhoc");

		// sleep for three seconds to allow that status to change
		Thread.sleep(3000);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER2, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals("Eric Westfall", (String) results.getInitiatorName());

		docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals("test adhoc doc", routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER2, routeHeader.getInitiator().getNetworkId());
		assertEquals("Eric Westfall", routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		userIdVO = new NetworkIdVO(TEST_USER);
		workflowDocument = new WorkflowDocument(userIdVO, docId);
		actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(1, actionRequests.length);
		actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("Action APPROVE generated by Workflow because Eric Westfall took action RETURNED TO PREVIOUS ROUTE LEVEL", actionRequest.getAnnotation());
		assertEquals(TEST_USER2, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		service.requestAdHocApproveToUser(routeHeader.getRouteHeaderId().toString(), TEST_USER2, TEST_ADHOC_USER, "requesting adhoc approve for " + TEST_ADHOC_USER);

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		results = service.getDocument(routeHeader.getRouteHeaderId().toString(), TEST_USER);
		// service.route(String.valueOf(routeHeader.getRouteHeaderId()),
		// TEST_USER2, null, null, "adhoc route - approve for " +
		// TEST_ADHOC_USER);

		//assertEquals(10, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER2, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals("Eric Westfall", (String) results.getInitiatorName());

		docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals("test adhoc doc", routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER2, routeHeader.getInitiator().getNetworkId());
		assertEquals("Eric Westfall", routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		userIdVO = new NetworkIdVO(TEST_USER2);
		workflowDocument = new WorkflowDocument(userIdVO, docId);
		actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(2, actionRequests.length);
		actionRequest = actionHasBeenRequested(actionRequests, TEST_ADHOC_USER, "A");
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("requesting adhoc approve for " + TEST_ADHOC_USER, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocFyiToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocFyiGroup() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocFyiToGroup(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_GROUP, "requesting adhoc fyi for " + TEST_ADHOC_GROUP);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - fyi for " + TEST_ADHOC_GROUP);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(2, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("F", actionRequest.getActionRequested());
		assertEquals("requesting adhoc fyi for " + TEST_ADHOC_GROUP, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_GROUP, actionRequest.getWorkgroupVO().getWorkgroupName());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionRequests[1];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#requestAdHocFyiToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRequestAdHocFyiUser() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		// routeTestDoc(routeHeader.getRouteHeaderId(), "test route");

		service.requestAdHocFyiToUser(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_ADHOC_USER, "requesting adhoc fyi for " + TEST_ADHOC_USER);
		StandardResponse results = service.route(String.valueOf(routeHeader.getRouteHeaderId()), TEST_USER, null, null, "adhoc route - fyi for " + TEST_ADHOC_USER);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		assertEquals(2, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("F", actionRequest.getActionRequested());
		assertEquals("requesting adhoc fyi for " + TEST_ADHOC_USER, actionRequest.getAnnotation());
		assertEquals(TEST_ADHOC_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an AdHoc request", actionRequest.isAdHocRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());
		actionRequest = actionRequests[1];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#route(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testRoute() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();

		StandardResponse results = service.route(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_TITLE, "<key>value</key>", "routing document");

		//assertEquals(6, results.size());
		assertEquals("ENROUTE", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());

		// sleep for two seconds to allow that status to change
		Thread.sleep(2000);

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		System.err.println("date lastmodified: " + routeHeader.getDateLastModified());
		System.err.println("date created: " + routeHeader.getDateCreated());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		// TODO: why is this 1 not 3?
		assertEquals(1, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#save(java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 */
	public void testSave() throws WorkflowException {

		RouteHeaderVO routeHeader = createTestDoc();

		StandardResponse results = service.save(routeHeader.getRouteHeaderId().toString(), TEST_USER, TEST_TITLE, "saving document");
		//assertEquals(6, results.size());

		assertEquals("SAVED", (String) results.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) results.getCreateDate()));
		assertEquals(TEST_USER, (String) results.getInitiatorId());
		assertEquals("", (String) results.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) results.getInitiatorName());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) results.getErrorMessage());

		Long docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("S", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#addNote(java.lang.String, java.lang.String, java.lang.String, byte[])}.
	 *
	 * @throws WorkflowException
	 */
	@Test public void testAddNote() throws WorkflowException {
		RouteHeaderVO routeHeader = createTestDoc();
		Long docId = routeHeader.getRouteHeaderId();

		NoteResponse results = service.addNote(docId.toString(), TEST_USER, TEST_NOTE_TEXT);
		assertEquals(TEST_USER_DISPLAY_NAME, results.getAuthor());
		assertNotNull(results.getNoteId());
		assertNotNull(results.getTimestamp());
		assertEquals(TEST_NOTE_TEXT, results.getNoteText());
		assertEquals("", results.getErrorMessage());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#updateNote(java.lang.String, java.lang.String, java.lang.String, byte[])}.
	 *
	 * @throws WorkflowException
	 */
	@Test public void testUpdateNote() throws WorkflowException {
		RouteHeaderVO routeHeader = createTestDoc();
		Long docId = routeHeader.getRouteHeaderId();

		NoteResponse results = service.addNote(docId.toString(), TEST_USER, TEST_NOTE_TEXT);
		String noteId = (String) results.getNoteId();
		String timestamp = (String) results.getTimestamp();
		assertEquals(TEST_USER_DISPLAY_NAME, results.getAuthor());
		assertNotNull(noteId);
		assertNotNull(timestamp);
		assertEquals(TEST_NOTE_TEXT, results.getNoteText());
		assertEquals("There shouldn't be an error message if things went well.", "", results.getErrorMessage());

		results = service.updateNote(docId.toString(), noteId, TEST_FYI_USER, TEST_NOTE_TEXT2);
		assertEquals(TEST_USER_DISPLAY_NAME, results.getAuthor());
		assertEquals(noteId, results.getNoteId());
		assertEquals(timestamp, results.getTimestamp());
		assertEquals(TEST_NOTE_TEXT2, results.getNoteText());
		assertEquals("There shouldn't be an error message if things went well.", "", results.getErrorMessage());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#deleteNote(java.lang.String)}.
	 *
	 * @throws WorkflowException
	 */
	@Test public void testDeleteNote() throws WorkflowException {
		RouteHeaderVO routeHeader = createTestDoc();
		Long docId = routeHeader.getRouteHeaderId();

		NoteResponse results = service.addNote(docId.toString(), TEST_USER, TEST_NOTE_TEXT);
		String noteId = (String) results.getNoteId();
		String timestamp = (String) results.getTimestamp();
		assertEquals("", results.getErrorMessage());
		assertEquals(TEST_USER_DISPLAY_NAME, results.getAuthor());
		assertNotNull(noteId);
		assertNotNull(timestamp);
		assertEquals(TEST_NOTE_TEXT, results.getNoteText());

		ErrorResponse eresults = service.deleteNote(docId.toString(), noteId, TEST_USER);
		assertEquals("There shouldn't be an error message if things went well.", "", eresults.getErrorMessage());
		DocumentResponse dresults = service.getDocument(docId.toString(), TEST_USER);
		assertEquals("There shouldn't be an error message if things went well.", "", dresults.getErrorMessage());
		List<NoteDetail> notes = dresults.getNotes();
		assertNotNull(notes);
		assertEquals(0, notes.size());
	}

	/**
	 * Test method for
	 * {@link edu.cornell.kew.service.impl.serviceImpl#returnToPrevious(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 *
	 * @throws WorkflowException
	 * @throws InterruptedException
	 */
	public void testReturnToPrevious() throws WorkflowException, InterruptedException {

		RouteHeaderVO routeHeader = createTestDoc();
		Long docId = routeHeader.getRouteHeaderId();
		routeTestDoc(docId, "test route");

		// sleep for three seconds to allow that status to change
		Thread.sleep(3000);

		DocumentResponse dresults = service.getDocument(String.valueOf(docId), TEST_USER);
		//assertEquals(10, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) dresults.getErrorMessage());
		assertEquals("ENROUTE", (String) dresults.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be empty", StringUtils.isNotEmpty((String) dresults.getCreateDate()));
		assertEquals(TEST_USER, (String) dresults.getInitiatorId());
		assertEquals("", (String) dresults.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) dresults.getInitiatorName());
		assertEquals("", (String) dresults.getDocContent());
		assertEquals("APPROVE", (String) dresults.getActionRequested());
		List<NoteDetail> notes = dresults.getNotes();
		assertNotNull(notes);
		assertEquals(0, notes.size());

		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docId);
		ActionRequestVO[] actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);

		// TODO: why is this 1 not 3?
		assertEquals(1, actionRequests.length);
		ActionRequestVO actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertNull("Annotation should be null since we didn't set it", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// return to previous and test results
		StandardResponse rresults = service.returnToPreviousNode(routeHeader.getRouteHeaderId().toString(), TEST_USER, "returning to Initiated node", "Initiated");

		// sleep for three seconds to allow that status to change
		Thread.sleep(3000);

		//assertEquals(6, results.size());
		assertEquals("There shouldn't be an error message if things went well.", "", (String) rresults.getErrorMessage());
		assertEquals("ENROUTE", (String) rresults.getDocStatus());
		// TODO: better date test
		assertTrue("createDate should not be emtpy", StringUtils.isNotEmpty((String) rresults.getCreateDate()));
		assertEquals(TEST_USER, (String) rresults.getInitiatorId());
		assertEquals("", (String) rresults.getAppDocId());
		assertEquals(TEST_USER_DISPLAY_NAME, (String) rresults.getInitiatorName());

		docId = routeHeader.getRouteHeaderId();
		routeHeader = getTestDoc(docId.longValue());
		assertEquals(TEST_TITLE, routeHeader.getDocTitle());
		assertEquals("R", routeHeader.getDocRouteStatus());
		// TODO: better date test
		assertNotNull("dateCreated should NOT be null", routeHeader.getDateCreated());
		assertEquals(TEST_USER, routeHeader.getInitiator().getNetworkId());
		assertEquals(TEST_USER_DISPLAY_NAME, routeHeader.getInitiator().getDisplayName());
		assertNull("We aren't using appDocId so it should be null", routeHeader.getAppDocId());
		assertTrue("dateLastModified should be after createDate", routeHeader.getDateLastModified().after(routeHeader.getDateCreated()));

		userIdVO = new NetworkIdVO(TEST_USER);
		workflowDocument = new WorkflowDocument(userIdVO, docId);
		actionRequests = workflowDocument.getActionRequests();
		assertNotNull("We should have some action requests", actionRequests);
		assertEquals(1, actionRequests.length);
		actionRequest = actionRequests[0];
		assertEquals("A", actionRequest.getActionRequested());
		assertEquals("Action APPROVE generated by Workflow because Test Student took action RETURNED TO PREVIOUS ROUTE LEVEL", actionRequest.getAnnotation());
		assertEquals(TEST_USER, actionRequest.getUserVO().getNetworkId());
		assertTrue("Should be an Approval request", actionRequest.isApprovalRequest());
		assertFalse("Should NOT be Done", actionRequest.isDone());

		// let's cleanup shall we
		deleteTestDoc(docId.longValue());
	}

	/**
	 * Create the RouteHeaderVO for a test doc.
	 *
	 * @return the populated route header
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private RouteHeaderVO createTestDoc() throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, TEST_DOC_TYPE);
		workflowDocument.setTitle(TEST_TITLE);
		workflowDocument.saveRoutingData();
		RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();
		return routeHeader;
	}

	/**
	 * Create the RouteHeaderVO for a test adhoc doc.
	 *
	 * @return the populated route header
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private RouteHeaderVO createTestAdhocDoc() throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER2);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, "GeneralPurposeRequest");
		workflowDocument.setTitle("test adhoc doc");
		workflowDocument.saveRoutingData();
		RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();
		return routeHeader;
	}

	/**
	 * Get the RouteHeaderVO for a test doc based on the docId passed in.
	 *
	 * @param docId
	 *            document id for the document to retrieve
	 * @return the populated route header
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private RouteHeaderVO getTestDoc(long docId) throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, new Long(docId));
		RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();
		return routeHeader;
	}

	/**
	 * Delete the test document.
	 *
	 * @param docId
	 *            document id for the document to delete
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private void deleteTestDoc(long docId) throws WorkflowException {
		// UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		// WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO,
		// new
		// Long(docId));
		// //workflowDocument.delete();
		// workflowDocument.cancel("Canceling test doc");
	}

	/**
	 * Route a test document with this document id and annotation.
	 *
	 * @param docId
	 *            document id for the document to route
	 * @param annotation
	 *            message associated with this request
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private void routeTestDoc(long docId, String annotation) throws WorkflowException {
		routeTestDoc(docId, null, null, annotation);
	}

	/**
	 * Route a test document with this document id, content, title and
	 * annotation.
	 *
	 * @param docId
	 *            document id for the document to route
	 * @param docContent
	 *            xml document content
	 * @param docTitle
	 *            title for the document
	 * @param annotation
	 *            message associated with this request
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private void routeTestDoc(long docId, String docContent, String docTitle, String annotation) throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, new Long(docId));
		if (StringUtils.isNotEmpty(docContent)) {
			workflowDocument.setApplicationContent(docContent);
		}
		if (StringUtils.isNotEmpty(docTitle)) {
			workflowDocument.setTitle(docTitle);
		}
		workflowDocument.routeDocument(annotation);
	}

	/**
	 * Approve a test document with this document id and annotation.
	 *
	 * @param docId
	 *            document id for the document to approve
	 * @param annotation
	 *            message associated with this request
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private void approveTestDoc(long docId, String annotation) throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, new Long(docId));
		workflowDocument.approve(annotation);
	}

	/**
	 * Acknowledge a test document with this document id and annotation.
	 *
	 * @param docId
	 *            document id for the document to acknowlege
	 * @param annotation
	 *            message associated with this request
	 * @throws WorkflowException
	 *             if something goes wrong
	 */
	private void acknowledgeTestDoc(long docId, String annotation) throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(TEST_USER);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, new Long(docId));
		workflowDocument.acknowledge(annotation);
	}

	private SimpleDocumentActionsWebService createStaticBindingService() throws Exception {
		ObjectServiceFactory serviceFactory = new ObjectServiceFactory(new AegisBindingProvider());
		XFireProxyFactory proxyFactory = new XFireProxyFactory();
		Service serviceModel = serviceFactory.create(SimpleDocumentActionsWebService.class);
		return (SimpleDocumentActionsWebService) proxyFactory.create(serviceModel, new URI(ENDPOINT_URL, false).toString());
	}

	public static class EncodedCommonsHttpMessageSender extends CommonsHttpMessageSender {

		public EncodedCommonsHttpMessageSender(OutMessage message, MessageContext context) {
			super(message, context);
		}

		@Override
		public String getUri() {
			System.err.println("super.getUri(): " + super.getUri());
			try {
				return new URI(super.getUri(), false).toString();
			} catch (URIException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
