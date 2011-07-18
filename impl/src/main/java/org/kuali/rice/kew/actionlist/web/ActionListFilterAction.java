/*
 * Copyright 2005-2009 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.actionlist.web;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Action for Action List Filter page.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListFilterAction extends KualiAction {

    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
    	request.setAttribute("Constants", getServlet().getServletContext().getAttribute("KEWConstants"));
    	request.setAttribute("preferences", this.getUserSession().retrieveObject(KEWConstants.PREFERENCES));
		initForm(request, form);
		return super.execute(mapping, form, request, response);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.web.struts.action.KualiAction#getReturnLocation(javax.servlet.http.HttpServletRequest, org.apache.struts.action.ActionMapping)
	 */
	@Override
	protected String getReturnLocation(HttpServletRequest request,
			ActionMapping mapping)
	{
    	String mappingPath = mapping.getPath();
    	String basePath = getApplicationBaseUrl();
        return basePath + KEWConstants.WEBAPP_DIRECTORY + mappingPath + ".do";
	}

	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        final UserSession uSession = getUserSession();
        final ActionListFilter filter = (ActionListFilter) uSession.retrieveObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME);
        if (filter != null) {
            if (filterForm.getDocTypeFullName() != null && ! "".equals(filterForm.getDocTypeFullName())) {
            	filter.setDocumentType(filterForm.getDocTypeFullName());
            	uSession.addObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME, filter);
                filterForm.setFilter(filter);
            } else {
                filterForm.setFilter(filter);
                filterForm.setDocTypeFullName(filter.getDocumentType());
            }
        }
        return mapping.findForward("viewFilter");
    }

    public ActionForward filter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        //validate the filter through the actionitem/actionlist service (I'm thinking actionlistservice)
        final UserSession uSession = getUserSession();
        ActionListFilter alFilter = filterForm.getLoadedFilter();
        if (StringUtils.isNotBlank(alFilter.getDelegatorId()) && !KEWConstants.DELEGATION_DEFAULT.equals(alFilter.getDelegatorId()) &&
        		StringUtils.isNotBlank(alFilter.getPrimaryDelegateId()) && !KEWConstants.PRIMARY_DELEGATION_DEFAULT.equals(alFilter.getPrimaryDelegateId())){
        	// If the primary and secondary delegation drop-downs are both visible and are both set to non-default values,
        	// then reset the secondary delegation drop-down to its default value.
        	alFilter.setDelegatorId(KEWConstants.DELEGATION_DEFAULT);
        }
        uSession.addObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME, alFilter);
        KEWServiceLocator.getActionListService().saveRefreshUserOption(getUserSession().getPrincipalId());
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            return mapping.findForward("viewActionList");
        } 
        return mapping.findForward("viewFilter");    
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        filterForm.setFilter(new ActionListFilter());
        filterForm.setCreateDateFrom("");
        filterForm.setCreateDateTo("");
        filterForm.setLastAssignedDateFrom("");
        filterForm.setLastAssignedDateTo("");
        filterForm.setDocTypeFullName("");
        UserSession session = getUserSession();
        session.removeObject(KEWConstants.ACTION_LIST_FILTER_ATTR_NAME);
        KEWServiceLocator.getActionListService().saveRefreshUserOption(getUserSession().getPrincipalId());
        return mapping.findForward("viewFilter");
    }

    public void initForm(HttpServletRequest request, ActionForm form) throws Exception {
        ActionListFilterForm filterForm = (ActionListFilterForm) form;
        filterForm.setUserWorkgroups(getUserWorkgroupsDropDownList(getUserSession().getPrincipalId()));
        PreferencesService prefSrv = KEWServiceLocator.getPreferencesService();
        Preferences preferences = prefSrv.getPreferences(getUserSession().getPrincipalId());
        request.setAttribute("preferences", preferences);
        ActionListService actionListSrv = KEWServiceLocator.getActionListService();
        request.setAttribute("delegators", ActionListUtil.getWebFriendlyRecipients(actionListSrv.findUserSecondaryDelegators(getUserSession().getPrincipalId())));
        request.setAttribute("primaryDelegates", ActionListUtil.getWebFriendlyRecipients(actionListSrv.findUserPrimaryDelegations(getUserSession().getPrincipalId())));
        if (! filterForm.getMethodToCall().equalsIgnoreCase("clear")) {
            filterForm.validateDates();
        }
    }

    private List<? extends KeyValue> getUserWorkgroupsDropDownList(String principalId) {
    	List<String> userWorkgroups =
            KimApiServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId);
        List<KeyValue> sortedUserWorkgroups = new ArrayList<KeyValue>();
    	KeyValue keyValue = null;
    	keyValue = new ConcreteKeyValue(KEWConstants.NO_FILTERING, KEWConstants.NO_FILTERING);
    	sortedUserWorkgroups.add(keyValue);
    	if (userWorkgroups != null && userWorkgroups.size() > 0) {
    		Collections.sort(userWorkgroups);

    		Group group;
            for (String groupId : userWorkgroups)
            {
                group = KimApiServiceLocator.getGroupService().getGroup(groupId);
                keyValue = new ConcreteKeyValue(groupId, group.getName());
                sortedUserWorkgroups.add(keyValue);
            }
    	}
    	return sortedUserWorkgroups;
    }

	private UserSession getUserSession(){
		return GlobalVariables.getUserSession();
	}
	
}

