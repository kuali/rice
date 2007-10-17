/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.edl.components;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * Used as a pre processor and post processor.
 * As a pre processor this creates/fetches the workflow document and sets it on request.
 * As a post processor this takes appropriate user action on the document if the document is not in error.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowDocumentActions implements EDLModelComponent {

	private static final Logger LOG = Logger.getLogger(WorkflowDocumentActions.class);

	public static final String USER_ACTION_REQUEST_KEY = "userAction";
    public static final String ACTION_CREATE = "initiate";
    public static final String RETRIEVE = "retrieve";
    public static final String ACTION_ROUTE = "route";
    public static final String ACTION_APPROVE = "approve";
    public static final String ACTION_DISAPPROVE = "disapprove";
    public static final String ACTION_CANCEL = "cancel";
    public static final String ACTION_BLANKETAPPROVE = "blanketApprove";
    public static final String ACTION_FYI = "fyi";
    public static final String ACTION_ACKNOWLEDGE = "acknowledge";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_COMPLETE = "complete";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_RETURN_TO_PREVIOUS = "returnToPrevious";

	boolean isPreProcessor;

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {

		try {
			isPreProcessor = configElement.getTagName().equals("preProcessor");
			if (isPreProcessor) {
				doPreProcessWork(edlContext);
			} else {
				doPostProcessWork(dom, edlContext);
			}
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}

	}

	private void doPreProcessWork(EDLContext edlContext) throws Exception {
		RequestParser requestParser = edlContext.getRequestParser();
		String userAction = requestParser.getParameterValue(USER_ACTION_REQUEST_KEY);
		if (userAction == null) {
			userAction = requestParser.getParameterValue("command");//'WorkflowQuicklinks'
		}

		WorkflowDocument document = null;
		if (ACTION_CREATE.equals(userAction)) {
			document = new WorkflowDocument(new NetworkIdVO(edlContext.getUserSession().getNetworkId()), edlContext.getEdocLiteAssociation().getEdlName());
			document.setTitle("Routing Document Type '" + document.getDocumentType() + "'");
			document.getRouteHeaderId();
			LOG.info("Created document " + document.getRouteHeaderId());
		} else {
			document = (WorkflowDocument)requestParser.getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
			if (document == null) {
				String docId = requestParser.getParameterValue("docId");
				if (docId == null) {
					LOG.info("no document found for edl " + edlContext.getEdocLiteAssociation().getEdlName());
					return;
				} else {
					document = new WorkflowDocument(new NetworkIdVO(edlContext.getUserSession().getNetworkId()), new Long(docId));
				}
			}
		}

		requestParser.setAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY, document);
	}

	private void doPostProcessWork(Document dom, EDLContext edlContext) throws Exception {
		RequestParser requestParser = edlContext.getRequestParser();
		// if the document is in error then we don't want to execute the action!
		if (edlContext.isInError()) {
			return;
		}
		WorkflowDocument document = (WorkflowDocument)edlContext.getRequestParser().getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
		if (document == null) {
			return;
		}
		//strip out the data element
		Element dataElement = (Element) dom.getElementsByTagName(EDLXmlUtils.DATA_E).item(0);
		String docContent = XmlHelper.writeNode(dataElement);//use the transformer on edlcontext
		document.setApplicationContent(docContent);
		takeAction(document, requestParser);
	}

	public static void takeAction(WorkflowDocument document, RequestParser requestParser) throws WorkflowException {

		String annotation = requestParser.getParameterValue("annotation");
		String action = requestParser.getParameterValue(USER_ACTION_REQUEST_KEY);
		String nodeName = requestParser.getParameterValue("previousNode");

		if (!EDLXmlUtils.isValidatableAction(action)) {
			// if the action's not validatable, clear the attribute definitions because we don't want to end up executing validateClientRoutingData()
			// TODO the problem here is that the XML is still updated on a cancel so we end up without any attribute content in the document content
			document.clearAttributeDefinitions();
		}

        if (ACTION_ROUTE.equals(action)) {
        	document.routeDocument(annotation);
        } else if (ACTION_APPROVE.equals(action)) {
        	document.approve(annotation);
        } else if (ACTION_DISAPPROVE.equals(action)) {
        	document.disapprove(annotation);
        } else if (ACTION_CANCEL.equals(action)) {
        	document.cancel(annotation);
        } else if (ACTION_BLANKETAPPROVE.equals(action)) {
        	document.blanketApprove(annotation);
        } else if (ACTION_FYI.equals(action)) {
        	document.fyi();
        } else if (ACTION_ACKNOWLEDGE.equals(action)) {
        	document.acknowledge(annotation);
        } else if (ACTION_SAVE.equals(action)) {
            if (document.getStatusDisplayValue().equals("INITIATED")) {
		document.saveDocument(annotation);
            } else {
        	document.saveRoutingData();
            }
        } else if (ACTION_COMPLETE.equals(action)) {
        	document.complete(annotation);
        } else if (ACTION_DELETE.equals(action)) {
        	document.delete();
        } else if (ACTION_RETURN_TO_PREVIOUS.equals(action)) {
            document.returnToPreviousNode(annotation, nodeName);
        }
    }


}
