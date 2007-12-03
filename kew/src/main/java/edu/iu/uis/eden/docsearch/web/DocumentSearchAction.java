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
package edu.iu.uis.eden.docsearch.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocumentSearchGenerator;
import edu.iu.uis.eden.docsearch.DocumentSearchResult;
import edu.iu.uis.eden.docsearch.DocumentSearchResultComponents;
import edu.iu.uis.eden.docsearch.DocumentSearchService;
import edu.iu.uis.eden.docsearch.SavedSearchResult;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.KeyValue;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Document search struts action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
    	// if there's no search criteria, just execute the search
    	if (!docSearchForm.isSearchCriteriaEnabled()) {
    		return doDocSearch(mapping, form, request, response);
    	}
    	return mapping.findForward("success");
    }

    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();;
        StringBuffer lookupUrl = new StringBuffer(basePath);

        String lookupType = docSearchForm.getLookupType();
        docSearchForm.setLookupType(null);

        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=").append(request.getParameter("lookupableImplServiceName"));

        if (lookupType != null && !lookupType.equals("")) {
            lookupUrl.append("&conversionFields=");
            WorkflowLookupable workflowLookupable =  (WorkflowLookupable) GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
            for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
                String returnType = (String) iterator.next();
                lookupUrl.append(returnType).append(":").append(lookupType);
            }
        } else if (!Utilities.isEmpty(docSearchForm.getConversionFields())) {
            lookupUrl.append("&conversionFields=");
            lookupUrl.append(docSearchForm.getConversionFields());
        }

        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }

    public ActionForward basic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        docSearchForm.setIsAdvancedSearch("NO");
        return mapping.findForward("success");
    }

    public ActionForward resetNamedSearches(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, RuntimeException {
        getDocumentSearchService().clearNamedSearches(getUserSession(request).getWorkflowUser());
//        request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
        return mapping.findForward("success");
    }

    public ActionForward advanced(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        docSearchForm.setIsAdvancedSearch("YES");
        return mapping.findForward("success");
    }

    public ActionForward superUserSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        docSearchForm.setSuperUserSearch("YES");
        return mapping.findForward("success");
    }

    public ActionForward clearSuperUserSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        docSearchForm.setSuperUserSearch("NO");
        return mapping.findForward("success");
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
        //retain screen placement vars
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        if (StringUtils.isNotBlank(docSearchForm.getCriteria().getDocTypeFullName())) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(docSearchForm.getCriteria().getDocTypeFullName());
            if (documentType == null) {
                throw new RuntimeException("Document Type invalid : " + docSearchForm.getCriteria().getDocTypeFullName());
            }
            DocumentSearchGenerator generator = documentType.getDocumentSearchGenerator();
            criteria = generator.clearSearch(docSearchForm.getCriteria());
        }
        criteria.setIsAdvancedSearch(docSearchForm.getIsAdvancedSearch());
        criteria.setSuperUserSearch(docSearchForm.getSuperUserSearch());
        docSearchForm.setCriteria(criteria);
        docSearchForm.clearSearchableAttributes();
        return mapping.findForward("success");
    }

    public ActionForward doDocSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	LOG.info("started doDocSearch");
	DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
	String previousDocTypeName = docSearchForm.getDocTypeDisplayName();
	DocumentSearchResultComponents results = null;
	SavedSearchResult result = null;
	if (docSearchForm.getNamedSearch() != null && !"".equals(docSearchForm.getNamedSearch()) && !"ignore".equals(docSearchForm.getNamedSearch())) {
	    result = getDocumentSearchService().getSavedSearchResults(getUserSession(request).getWorkflowUser(), docSearchForm.getNamedSearch());
	    if (result != null) {
		docSearchForm.updateFormUsingSavedSearch(result);
		setDropdowns(docSearchForm, request);
		results = result.getSearchResult();
	    } else {
		LOG.warn("Could not find saved search with name '" + docSearchForm.getNamedSearch() + "' for user " + getUserSession(request).getWorkflowUser());
		ActionErrors errors = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.savedSearches.notFound", docSearchForm.getNamedSearch()));
		saveErrors(request, errors);
		return mapping.findForward("success");
	    }
	} else {
	    docSearchForm.addSearchableAttributesToCriteria();
	    DocSearchCriteriaVO criteria = docSearchForm.getCriteria();
	    if (docSearchForm.isInitiatorUser()) {
		criteria.setInitiator(getUserSession(request).getNetworkId());
	    }
	    results = getDocumentSearchService().getList(getUserSession(request).getWorkflowUser(), docSearchForm.getCriteria());
	    result = new SavedSearchResult(criteria, results);
	}

	List columns = results.getColumns();
	MessageResources mr = getResources(request);
	mr.setReturnNull(true);
	Locale locale = (Locale) request.getAttribute(Globals.LOCALE_KEY);
	for (Iterator iter = columns.iterator(); iter.hasNext();) {
	    Column column = (Column) iter.next();
	    if ((column.getColumnTitle() == null) || (column.getColumnTitle().trim().length() == 0)) {
		String title = mr.getMessage(locale, "docSearch.DocumentSearch.results.label." + column.getKey());
		if (StringUtils.isBlank(title)) {
		    title = "** No Title Available **";
		}
		column.setColumnTitle(title);
	    }
	}

	// adjust results and result objects
	result = new SavedSearchResult(docSearchForm.getCriteria(), new DocumentSearchResultComponents(columns, results.getSearchResults()));
	request.setAttribute("reqSearchResultColumns", result.getSearchResult().getColumns());
	request.setAttribute("reqSearchResults", result.getSearchResult().getSearchResults());
	// request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
	request.setAttribute("key", getUserSession(request).addObject(new State(docSearchForm, result)));
	if (docSearchForm.getCriteria().isOverThreshold() && docSearchForm.getCriteria().getSecurityFilteredRows() > 0) {
	    ActionErrors errors = new ActionErrors();
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.exceededThresholdAndSecurityFiltered", String.valueOf(results.getSearchResults().size()), docSearchForm.getCriteria().getSecurityFilteredRows()));
	    saveErrors(request, errors);
	} else if (docSearchForm.getCriteria().getSecurityFilteredRows() > 0) {
	    ActionErrors errors = new ActionErrors();
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.securityFiltered", docSearchForm.getCriteria().getSecurityFilteredRows()));
	    saveErrors(request, errors);
	} else if (docSearchForm.getCriteria().isOverThreshold()) {
	    ActionErrors errors = new ActionErrors();
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.exceededThreshold", String.valueOf(results.getSearchResults().size())));
	    saveErrors(request, errors);
	}

	LOG.info("end doDocSearch");
	return mapping.findForward("success");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        DocumentSearchForm docSearchForm = (DocumentSearchForm)form;
        Preferences preferences = getUserSession(request).getPreferences();
        request.setAttribute("preferences", preferences);
