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
package org.kuali.rice.kew.util;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.core.util.JSTLConstants;

/**
 * This is a constants file used to describe KEW properties
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KEWPropertyConstants extends JSTLConstants {

    private static final long serialVersionUID = 3866677900853284209L;

    // Constants used by Document Search and the document search results
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID = "routeHeaderId";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL = "docTypeLabel";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE = "documentTitle";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC = "docRouteStatusCodeDesc";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR = "initiator";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED = "dateCreated";
    public static final String DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG = "routeLog";
    // Constants used by DocumentTypeLookupableHelperServiceImpl
    public static final String DOCUMENT_TYPE_ID = "documentTypeId";
    public static final String NAME = "name";
    public static final String DOC_TYP_LABEL = "label";
    public static final String DOC_TYPE_PARENT_ID = "documentTypeId";
    public static final String PARENT_DOC_TYPE_NAME = "parentDocType.name";
    public static final String ACTIVE = "active";
    public static final String BACK_LOCATION = "backLocation";
    public static final String SERVICE_NAMESPACE = "serviceNamespace";
    public static final String DOC_FORM_KEY = "docFormKey";
    
    //Constants used by RouteNode
    public static final String ROUTE_NODE_ID = "routeNodeId";
    public static final String ROUTE_NODE_INSTANCE_ID = "routeNodeInstanceId";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String DOCUMENT_ID = "documentId";
    public static final String ROUTE_HEADER_ID = "routeHeaderId";
    public static final String ROUTE_NODE_NAME = "routeNodeName";
    public static final String PROCESS_ID = "processId";
    public static final String COMPLETE = "complete";
    public static final String FINAL_APPROVAL = "finalApprovalInd";
    public static final String KEY = "key";
    public static final String ROUTE_NODE_STATE_ID = "nodeStateId";
    
    
    public static final Set<String> DOC_SEARCH_RESULT_PROPERTY_NAME_SET = new HashSet<String>();
    static {
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED);
        DOC_SEARCH_RESULT_PROPERTY_NAME_SET.add(DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG);
    }

}
