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
package org.kuali.rice.edl.framework.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.kuali.rice.core.util.xml.XmlHelper;
import org.kuali.rice.core.util.xml.XmlJotter;
import org.kuali.rice.edl.framework.extract.DumpDTO;
import org.kuali.rice.edl.framework.extract.ExtractService;
import org.kuali.rice.edl.framework.extract.FieldDTO;
import org.kuali.rice.edl.framework.services.EdlFrameworkServiceLocator;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.w3c.dom.Document;



public class EDocLiteDatabasePostProcessor extends EDocLitePostProcessor {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EDocLiteDatabasePostProcessor.class);

	   public boolean doRouteStatusChange(DocumentRouteStatusChangeDTO event) throws RemoteException {
	        LOG.debug("doRouteStatusChange: " + event);
	        String documentId = event.getDocumentId();
	        super.postEvent(documentId, event, "statusChange");
	        Document doc = getEDLContent(documentId);
	        if (LOG.isDebugEnabled()) {
                LOG.debug("Submitting doc: " + XmlJotter.jotNode(doc));
            }
			extractEDLData(documentId, getNodeNames(event.getDocumentId()), doc);
	        return super.doRouteStatusChange(event);
	    }

	    public boolean doActionTaken(ActionTakenEventDTO event) throws RemoteException {
	        LOG.debug("doActionTaken: " + event);
	        String documentId = event.getDocumentId();
	        super.postEvent(documentId, event, "actionTaken");
	        
	        // if the action requested is a save, go ahead and update the database with the most current information. -grpatter
	 	 	if (KEWConstants.ACTION_TAKEN_SAVED_CD.equals(event.getActionTaken().getActionTaken())) {
	 	 		Document doc = getEDLContent(documentId);
	 	 		extractEDLData(documentId, getNodeNames(event.getDocumentId()), doc);
	 	 	}
	        
	        return super.doActionTaken(event);
	    }

	    public boolean doDeleteRouteHeader(DeleteEventDTO event) throws RemoteException {
	        LOG.debug("doDeleteRouteHeader: " + event);
	        super.postEvent(event.getDocumentId(), event, "deleteRouteHeader");
	        return super.doDeleteRouteHeader(event);
	    }

	    public boolean doRouteLevelChange(DocumentRouteLevelChangeDTO event) throws RemoteException {
	        LOG.debug("doRouteLevelChange: " + event);
	        String documentId = event.getDocumentId();
	        super.postEvent(documentId, event, "routeLevelChange");
	        Document doc = getEDLContent(documentId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Submitting doc: " + XmlJotter.jotNode(doc));
            }
			extractEDLData(documentId, new String[] {event.getNewNodeName()}, doc);
	        return super.doRouteLevelChange(event);
	    }

//	    public static Document getEDLContent(DocumentRouteHeaderValue routeHeader) throws Exception {
//	        String content = routeHeader.getDocContent();
//	        Document doc =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
//	        return doc;
//	    }

	    private String[] getNodeNames(String documentId) {
	    	try {
	            RouteNodeInstanceDTO[] activeNodeInstances = new WorkflowInfo().getActiveNodeInstances(documentId);
	            if (activeNodeInstances == null || activeNodeInstances.length == 0) {
	        	activeNodeInstances = new WorkflowInfo().getTerminalNodeInstances(documentId);
	            }
	            String[] nodeNames = new String[(activeNodeInstances == null ? 0 : activeNodeInstances.length)];
	            for (int index = 0; index < activeNodeInstances.length; index++) {
	                nodeNames[index] = activeNodeInstances[index].getName();
	            }
	            return nodeNames;
	    	} catch (WorkflowException e) {
	    		throw new RuntimeException(e);
	    	}
	    }

	    private void extractEDLData(String documentId, String[] nodeNames, Document documentContent) {
	    	try {
	    	RouteHeaderDTO routeHeader = new WorkflowInfo().getRouteHeader(documentId);
	    	DocumentTypeDTO documentType = new WorkflowInfo().getDocTypeById(routeHeader.getDocTypeId());
	    	DumpDTO dump = getExtractService().getDumpByDocumentId(routeHeader.getDocumentId());
	    	if (dump == null) {
	    		dump = new DumpDTO();
	    	}
	    	dump.setDocId(routeHeader.getDocumentId());
			dump.setDocCreationDate(new Timestamp(routeHeader.getDateCreated().getTimeInMillis()));
	    	dump.setDocCurrentNodeName(StringUtils.join(nodeNames, ","));
			dump.setDocDescription(documentType.getDocTypeDescription());
			if (routeHeader.getDateLastModified() != null) {
				dump.setDocModificationDate(new Timestamp(routeHeader.getDateLastModified().getTimeInMillis()));
			}
			dump.setDocInitiatorId(routeHeader.getInitiatorPrincipalId());
			dump.setDocRouteStatusCode(routeHeader.getDocRouteStatus());
			dump.setDocTypeName(documentType.getName());

			List<FieldDTO> fields = dump.getFields();
			fields.clear();

			List fieldElements = setExtractFields(routeHeader, documentContent);
			for (Iterator iter = fieldElements.iterator(); iter.hasNext();) {
				FieldDTO field = new FieldDTO();
				field.setDocId(dump.getDocId());
				Element element = (Element)iter.next();
				Attribute attribute = element.getAttribute("name");
				field.setFieldName(attribute.getValue());
				field.setFieldValue(element.getChildText("value"));
				fields.add(field);
			}
			dump.setFields(fields);
			getExtractService().saveDump(dump);
	    	} catch (Exception e) {
	    		if (e instanceof RuntimeException) {
	    			throw (RuntimeException)e;
	    		}
	    		throw new RuntimeException(e);
	    	}
	    }


	private ExtractService getExtractService() {
		return EdlFrameworkServiceLocator.getExtractService();
	}


	private static Element getRootElement(Document docContent) {
		return XmlHelper.buildJDocument(docContent).getRootElement();
	}

	private List setExtractFields(RouteHeaderDTO routeHeader, Document documentContent) {
		Element rootElement = getRootElement(documentContent);
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
