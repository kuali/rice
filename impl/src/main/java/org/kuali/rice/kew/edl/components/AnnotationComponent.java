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
// Created on Dec 14, 2006

package org.kuali.rice.kew.edl.components;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.edl.EDLContext;
import org.kuali.rice.kew.edl.EDLModelComponent;
import org.kuali.rice.kew.edl.EDLXmlUtils;
import org.kuali.rice.kew.edl.RequestParser;
import org.kuali.rice.kew.exception.WorkflowException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.util.XmlHelper;

/**
 * EDL pipeline component that exposes annotations from the previous array of taken actions
 * in the EDL to render.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AnnotationComponent implements EDLModelComponent {
    private static final Logger LOG = Logger.getLogger(AnnotationComponent.class);

    public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
        WorkflowDocument document = (WorkflowDocument)edlContext.getRequestParser().getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);

        // insert current annotation into docContent
    	Element currentVersion = VersioningPreprocessor.findCurrentVersion(dom);
        String annotation = edlContext.getRequestParser().getParameterValue("annotation");
        if (!StringUtils.isEmpty(annotation)) {
        	EDLXmlUtils.createTextElementOnParent(currentVersion, "currentAnnotation", annotation);
        }
    	LOG.debug("Inserting annotation: " + annotation);

        // get the array of actions taken
        ActionTakenDTO[] actionsTaken;
        try {
            actionsTaken = document.getActionsTaken();
        } catch (WorkflowException we) {
            try {
                LOG.error("Error retrieving actions taken on document " + document.getRouteHeaderId() + " (" + document.getDocumentType() + ")", we);
            } catch (WorkflowException we2) {
                // give me a break :/
                LOG.error("Error retrieving route header id from document");
            }
            return;
        }
        if (actionsTaken != null) {
            // get the current version of data
            //Element currentVersion = VersioningPreprocessor.findCurrentVersion(dom);
            // for every ActionTaken, append every annotation as a child element of EDL data element
            for (ActionTakenDTO actionTaken: actionsTaken) {
                if (actionTaken != null) {
                    annotation = actionTaken.getAnnotation();
                    if (annotation != null) {
                        LOG.debug("Adding annotation: " + annotation);
                        EDLXmlUtils.createTextElementOnParent(currentVersion, "annotation", actionTaken.getUserVO().getDisplayName() + ": " + annotation);
                        LOG.debug("dom: " + XmlHelper.jotNode(dom));
                    }
                }
            }
        }
    }
}
