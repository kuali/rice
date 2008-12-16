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

import java.util.Set;

import org.kuali.rice.kns.document.Document;

/**
 * The DocumentPresentationController class is used for non-user related lock down 
 * 
 * 
 */
public interface DocumentPresentationController {
    /**
     * @param document
     * @return boolean indicating whether the document can be edited 
     */
    public boolean canEdit(Document document);
    
    /**
     * @param document
     * @return boolean indicating whether the document can be annotated
     */
    public boolean canAnnotate(Document document);
    
    /**
     * @param document
     * @return boolean indicating whether the document can be reloaded
     */
    public boolean canReload(Document document);
    
    /**
     * @param document
     * @return boolean indicating whether the document can be closed
     */
    public boolean canClose(Document document);
    
    
    /**
     * @param document
     * @return boolean indicating whether the document can be saved
     */
    public boolean canSave(Document document);
    
    
    /**
     * @param document
     * @return boolean indicating whether the document can be routed
     */
    public boolean canRoute(Document document);
    
  
    
    /**
     * @param document
     * @return boolean indicating whether the document can be canceled
     */
    public boolean canCancel(Document document);
    
    /**
     * @param document
     * @return boolean indicating whether the document can be copied
     */
    public boolean canCopy(Document document);
    
    
    /**
     * @param document
     * @return boolean indicating whether can perform route report
     */
    public boolean canPerformRouteReport(Document document);
    
    /**
     * @param document
     * @return boolean indicating whether can ad hoc route
     */
    public boolean canAdHocRoute(Document document);
    
    /**
     * @param document
     * @return Set of actions that allow to take on that document.
     */
    public Set<String> getDocumentActions(Document document);
    
    
}

