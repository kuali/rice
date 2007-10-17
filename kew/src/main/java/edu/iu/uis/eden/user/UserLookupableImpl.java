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
package edu.iu.uis.eden.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.user.web.WebWorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.UrlResolver;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * The default Lookupable implementation for WorkflowUsers. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserLookupableImpl implements WorkflowLookupable {

    private List rows;
    private static List columns = establishColumns();
    private static List columnsRestrictIds = establishColumnsRestrictIds();
    private static final String title = "User Lookup";
    private static final String returnLocation = "Lookup.do";

    private static final String LAST_NAME_FIELD_LABEL = "Last Name";
    private static final String FIRST_NAME_FIELD_LABEL = "First Name";
    private static final String NETWORK_ID_FIELD_LABEL = "Network Id";
    private static final String WORKFLOW_ID_FIELD_LABEL = "Workflow User Id";
    private static final String EMPLID_FIELD_LABEL = "University Id";
    private static final String UUID_FIELD_LABEL = "UUID";
    
    private static final String LAST_NAME_FIELD_HELP = "";
    private static final String FIRST_NAME_FIELD_HELP = "";
    private static final String NETWORK_ID_FIELD_HELP = "";
    private static final String WORKFLOW_ID_FIELD_HELP = "";
    private static final String EMPLID_FIELD_HELP = "";
    private static final String UUID_FIELD_HELP = "";
    
    private static final String LAST_NAME_PROPERTY_NAME = "lastName";
    private static final String FIRST_NAME_PROPERTY_NAME = "firstName";
    private static final String NETWORK_ID_PROPERTY_NAME = "networkId";
    private static final String WORKFLOW_ID_PROPERTY_NAME = "workflowId";
    private static final String EMPLID_PROPERTY_NAME = "emplId";
    private static final String UUID_PROPERTY_NAME = "uuId";
    private static final String BACK_LOCATION_KEY_NAME = "backLocation";
    private static final String DOC_FORM_KEY_NAME = "docFormKey";

    /**
     * UserLookupableImpl - constructor that sets up the values of what the form on the jsp will look like.
     */
    public UserLookupableImpl() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(LAST_NAME_FIELD_LABEL, LAST_NAME_FIELD_HELP, Field.TEXT, false, LAST_NAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(FIRST_NAME_FIELD_LABEL, FIRST_NAME_FIELD_HELP, Field.TEXT, false, FIRST_NAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(NETWORK_ID_FIELD_LABEL, NETWORK_ID_FIELD_HELP, Field.TEXT, false, NETWORK_ID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));
        
        fields = new ArrayList();
        fields.add(new Field(WORKFLOW_ID_FIELD_LABEL, WORKFLOW_ID_FIELD_HELP, Field.TEXT, false, WORKFLOW_ID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));
        
        if (userIdsViewable()) {
        fields = new ArrayList();
        fields.add(new Field(EMPLID_FIELD_LABEL, EMPLID_FIELD_HELP, Field.TEXT, false, EMPLID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));
        
        fields = new ArrayList();
        fields.add(new Field(UUID_FIELD_LABEL, UUID_FIELD_HELP, Field.TEXT, false, UUID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields)); 
    }
    }

    private static List establishColumns() {
        List columns = new ArrayList();
        columns.add(new Column("Full Name", Column.COLUMN_IS_SORTABLE_VALUE, "transposedNameSafe"));
        columns.add(new Column("Network Id", Column.COLUMN_IS_SORTABLE_VALUE, "authenticationUserId.authenticationId"));
        columns.add(new Column("Workflow User Id", Column.COLUMN_IS_SORTABLE_VALUE, "workflowUserId.workflowId"));
        columns.add(new Column("University Id", Column.COLUMN_IS_SORTABLE_VALUE, "emplId.emplId"));
        columns.add(new Column("UUID", Column.COLUMN_IS_SORTABLE_VALUE, "uuId.uuId"));
        columns.add(new Column("Actions", Column.COLUMN_IS_SORTABLE_VALUE, "actionsUrl"));
        return columns;
    }

    private static List establishColumnsRestrictIds() {
        List columns = new ArrayList();
        columns.add(new Column("Full Name", "true", "transposedNameSafe"));
        columns.add(new Column("Network Id", "true", "authenticationUserId.authenticationId"));
        columns.add(new Column("Workflow User Id", "true", "workflowUserId.workflowId"));
        columns.add(new Column("Actions", "true", "actionsUrl"));
        return columns;
    }

    public String getNoReturnParams(Map fieldConversions) {
        String userReturn = (String) fieldConversions.get(NETWORK_ID_PROPERTY_NAME);
        StringBuffer noReturnParams = new StringBuffer("&");
        if (!Utilities.isEmpty(userReturn)) {
            noReturnParams.append(userReturn);
        } else {
            noReturnParams.append(NETWORK_ID_PROPERTY_NAME);
        }
        noReturnParams.append("=");
        return noReturnParams.toString();
    }
    
    public void changeIdToName(Map fieldValues) {
        
    }
    /**
     * getSearchResults - searches for a fiscal organization information based on the criteria passed in by the map.
     * 
     * @return Returns a list of FiscalOrganization objects that match the result.
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        String lastName = (String) fieldValues.get(LAST_NAME_PROPERTY_NAME);
        String firstName = (String) fieldValues.get(FIRST_NAME_PROPERTY_NAME);
        String networkId = (String) fieldValues.get(NETWORK_ID_PROPERTY_NAME);
        String workflowId = (String) fieldValues.get(WORKFLOW_ID_PROPERTY_NAME);
        String emplId = (String) fieldValues.get(EMPLID_PROPERTY_NAME);
        String uuId = (String) fieldValues.get(UUID_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION_KEY_NAME);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY_NAME);

        String userReturn = (String) fieldConversions.get(NETWORK_ID_PROPERTY_NAME);
        
        UserService userSrv = (UserService) KEWServiceLocator.getUserService();
        WorkflowUser workflowUserExample = userSrv.getBlankUser();
        workflowUserExample.setGivenName(firstName == null ? "" : firstName.trim());
        workflowUserExample.setLastName(lastName == null ? "" : lastName.trim());
        workflowUserExample.setAuthenticationUserId(new AuthenticationUserId(networkId == null ? "" : networkId.trim()));
        workflowUserExample.setWorkflowUserId(new WorkflowUserId(workflowId == null ? "" : workflowId.trim()));
        workflowUserExample.setEmplId(new EmplId(emplId == null ? "" : emplId.trim()));
        workflowUserExample.setUuId(new UuId(uuId == null ? "" : uuId.trim()));
        
        Iterator users = userSrv.search(workflowUserExample, false).iterator();
        List displayList = new ArrayList();
        while (users.hasNext()) {
            WorkflowUser prototype = (WorkflowUser) users.next();
            WebWorkflowUser type = new WebWorkflowUser(prototype);
            
            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
            if (!Utilities.isEmpty(userReturn)) {
                returnUrl.append(userReturn);
            } else {
                returnUrl.append(NETWORK_ID_PROPERTY_NAME);
            }
            returnUrl.append("=");
            if (type.getAuthenticationUserId() == null || type.getAuthenticationUserId().getAuthenticationId() == null) {
                type.setAuthenticationUserId(new AuthenticationUserId("not found"));
            } else {
                returnUrl.append(type.getAuthenticationUserId().getAuthenticationId());                
            }

            returnUrl.append("\">return value</a>");
            type.setReturnUrl(returnUrl.toString());
            StringBuffer actionsUrl = new StringBuffer();
            UserCapabilities capabilities = KEWServiceLocator.getUserService().getCapabilities();
            int actionsAvailable = 0;
            if (capabilities.isReportSupported()) {
            	// the following distinction is made in the Url because of admin limitation on creation and editing of users
                actionsUrl.append("<a href=\"" + UrlResolver.getInstance().getUserReportUrl() + "?workflowId=" + type.getWorkflowUserId().getWorkflowId() + "&methodToCall=report&showEdit=yes" +	"\">Report</a>");
                actionsAvailable++;
            }
            if (capabilities.isEditSupported()) {
            	if (actionsAvailable > 0) {
            		actionsUrl.append(" | ");
            	}
            	actionsUrl.append("<a href=\"" + UrlResolver.getInstance().getUserUrl() + "?workflowId=" + type.getWorkflowUserId().getWorkflowId() + "&methodToCall=edit" + "\">Edit</a>");
            	actionsAvailable++;
            }
            if (actionsAvailable == 0) {
            	actionsUrl.append("No actions available");
            }
            type.setActionsUrl(actionsUrl.toString());
            displayList.add(type);
        }
        return displayList;
    }
    
    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }


    public List getDefaultReturnType(){
        List returnTypes = new ArrayList();
        returnTypes.add(NETWORK_ID_PROPERTY_NAME);
        return returnTypes;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns the instructions.
     */
    public String getLookupInstructions() {
        return Utilities.getApplicationConstant(EdenConstants.USER_SEARCH_INSTRUCTION_KEY);
    }

    /**
     * @return Returns the returnLocation.
     */
    public String getReturnLocation() {
        return returnLocation;
    }

    /**
     * @return Returns the columns.
     */
    public List getColumns() {
    	if (userIdsViewable()) {
        return columns;
    	} else {
    		return columnsRestrictIds;
    }
    }

    protected boolean userIdsViewable() {
    	UserSession userSession = UserSession.getAuthenticatedUser();
    	String allowViewRoles = Utilities.getApplicationConstant("Authorization.UserIds.AllowViewRoles");
    	if (!StringUtils.isBlank(allowViewRoles)) {
    		String[] allowRoles = allowViewRoles.split(",");
    		for (String allowRole : allowRoles) {
    			if (userSession.hasRole(allowRole)) {
    				return true;
    			}
    		}
    		return false;
    	}
    	return true;
    }

    public String getHtmlMenuBar() {
    	if (!KEWServiceLocator.getUserService().getCapabilities().isCreateSupported()) {
    		return "";
    	}
        return "<a href=\"" + UrlResolver.getInstance().getUserUrl() + "?methodToCall=createNew\">Create a New User</a>";
    }

    public List getRows() {
        return rows;
    }
}