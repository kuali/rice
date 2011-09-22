/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.CoreConstants.Versions;

import java.util.HashMap;
import java.util.Map;

public final class KewApiConstants {

    public static final String MACHINE_GENERATED_RESPONSIBILITY_ID = "0";
    public static final String ADHOC_REQUEST_RESPONSIBILITY_ID = "-1";
    public static final String EXCEPTION_REQUEST_RESPONSIBILITY_ID = "-2";
    public static final String SAVED_REQUEST_RESPONSIBILITY_ID = "-3";

    public static final int TITLE_MAX_LENGTH = 255;

    public static final String DOCUMENT_CONTENT_ELEMENT = "documentContent";
    public static final String ATTRIBUTE_CONTENT_ELEMENT = "attributeContent";
    public static final String SEARCHABLE_CONTENT_ELEMENT = "searchableContent";
    public static final String APPLICATION_CONTENT_ELEMENT = "applicationContent";
    public static final String DEFAULT_DOCUMENT_CONTENT = "<" + DOCUMENT_CONTENT_ELEMENT + "/>";
    public static final String DEFAULT_DOCUMENT_CONTENT2 = "<" + DOCUMENT_CONTENT_ELEMENT + "></"
            + DOCUMENT_CONTENT_ELEMENT + ">";

    // receive future action request constants
    public static final String RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_KEY = "_receive_future_requests";
    public static final String DEACTIVATED_FUTURE_REQUESTS_BRANCH_STATE_KEY = "_deactivated_future_requests";
    public static final String DONT_RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE = "NO";
    public static final String RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE = "YES";
    public static final String CLEAR_FUTURE_REQUESTS_BRANCH_STATE_VALUE = "CLEAR";

    public static final class DocumentContentVersions {
        public static final int ROUTE_LEVEL = 0;
        public static final int NODAL = 1;
        public static final int CURRENT = NODAL;
    }

    public static final class Namespaces {

        public static final String KEW_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/kew";

        /**
         * Namespace for the kew module which is compatible with Kuali Rice 2.0.x.
         */
        public static final String KEW_NAMESPACE_2_0 = KEW_NAMESPACE_PREFIX + "/" + Versions.VERSION_2_0;

    }
    
    public static final class ServiceNames {
        public static final String WORKFLOW_DOCUMENT_ACTIONS_SERVICE_SOAP = "workflowDocumentActionsServiceSoap";
    }

    private KewApiConstants() {
        throw new UnsupportedOperationException("Should never be called.");
    }


        public static final String RULE_ATTRIBUTE_TYPE = "RuleAttribute";
    public static final String SEARCHABLE_ATTRIBUTE_TYPE = "SearchableAttribute";
    public static final String RULE_XML_ATTRIBUTE_TYPE = "RuleXmlAttribute";
    public static final String SEARCHABLE_XML_ATTRIBUTE_TYPE = "SearchableXmlAttribute";
    public static final String EXTENSION_ATTRIBUTE_TYPE = "ExtensionAttribute";
    public static final String EMAIL_ATTRIBUTE_TYPE = "EmailAttribute";
    public static final String NOTE_ATTRIBUTE_TYPE = "NoteAttribute";
    public static final String ACTION_LIST_ATTRIBUTE_TYPE = "ActionListAttribute";
    public static final String RULE_VALIDATION_ATTRIBUTE_TYPE = "RuleValidationAttribute";
    public static final String SEARCH_GENERATOR_ATTRIBUTE_TYPE = "DocumentSearchGeneratorAttribute";
    public static final String SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE = "DocumentSearchResultProcessorAttribute";
    public static final String SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE = "DocumentSearchXMLResultProcessorAttribute";
    public static final String DOCUMENT_SEARCH_SECURITY_FILTER_ATTRIBUTE_TYPE = "DocumentSearchSecurityFilterAttribute";
    public static final String QUALIFIER_RESOLVER_ATTRIBUTE_TYPE = "QualifierResolver";
    public static final String DOCUMENT_LOOKUP_CUSTOMIZER_ATTRIBUTE_TYPE = "DocumentLookupCustomizer";

