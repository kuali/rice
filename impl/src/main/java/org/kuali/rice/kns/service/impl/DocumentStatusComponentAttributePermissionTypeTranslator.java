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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributesTranslatorBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentStatusComponentAttributePermissionTypeTranslator extends KimAttributesTranslatorBase {

    /***
     * @see org.kuali.rice.kim.bo.types.impl.KimAttributesTranslatorBase#translateAndAddAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public AttributeSet translateAttributes(final AttributeSet attributes){
        //Translate document number property to routingStatus, routingNode, and documentTypeName attributes
    	String documentNumber = attributes.get(KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER);
        if(StringUtils.isEmpty(documentNumber) 
        		|| StringUtils.isEmpty(attributes.get(KimConstants.KIM_ATTRIB_COMPONENT_NAME))
        		|| StringUtils.isEmpty(attributes.get(KimConstants.KIM_ATTRIB_PROPERTY_NAME)))
        	throw new RuntimeException(
        			KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER+", "+
        			KimConstants.KIM_ATTRIB_COMPONENT_NAME+", and "+
        			KimConstants.KIM_ATTRIB_PROPERTY_NAME+" should not be blank or null.");
        KualiWorkflowDocument workflowDocument = null;
        try{
	        workflowDocument = KNSServiceLocator.getWorkflowDocumentService().createWorkflowDocument(
	        			documentNumber, GlobalVariables.getUserSession().getPerson());
        } catch(WorkflowException wex){
        	throw new RuntimeException("Could not retrieve document for documemt number:"+documentNumber);
        }

        AttributeSet translatedAttributes = new AttributeSet();
        translatedAttributes.putAll(attributes);
        //translatedAttributes.put(KimConstants.KIM_ATTRIB_NAMESPACE_CODE, workflowDocument.get);
        //translatedAttributes.put(KimConstants.KIM_ATTRIB_COMPONENT, workflowDocument.get);
        translatedAttributes.put(KimConstants.KIM_ATTRIB_DOCUMENT_TYPE_NAME, workflowDocument.getDocumentType());
        translatedAttributes.put(KimConstants.KIM_ATTRIB_ROUTE_STATUS_CODE, workflowDocument.getRouteHeader().getDocRouteStatus());
        translatedAttributes.put(KimConstants.KIM_ATTRIB_ROUTE_NODE_NAME, workflowDocument.getCurrentRouteNodeNames());

        return translatedAttributes;
    }

}
