/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.authorization;

import org.kuali.rice.KNSServiceLocator;

/**
 * A list of standard authorization types used by the application as a whole.
 * 
 */
public interface AuthorizationType {
    public Class getTargetObjectClass();
    public String getName();
    abstract class AuthorizationTypeBase implements AuthorizationType{
        private Class targetObjectClass;
        public AuthorizationTypeBase() {};
        public AuthorizationTypeBase(Class businessObjectClass) {
            this();
            this.targetObjectClass = businessObjectClass;
        }
        public Class getTargetObjectClass() {
            return targetObjectClass;
        }
        public String getName() {
            return this.getClass().getSimpleName().toLowerCase();
        }
    }
    class Default extends AuthorizationTypeBase { // ability to do something that can't be categorized into one of below auth tyes, because action has not implemented checkAuthorization
        public Default(Class actionClass) {
            super(actionClass);
        }
    }
    class Inquiry extends AuthorizationTypeBase { // ability to inquire (get detail) on a specific object
        public Inquiry(Class businessObjectClass) {
            super(businessObjectClass);
        }
        public String getName() {
            return KNSServiceLocator.getBusinessObjectDictionaryService().getInquiryTitle(getTargetObjectClass());
        }
    }
    class Lookup extends AuthorizationTypeBase { // ability to perform a search on an object
        public Lookup(Class businessObjectClass) {
            super(businessObjectClass);
        }
        public String getName() {
            return KNSServiceLocator.getBusinessObjectDictionaryService().getLookupTitle(getTargetObjectClass());
        }
    }
    class Document extends AuthorizationTypeBase { // ability to interact in any way with a document (view/initiate/approve)
        private org.kuali.core.document.Document document;
        public Document(Class documentOrbusinessObjectClass, org.kuali.core.document.Document document) {
            super(documentOrbusinessObjectClass);
            this.document = document;
        }
        public String getName() {
            return KNSServiceLocator.getDataDictionaryService().getDocumentLabelByClass(getTargetObjectClass());
        }
        public org.kuali.core.document.Document getDocument() {
            return document;
        }
    }
    class AdHocRequest extends AuthorizationTypeBase { // eligibility to be sent an ad hoc action request
        private String actionRequested;
        public AdHocRequest(Class documentOrBusinessObjectClass, String actionRequested) {
            super(documentOrBusinessObjectClass);
            this.actionRequested = actionRequested;
        }
        public String getActionRequested() {
            return actionRequested;
        }
    }
}
