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
package edu.iu.uis.eden.clientapp;

/**
 * Convenience interface holding all the request parameters a client application 
 * may need to use from a doc handler
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface IDocHandler {
  public static final String ACTIONLIST_COMMAND = "displayActionListView";
  public static final String ACTIONLIST_INLINE_COMMAND = "displayActionListInlineView";
  public static final String EMAIL_COMMAND = "displayEmailView";
  public static final String DOCSEARCH_COMMAND = "displayDocSearchView";
  public static final String SUPERUSER_COMMAND = "displaySuperUserView";
  public static final String HELPDESK_ACTIONLIST_COMMAND = "displayHelpDeskActionListView";
  public static final String INITIATE_COMMAND = "initiate";
  public static final String COMMAND_PARAMETER = "command";
  public static final String ROUTEHEADER_ID_PARAMETER = "docId";
  public static final String BACKDOOR_ID_PARAMETER = "backdoorId";
  public static final String DOCTYPE_PARAMETER = "docTypeName";
  public static final String INITIATE_URL = "initiateURL";
  public static final String ADVANCE_SEARCH_URL = "advanceSearchURL";
  public static final String DOCHANDLER_METHOD = "dochandlerMethod"; 
}