    public static final String RULE_ATTRIBUTE_TYPE_LABEL = "Rule Attribute";
    public static final String SEARCHABLE_ATTRIBUTE_TYPE_LABEL = "Searchable Attribute";
    public static final String RULE_XML_ATTRIBUTE_TYPE_LABEL = "Rule Xml Attribute";
    public static final String SEARCHABLE_XML_ATTRIBUTE_TYPE_LABEL = "Searchable Xml Attribute";
    public static final String EXTENSION_ATTRIBUTE_TYPE_LABEL = "Extension Attribute";
    public static final String EMAIL_ATTRIBUTE_TYPE_LABEL = "Email Attribute";
    public static final String NOTE_ATTRIBUTE_TYPE_LABEL = "Note Attribute";
    public static final String ACTION_LIST_ATTRIBUTE_TYPE_LABEL = "Action List Attribute";
    public static final String RULE_VALIDATION_ATTRIBUTE_TYPE_LABEL = "Rule Validation Attribute";
    public static final String SEARCH_GENERATOR_ATTRIBUTE_TYPE_LABEL = "Document Search Generator Attribute";
    public static final String SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE_LABEL = "Document Search Result Processor Attribute";
    public static final String SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE_LABEL = "Document Search Result Processor XML Attribute";
    public static final String DOCUMENT_SEARCH_SECURITY_FILTER_ATTRIBUTE_TYPE_LABEL = "Document Search Security Filter Attribute";
    public static final String QUALIFIER_RESOLVER_ATTRIBUTE_TYPE_LABEL = "Qualifier Resolver";
    
    public static final String ACTION_TAKEN_APPROVED_CD = "A";
    public static final String ACTION_TAKEN_COMPLETED_CD = "C";
    public static final String ACTION_TAKEN_ACKNOWLEDGED_CD = "K";
    public static final String ACTION_TAKEN_FYI_CD = "F";
    public static final String ACTION_TAKEN_DENIED_CD = "D";
    public static final String ACTION_TAKEN_CANCELED_CD = "X";
    public static final String ACTION_TAKEN_ROUTED_CD = "O";

    public static final String[] RULE_ATTRIBUTE_TYPES = {
    	RULE_ATTRIBUTE_TYPE,
        SEARCHABLE_ATTRIBUTE_TYPE,
        RULE_XML_ATTRIBUTE_TYPE,
        SEARCHABLE_XML_ATTRIBUTE_TYPE,
        EXTENSION_ATTRIBUTE_TYPE,
        EMAIL_ATTRIBUTE_TYPE,
        NOTE_ATTRIBUTE_TYPE,
        ACTION_LIST_ATTRIBUTE_TYPE,
        RULE_VALIDATION_ATTRIBUTE_TYPE,
        SEARCH_GENERATOR_ATTRIBUTE_TYPE,
        SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE,
        SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE,
        DOCUMENT_SEARCH_SECURITY_FILTER_ATTRIBUTE_TYPE,
        QUALIFIER_RESOLVER_ATTRIBUTE_TYPE
    };

    public static final Map<String, String> RULE_ATTRIBUTE_TYPE_MAP;
    static {
    	RULE_ATTRIBUTE_TYPE_MAP = new HashMap<String, String>();
    	RULE_ATTRIBUTE_TYPE_MAP.put(RULE_ATTRIBUTE_TYPE, RULE_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(SEARCHABLE_ATTRIBUTE_TYPE, SEARCHABLE_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(RULE_XML_ATTRIBUTE_TYPE, RULE_XML_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(SEARCHABLE_XML_ATTRIBUTE_TYPE, SEARCHABLE_XML_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(EXTENSION_ATTRIBUTE_TYPE, EXTENSION_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(EMAIL_ATTRIBUTE_TYPE, EMAIL_ATTRIBUTE_TYPE_LABEL);
       	RULE_ATTRIBUTE_TYPE_MAP.put(NOTE_ATTRIBUTE_TYPE, NOTE_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(ACTION_LIST_ATTRIBUTE_TYPE, ACTION_LIST_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(RULE_VALIDATION_ATTRIBUTE_TYPE, RULE_VALIDATION_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(SEARCH_GENERATOR_ATTRIBUTE_TYPE, SEARCH_GENERATOR_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE, SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE, SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(DOCUMENT_SEARCH_SECURITY_FILTER_ATTRIBUTE_TYPE, DOCUMENT_SEARCH_SECURITY_FILTER_ATTRIBUTE_TYPE_LABEL);
        RULE_ATTRIBUTE_TYPE_MAP.put(QUALIFIER_RESOLVER_ATTRIBUTE_TYPE, QUALIFIER_RESOLVER_ATTRIBUTE_TYPE_LABEL);
    }
}
