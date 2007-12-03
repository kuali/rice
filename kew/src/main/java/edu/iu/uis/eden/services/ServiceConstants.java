/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.services;

import java.util.HashMap;

/**
 * A random assortment of constants.
 * 
 * TODO shouldn't these be integrated into EdenConstants?  Seems that many of these are no
 * longer being used?
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceConstants {
    
  public static final HashMap ACTION_REQUESTED_MAP;
  public static final HashMap PARTY_TYPE_MAP;

  static {
    HashMap map = new HashMap();
    map.put("A", "Approve");
    map.put("C", "Complete");
    map.put("K", "Acknowledge");
    map.put("F", "FYI");
    ACTION_REQUESTED_MAP = map;
  }

  static {
    HashMap map = new HashMap();
    map.put("U", "User");
    map.put("W", "Workgroup");
    PARTY_TYPE_MAP = map;
  }    
    
  public static final String APP_RESOURCE_PREKEY = "edenDocuments.";
  public static final String APPROVE = "Approve Document";
  public static final String DISAPPROVE = "Disapprove Document";
  public static final String CANCEL = "Cancel Document";
  public static final String COMPLETE = "Complete Document";
  public static final String ADHOC = "Adhoc Route Document";
  public static final String FYI = "OK";
  public static final String ACKNOWLEDGE = "Acknowledge";
  public static final String LAST_PAGE = "lastPage";
  public static final String DOCUMENT_SESSION_NAME = "documentInSession";
  public static final String DT_MONITOR_DOCUMENT_SESSION_NAME = ".docTypeMonitorDocumentInSession";
  public static final String WORKGROUP_DOCUMENT_SESSION_NAME = ".workGroupDocumentInSession";
  public static final String DOCUMENT_DOCUMENT_SESSION_NAME = ".documentDocumentInSession";
  public static final String DOCUMENT_GROUP_DOCUMENT_SESSION_NAME = ".documentGroupDocumentInSession";
  public static final String BASE_DOCUMENT_SESSION_NAME = ".baseDocumentInSession";
  public static final String HRMS_DOCUMENT_SESSION_NAME = ".hrmsDocumentInSession";
  public static final String FISCAL_UPPA_DOCUMENT_SESSION_NAME = ".fiscalUppaInSession";
  public static final String SAVE_LOCATION_KEY = "saveLocation";
  public static final String DOCUMENT_STATE_SAVE_KEY = "documentSaved";
  public static final String DOCUMENT_KEY_YES_VALUE = "yes";
  public static final String FOOTER_MSG_KEY = "footerMessage";
  public static final String INPUT_ERROR_KEY = "errorMsg";
  public static final String ADD_MEMBER = "AddMember";
  public static final String DELETE_MEMBER = "DeleteMember";
  public static final String ROUTE = "route";
  public static final String ACTION_LIST_SESSION_NAME = "actionList";
  public static final String SEARCH_RETURN = "return";

  /**
   * navigation constants
   *
   */
  public static final String WIZARD_NEXT = "next";
  public static final String WIZARD_BACK = "back";
  public static final String WIZARD_SAVE = "save";
  public static final String WIZARD_EXIT = "exit";
  public static final String WIZARD_DELETE = "delete";
  public static final String WIZARD_ROUTE = "RouteDocument";
  public static final String CUSTOM_ACTION = "customAction";
  public static final String EDEN_WORKGROUP = "EDEN_TEAM";

  //  public static final String DOC_HANDLER_SESSION_APPENDER = "docHandler";
  public static final String DATE_FORMAT = "MM/dd/yyyy";

  //blanket approve workgroups
  public static final String BASE_REVIEW_BLANKET_APPRV_WRKGRP = "IA.UITS.Base Review Blanket Approve Workgroup";
  public static final String HRMS_REVIEW_BLANKET_APPRV_WRKGRP = "IA.UITS.HRMS Review Blanket Approve Workgroup";
  public static final String DOC_TYPE_BLANKET_APPRV_WRKGRP = "IA.UITS.Document Type Blanket Approve Workgroup";
  public static final String DOC_TYPE_GRP_BLANKET_APPRV_WRKGRP = "IA.UITS.Document Type Group Blanket Approve Workgroup";
  public static final String FISCA_UPPA_BLANKET_APPRV_WRKGRP = "IA.UITS.Fiscal Uppa Blanket Approve Workgroup";
  public static final String WORKGROUP_BLANKET_APPRV_WRKGRP = "Workgroup Blanket Approve Workgroup";
  public static final String USER_REPORT_WRKGRP = "IA.UITS.User Report Workgroup";
  public static final String DT_MONITOR_BLANKET_APPRV_WRKGRP = "IA.UITS.DocType Monitor Blanket Approve Workgroup";
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
