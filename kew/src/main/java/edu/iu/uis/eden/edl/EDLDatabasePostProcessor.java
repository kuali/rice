package edu.iu.uis.eden.edl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.edl.extract.Dump;
import edu.iu.uis.eden.edl.extract.ExtractService;
import edu.iu.uis.eden.edl.extract.Fields;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;
import edu.iu.uis.eden.util.XmlHelper;


public class EDLDatabasePostProcessor extends EDocLitePostProcessor {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EDLDatabasePostProcessor.class);

	   public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange event) throws Exception {
	        LOG.debug("doRouteStatusChange: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "statusChange");
	        DocumentRouteHeaderValue val = KEWServiceLocator.getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
	        Document doc = getEDLContent(val);
	        LOG.debug("Submitting doc: " + XmlHelper.jotNode(doc));
			DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(event.getRouteHeaderId());
			extractEDLData(routeHeader, getNodeNames(event.getRouteHeaderId()));
	        return super.doRouteStatusChange(event);
	    }

	    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
	        LOG.debug("doActionTaken: " + event);
	        super.postEvent(event.getRouteHeaderId(), event, "actionTaken");
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
	        LOG.debug("Submitting doc: " + XmlHelper.jotNode(doc));
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
	            RouteNodeInstanceVO[] activeNodeInstances = new WorkflowInfo().getActiveNodeInstances(documentId);
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
		} catch (InvalidXmlException e) {
			throw new WorkflowRuntimeException("Caught exception retrieving doc content", e);
		}
		Element rootElement = getRootElement(standardDocContent);
		List<Element> fields = new ArrayList<Element>();
		List fieldElements = XmlHelper.findElements(rootElement, "field");
        ListIterator elementIter = fieldElements.listIterator();
        while (elementIter.hasNext()) {
        	Element field = (Element) elementIter.next();
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