// request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
        setDropdowns(docSearchForm, request);
        return null;
    }

    @Override
    public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
        DocumentSearchForm docSearchForm = (DocumentSearchForm)form;
        request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
        docSearchForm.checkForAdditionalFields();
//        List namedSearches = (List)request.getAttribute("namedSearches");
//        if ( (namedSearches == null) || (namedSearches.isEmpty()) ) {
//            request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
//        }
        return null;
    }

    @Override
    public void establishExceptionFinalState(HttpServletRequest request, ActionForm form) throws Exception {
	establishFinalState(request, form);
    }

    private List getSavedSearches(WorkflowUser workflowUser) {
        List savedSearches = new ArrayList();
        savedSearches.add(new KeyValue("", "Searches"));
        savedSearches.add(new KeyValue("ignore", "-----"));
        savedSearches.add(new KeyValue("ignore", "&nbsp;&nbsp;Named Searches"));
        List namedSearches = getDocumentSearchService().getNamedSearches(workflowUser);
        for (Iterator iter = namedSearches.iterator(); iter.hasNext();) {
            KeyValue namedSearch = (KeyValue) iter.next();
            savedSearches.add(new KeyValue(namedSearch.getKey(), "&nbsp;&nbsp;&nbsp;&nbsp;" + namedSearch.getValue()));
        }
        savedSearches.add(new KeyValue("ignore", "-----"));
        savedSearches.add(new KeyValue("ignore", "&nbsp;&nbsp;Recent Searches"));
        List mostRecentSearches = getDocumentSearchService().getMostRecentSearches(workflowUser);
        for (Iterator iter = mostRecentSearches.iterator(); iter.hasNext();) {
            KeyValue recentSearch = (KeyValue) iter.next();
            savedSearches.add(new KeyValue(recentSearch.getKey(), "&nbsp;&nbsp;&nbsp;&nbsp;" + (recentSearch.getValue().length() > 100 ? recentSearch.getValue().substring(0, 100) + "..." : recentSearch.getValue())));
        }
        return savedSearches;
    }

    private DocumentSearchService getDocumentSearchService() {
        return ((DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE));
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DocumentSearchForm documentSearchForm = (DocumentSearchForm) form;

        if (request.getParameter("docTypeFullName") != null) {
        	documentSearchForm.setNamedSearch("");
        	documentSearchForm.getCriteria().setNamedSearch("");
        	documentSearchForm.clearSearchableAttributes();
        }
        if (request.getParameter("workgroupId") != null) {
            Long groupId = new Long(request.getParameter("workgroupId"));
            Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(groupId));
            documentSearchForm.getCriteria().setWorkgroupViewerName(workgroup.getDisplayName());
        }
        return mapping.findForward("success");
    }

    private static void setDropdowns(DocumentSearchForm dsForm, HttpServletRequest request) {
        LOG.debug("Entered setDropDowns");

        List documentRouteStatus = new ArrayList();
        documentRouteStatus.add(new KeyValue("", "All"));
        documentRouteStatus.addAll(EdenConstants.DOCUMENT_SEARCH_SEARCHABLE_DOCUMENT_STATUSES);
        request.setAttribute("documentRouteStatus", documentRouteStatus);

        if (! Utilities.isEmpty(dsForm.getCriteria().getDocTypeFullName())) {
            List qualifierLogic = new ArrayList();
            qualifierLogic.add(new KeyValue("equal", "Exactly"));
            qualifierLogic.add(new KeyValue("before", "Before"));
            qualifierLogic.add(new KeyValue("after", "After"));
            request.setAttribute("qualifierLogic", qualifierLogic);

            //people are going to be feeding us doctype names by url for inline doc search so check for a null doctype to give
            //a sensible error
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(dsForm.getCriteria().getDocTypeFullName());
            if (documentType == null) {
                throw new RuntimeException("Document Type invalid : " + dsForm.getCriteria().getDocTypeFullName());
            }
            List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
            RouteNode blankNode = new RouteNode();//for a default no choice option
            blankNode.setRouteNodeId(new Long(-1));
            blankNode.setRouteNodeName("");
            routeNodes.add(0, blankNode);
            request.setAttribute("routeNodes", routeNodes);
        }
        LOG.debug("Leaving setDropDowns");
    }

    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;

        State state = (State)getUserSession(request).retrieveObject(request.getParameter("key"));
        state.updateForm(docSearchForm);
        DocumentSearchResultComponents searchComponents = state.getResult().getSearchResult();
        List<Column> columns = searchComponents.getColumns();
        List<DocumentSearchResult> displayResults = searchComponents.getSearchResults();
        DocSearchCriteriaVO criteria = state.getResult().getDocSearchCriteriaVO();

        docSearchForm.setCriteria(criteria);
        setDropdowns(docSearchForm, request);
        docSearchForm.setNamedSearch("");

        boolean ascending = true;
        String sortOrderParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_ORDER);
        String sortOrder = request.getParameter(sortOrderParameter);
        if (sortOrder == null) {
        	sortOrder = "1";
        }
        if (sortOrder.equals("2") ){
        	ascending = false;
        }
        String sortNameParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_SORT);
        String sortName = request.getParameter(sortNameParameter);
        Column sortColumn = getSortColumn(columns, sortName);
        sortDisplayList(sortColumn, displayResults, ascending);
        state.getResult().setSearchResult(new DocumentSearchResultComponents(columns,displayResults));
        request.setAttribute("key", getUserSession(request).addObject(state));
        request.setAttribute("reqSearchResultColumns", state.getResult().getSearchResult().getColumns());
        request.setAttribute("reqSearchResults", state.getResult().getSearchResult().getSearchResults());
        return mapping.findForward("success");
    }

    private Column getSortColumn(List columns, String sortName) {
    	if (StringUtils.isEmpty(sortName)) {
    		return (Column)columns.get(0);
    	}
    	for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			Column column = (Column) iterator.next();
			if (column.getSortName().equals(sortName)) {
				return column;
			}
		}
    	throw new WorkflowRuntimeException("Could not sort based on the given sort name of " + sortName);
    }

    private void sortDisplayList(Column sortColumn, List<DocumentSearchResult> displayList, boolean ascending) {
    	Collections.sort(displayList, new ColumnComparator(sortColumn, ascending));
    }

    private class ColumnComparator implements Comparator<Object> {

    	private Column column;
    	private boolean ascending;

    	public ColumnComparator(Column column, boolean ascending) {
    		this.column = column;
    		this.ascending = ascending;
    	}

		public int compare(Object row1, Object row2) {
			try {
                Object property1 = PropertyUtils.getProperty(row1, column.getSortName());
                Object property2 = PropertyUtils.getProperty(row2, column.getSortName());
                int compare = 0;
                if ((property1 != null) || (property2 != null)) {
                    if ( (property1 != null) && (property2 != null) && (!(property1.getClass().equals(property2.getClass()))) ) {
                        // two classes are unequal... throw exception
                        String errorMessage = "Found classes in this comparator that are unequal (property class '" + property1.getClass().getName() + "' is not equal to property class '" + property2.getClass().getName() + "')";
                        LOG.error("compare() " + errorMessage);
                        throw new RuntimeException(errorMessage);
                    }
                    if ( (property1 instanceof Integer) || (property2 instanceof Integer) ) {
                        Integer value1 = (property1 != null) ? (Integer) property1 : Integer.MIN_VALUE;
                        Integer value2 = (property2 != null) ? (Integer) property2 : Integer.MIN_VALUE;
                        compare = value1.compareTo(value2);
                    } else if ( (property1 instanceof Long) || (property2 instanceof Long) ) {
                        Long value1 = (property1 != null) ? (Long) property1 : Long.MIN_VALUE;
                        Long value2 = (property2 != null) ? (Long) property2 : Long.MIN_VALUE;
                        compare = value1.compareTo(value2);
                    } else if ( (property1 instanceof BigDecimal) || (property2 instanceof BigDecimal) ) {
                        BigDecimal value1 = (property1 != null) ? (BigDecimal) property1 : BigDecimal.ZERO;
                        BigDecimal value2 = (property2 != null) ? (BigDecimal) property2 : BigDecimal.ZERO;
                        compare = value1.compareTo(value2);
                    } else if ( (property1 instanceof Timestamp) || (property2 instanceof Timestamp) ) {
                        Timestamp value1 = (Timestamp) property1;
                        Timestamp value2 = (Timestamp) property2;
                        if ( (value1 != null) && (value2 != null) ) {
                            compare = value1.compareTo(value2);
                        }
                        else if ( (value1 == null) && (value2 != null) ) {
                            compare = -1;
                        }
                        else if ( (value1 != null) && (value2 == null) ) {
                            compare = 1;
                        }
                    } else {
                        // at this point... assume String
                        String value1 = (property1 != null) ? (String) property1 : "";
                        String value2 = (property2 != null) ? (String) property2 : "";
                        compare = String.CASE_INSENSITIVE_ORDER.compare(value1, value2);
                    }
                }


//				String property1Value = (property1 != null) ? property1.toString() : "";
//				String property2Value = (property2 != null) ? property2.toString() : "";
//				if (Column.INTEGER.equals(column.getType())) {
//					Integer i1 = Integer.valueOf(property1Value);
//					Integer i2 = Integer.valueOf(property2Value);
//					compare = i1.compareTo(i2);
//				} else if (Column.LONG.equals(column.getType())) {
//					Long l1 = Long.valueOf(property1Value);
//					Long l2 = Long.valueOf(property2Value);
//					compare = l1.compareTo(l2);
//				} else if (Column.FLOAT.equals(column.getType())) {
//					Float f1 = Float.valueOf(property1Value);
//					Float f2 = Float.valueOf(property2Value);
//					compare = f1.compareTo(f2);
//				} else if (Column.DATETIME.equals(column.getType())) {
//					Timestamp t1 = Timestamp.valueOf(property1Value);
//					Timestamp t2 = Timestamp.valueOf(property2Value);
//					compare = t1.compareTo(t2);
//				} else {
//					compare = property1Value.compareTo(property2Value);
//				}
				if (!ascending) {
					compare *= -1;
				}
				return compare;
			} catch (Exception e) {
				throw new WorkflowRuntimeException(e);
			}
		}


    }


    private class State {
    	private boolean headerBarEnabled = true;
    	private boolean searchCriteriaEnabled = true;
    	private SavedSearchResult result;
    	public State(DocumentSearchForm form, SavedSearchResult result) {
    		this.headerBarEnabled = form.isHeaderBarEnabled();
    		this.searchCriteriaEnabled = form.isSearchCriteriaEnabled();
    		this.result = result;
    	}
    	public void updateForm(DocumentSearchForm form) {
    		form.setCriteria(result.getDocSearchCriteriaVO());
    		form.setHeaderBarEnabled(isHeaderBarEnabled());
    		form.setSearchCriteriaEnabled(isSearchCriteriaEnabled());
    	}
		public SavedSearchResult getResult() {
			return result;
		}
		public boolean isHeaderBarEnabled() {
			return headerBarEnabled;
		}
		public boolean isSearchCriteriaEnabled() {
			return searchCriteriaEnabled;
		}

    }

}