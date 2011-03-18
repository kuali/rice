/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kew.edl;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.kuali.rice.core.util.XmlHelper;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.core.xml.XmlException;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.edl.extract.Dump;
import org.kuali.rice.kew.edl.extract.ExtractService;
import org.kuali.rice.kew.edl.extract.Fields;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.postprocessor.*;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



public class EDLDatabasePostProcessor extends EDocLitePostProcessor {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EDLDatabasePostProcessor.class);

	   public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange event) throws Exception {
	        LOG.debug("doRouteStatusChange: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "statusChange");
	        DocumentRouteHeaderValue val = KEWServiceLocator.getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
	        Document doc = getEDLContent(val);
	        if (LOG.isDebugEnabled()) {
                LOG.debug("Submitting doc: " + XmlJotter.jotNode(doc));
            }
			DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
			extractEDLData(routeHeader, getNodeNames(event.getRouteHeaderId()));
	        return super.doRouteStatusChange(event);
	    }

	    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
	        LOG.debug("doActionTaken: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "actionTaken");
	        
	        // if the action requested is a save, go ahead and update the database with the most current information. -grpatter
	 	 	if (KEWConstants.ACTION_TAKEN_SAVED_CD.equals(event.getActionTaken().getActionTaken())) {
	 	 		DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
	 	 		extractEDLData(routeHeader, getNodeNames(event.getRouteHeaderId()));
	 	 	}
	        
	        return super.doActionTaken(event);
	    }

	    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
	        LOG.debug("doDeleteRouteHeader: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "deleteRouteHeader");
	        return super.doDeleteRouteHeader(event);
	    }

	    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange event) throws Exception {
	        LOG.debug("doRouteLevelChange: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "routeLevelChange");
	        DocumentRouteHeaderValue val = KEWServiceLocator.getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
	        Document doc = getEDLContent(val);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Submitting doc: " + XmlJotter.jotNode(doc));
            }
			DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
			extractEDLData(routeHeader, new String[] {event.getNewNodeName()});
	        return super.doRouteLevelChange(event);
	    }

	    public static Document getEDLContent(DocumentRouteHeaderValue routeHeader) throws Exception {
	        String content = routeHeader.getDocContent();
	        Document doc =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
	        return doc;
	    }

	    private RouteHeaderService getRouteHeaderService() {
	    	return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
	    }

	    private String[] getNodeNames(Long documentId) throws WorkflowException {
	            RouteNodeInstanceDTO[] activeNodeInstances = new WorkflowInfo().getActiveNodeInstances(documentId);
	            if (activeNodeInstances == null || activeNodeInstances.length == 0) {
	        	activeNodeInstances = new WorkflowInfo().getTerminalNodeInstances(documentId);
	            }
	            String[] nodeNames = new String[(activeNodeInstances == null ? 0 : activeNodeInstances.length)];
	            for (int index = 0; index < activeNodeInstances.length; index++) {
	                nodeNames[index] = activeNodeInstances[index].getName();
	            }
	            return nodeNames;
	    }

	    private void extractEDLData(DocumentRouteHeaderValue routeHeader, String[] nodeNames) {
	    	Dump dump = getExtractService().getDumpByDocumentId(routeHeader.getRouteHeaderId());
	    	if (dump == null) {
	    		dump = new Dump();
	    	}
	    	dump.setDocId(routeHeader.getRouteHeaderId());
			dump.setDocCreationDate(routeHeader.getCreateDate());
	    	dump.setDocCurrentNodeName(StringUtils.join(nodeNames, ","));
			dump.setDocDescription(routeHeader.getDocumentType().getDescription());
			dump.setDocModificationDate(routeHeader.getStatusModDate());
			dump.setDocInitiatorId(routeHeader.getInitiatorWorkflowId());
			dump.setDocRouteStatusCode(routeHeader.getDocRouteStatus());
			dump.setDocTypeName(routeHeader.getDocumentType().getName());

			List<Fields> fields = dump.getFields();
			fields.clear();

			List fieldElements = setExtractFields(routeHeader);
			for (Iterator iter = fieldElements.iterator(); iter.hasNext();) {
				Fields field = new Fields();
				field.setDump(dump);
				field.setDocId(dump.getDocId());
				Element element = (Element)iter.next();
				Attribute attribute = element.getAttribute("name");
				field.setFieldName(attribute.getValue());
				field.setFieldValue(element.getChildText("value"));
				fields.add(field);
			}
			dump.setFields(fields);
			getExtractService().saveDump(dump);
	    }


	private ExtractService getExtractService() {
		return (ExtractService) KEWServiceLocator.getService(KEWServiceLocator.EXTRACT_SERVICE);
	}


	private static Element getRootElement(DocumentContent docContent) {
		Element rootElement = null;
		try {
			rootElement = XmlHelper.buildJDocument(docContent.getDocument()).getRootElement();
		} catch (Exception e) {
			throw new WorkflowServiceErrorException("Invalid XML submitted", new ArrayList<Object>());
		}
		return rootElement;
	}

	private List setExtractFields(DocumentRouteHeaderValue routeHeader) {

		// get doc content for fields associated with extract record
		StandardDocumentContent standardDocContent = null;
		try {
			standardDocContent = new StandardDocumentContent(routeHeader.getDocumentContent().getDocumentContent());
		} catch (XmlException e) {
			throw new WorkflowRuntimeException("Caught exception retrieving doc content", e);
		}
		Element rootElement = getRootElement(standardDocContent);
		List<Element> fields = new ArrayList<Element>();
		Collection<Element> fieldElements = XmlHelper.findElements(rootElement, "field");
        Iterator<Element> elementIter = fieldElements.iterator();
        while (elementIter.hasNext()) {
        	Element field = elementIter.next();
        	Element version = field.getParentElement();
        	if (version.getAttribute("current").getValue().equals("true")) {
               	if (field.getAttribute("name")!= null) {
               		fields.add(field);
               	}
        	}
        }
        return fields;
	}

}
