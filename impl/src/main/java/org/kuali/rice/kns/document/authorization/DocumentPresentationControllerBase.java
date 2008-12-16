/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class DocumentPresentationControllerBase implements DocumentPresentationController {
    private static Log LOG = LogFactory.getLog(DocumentPresentationControllerBase.class);
    
  
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#getEdit(org.kuali.rice.kns.document.Document)
     */
    public boolean canEdit(Document document){
    	boolean canEdit = false;
    	KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute() || workflowDocument.stateIsException()) {
        	canEdit = true; 
        }
        
        return canEdit;
    }
    
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canAnnotate(org.kuali.rice.kns.document.Document)
     */
    public boolean canAnnotate(Document document){
    	return canEdit(document);
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canReload(org.kuali.rice.kns.document.Document)
     */
    public boolean canReload(Document document){
    	KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
    	return (canEdit(document) && !workflowDocument.stateIsInitiated()) ;
             
    }
    
    
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canClose(org.kuali.rice.kns.document.Document)
     */
    public boolean canClose(Document document){
    	return true;
    }
    
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canSave(org.kuali.rice.kns.document.Document)
     */
    public boolean canSave(Document document){
    	return canEdit(document);
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canRoute(org.kuali.rice.kns.document.Document)
     */
    public boolean canRoute(Document document){
    	boolean canRoute = false;
    	KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
    	if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()){
    		 canRoute = true;
    	}
    	return canRoute;
    }
        
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canCancel(org.kuali.rice.kns.document.Document)
     */
    public boolean canCancel(Document document){
    	return canEdit(document);
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canCopy(org.kuali.rice.kns.document.Document)
     */
    public boolean canCopy(Document document){
    	 boolean canCopy = false;
    	 if(document.getAllowsCopy()){
    		 canCopy = true;
    	 }
    	 return canCopy;
    }
    
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canPerformRouteReport(org.kuali.rice.kns.document.Document)
     */
    public boolean canPerformRouteReport(Document document){
    	KualiConfigurationService kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
        return kualiConfigurationService.getIndicatorParameter( KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND);
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canAdHocRoute(org.kuali.rice.kns.document.Document)
     */
    public boolean canAdHocRoute(Document document){
    	KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
    	return (canEdit(document)&& !workflowDocument.stateIsException());
    }
    
   
    /**
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    public boolean canBlanketApprove(Document document){
    	return canEdit(document);
    }
    
    /**
     
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationController#getDocumentActions(org.kuali.rice.kns.document.Document)
     */
    public Set<String> getDocumentActions(Document document){
    	Set<String> documentActions = new HashSet();
    	if (canEdit(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_EDIT);
    	}
    	
    	 if(canAnnotate(document)){
    		 documentActions.add(KNSConstants.KUALI_ACTION_CAN_ANNOTATE);
    	 }
    	 
    	 if(canClose(document)){
    		 documentActions.add(KNSConstants.KUALI_ACTION_CAN_CLOSE);
    	 }
    	 
    	 if(canSave(document)){
    		 documentActions.add(KNSConstants.KUALI_ACTION_CAN_SAVE);
    	 }
    	 if(canRoute(document)){
    		 documentActions.add(KNSConstants.KUALI_ACTION_CAN_ROUTE);
    	 }
    	 
    	 if(canCancel(document)){
    		 documentActions.add(KNSConstants.KUALI_ACTION_CAN_CANCEL);
    	 }
    	 
    	 if(canReload(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_RELOAD);
    	 }
    	if(canCopy(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_COPY);
    	}
    	if(canPerformRouteReport(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
    	}
    	
    	if(canAdHocRoute(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE);
    	}
    	
    	if(canBlanketApprove(document)){
    		documentActions.add(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
    	}
    	return documentActions;
    }
    
        
}
