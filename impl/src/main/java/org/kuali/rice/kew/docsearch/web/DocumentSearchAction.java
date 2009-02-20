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
package org.kuali.rice.kew.docsearch.web;


/**
 * Document search struts action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchAction {
//FIXME: delete this when doc search is fully moved over
//extends WorkflowAction {
//
//    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchAction.class);
//
//    public static final Map<String,String> SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY = new HashMap<String,String>();
//    static {
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID,"docSearch.DocumentSearch.results.label.documentId");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,"docSearch.DocumentSearch.results.label.type");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,"docSearch.DocumentSearch.results.label.title");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,"docSearch.DocumentSearch.results.label.routeStatus");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,"docSearch.DocumentSearch.results.label.initiator");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,"docSearch.DocumentSearch.results.label.dateCreated");
//        SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG,"docSearch.DocumentSearch.results.label.routeLog");
//    }
//
//    public static final List<KeyValue> DOCUMENT_SEARCH_SEARCHABLE_DOCUMENT_STATUSES = Arrays.asList(new KeyValue[] { new KeyValue("I", "Initiated"),
//            new KeyValue("R", "Enroute"),
//            new KeyValue("S", "Saved"),
//            new KeyValue("F", "Final"),
//            new KeyValue("A", "Approved"),
//            new KeyValue("X", "Canceled"),
//            new KeyValue("E", "Exception"),
//            new KeyValue("P", "Processed"),
//            new KeyValue("D", "Disapproved") });
//
//    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        docSearchForm.setupPropertyFieldsUsingCriteria();
//        adjustStateAndForm(getCurrentState(request), docSearchForm, getUserSession(request));
//        // if there's no search criteria, just execute the search
//        if (!docSearchForm.isShowSearchCriteria()) {
//            return doDocSearch(mapping, form, request, response);
//        }
//        return mapping.findForward("success");
//    }
//
//    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
//        StringBuffer lookupUrl = new StringBuffer(basePath);
//
//        String lookupType = docSearchForm.getLookupType();
//        docSearchForm.setLookupType(null);
//
//        String docFormKey = getUserSession(request).addObject(form);
//        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(docFormKey).append("&lookupableImplServiceName=").append(request.getParameter("lookupableImplServiceName"));
//
//        StringBuffer conversionFields = new StringBuffer();
//
//        if (lookupType != null && !lookupType.equals("")) {
//            lookupUrl.append("&conversionFields=");
//
//            //WorkflowLookupable workflowLookupable = (WorkflowLookupable) GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));
//            //for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
//            //    String returnType = (String) iterator.next();
//            //    conversionFields.append(returnType).append(":").append(lookupType);
//            //}
//            lookupUrl.append(conversionFields);
//        } else if (!Utilities.isEmpty(docSearchForm.getConversionFields())) {
//            lookupUrl.append("&conversionFields=");
//            lookupUrl.append(docSearchForm.getConversionFields());
//            conversionFields.append(docSearchForm.getConversionFields());
//        }
//
//        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
//
//        /*
//        * Code below added by Jeremy and Eric - 12-10-2008
//        *
//        * Temporarily,  until Document Search is converted to a proper lookup, let's have our outgoing lookup links
//        * for the Document Type lookup
//        * generated in here, instead of using the KNS tags to generate our lookup links
//        */
//       String lookupableImplServiceName = request.getParameter("lookupableImplServiceName");
//       if (lookupableImplServiceName.equals(DocumentSearchCriteriaProcessor.DOC_TYP_LOOKUPABLE)) {
//           lookupUrl = new StringBuffer();
//           lookupUrl.append("../kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.doctype.bo.DocumentType&docFormKey=" +
//               docFormKey +
//               "&returnLocation=" +
//               basePath +
//               mapping.getPath() +
//               ".do" +
//               "&conversionFields=" +
//               conversionFields.toString());
//       }
//
//        return new ActionForward(lookupUrl.toString(), true);
//    }
//
//    public ActionForward basic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        docSearchForm.setIsAdvancedSearch("NO");
//        return mapping.findForward("success");
//    }
//
//    public ActionForward resetNamedSearches(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, RuntimeException {
//        getDocumentSearchService().clearNamedSearches(getUserSession(request).getPrincipalId());
//        return mapping.findForward("success");
//    }
//
//    public ActionForward advanced(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        docSearchForm.setIsAdvancedSearch(DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING);
//        return mapping.findForward("success");
//    }
//
//    public ActionForward superUserSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        docSearchForm.setSuperUserSearch(DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING);
//        return mapping.findForward("success");
//    }
//
//    public ActionForward clearSuperUserSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        docSearchForm.setSuperUserSearch("NO");
//        return mapping.findForward("success");
//    }
//
//    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        String currentDocTypeName = docSearchForm.getCriteria().getDocTypeFullName();
//        String advancedSearchValue = docSearchForm.getIsAdvancedSearch();
//        String superUserSearchValue = docSearchForm.getSuperUserSearch();
//        // set up new criteria object using generator class if possible
//        DocSearchCriteriaDTO newCriteria = null;
//        if (StringUtils.isNotBlank(currentDocTypeName)) {
//            newCriteria = getValidDocumentType(currentDocTypeName).getDocumentSearchGenerator().clearSearch(docSearchForm.getCriteria());
//        }
//        if (newCriteria == null) {
//            newCriteria = new DocSearchCriteriaDTO();
//        }
//        newCriteria.setIsAdvancedSearch(advancedSearchValue);
//        newCriteria.setSuperUserSearch(superUserSearchValue);
//        // adjust the processor class and state if necessary
//        State state = getCurrentState(request);
//        DocumentSearchCriteriaProcessor currentProcessor = state.getCriteriaProcessor();
//        if (StringUtils.equals(currentProcessor.getDocSearchCriteriaDTO().getDocTypeFullName(),newCriteria.getDocTypeFullName())) {
//            // since doc type names are equal do not reset the state or processor only the criteria object
//            state.getCriteriaProcessor().setDocSearchCriteriaDTO(newCriteria);
//        } else {
//            // document types are not equal so reset the state and processor using the new criteria
//            state = adjustStateAndForm(null, docSearchForm, newCriteria, getUserSession(request));
//        }
//        setAdjustedState(request, state);
////        DocumentSearchCriteriaProcessor processor = buildCriteriaProcessor((state != null) ? state.getCriteriaProcessor() : null, docSearchForm, getUserSession(request));
////        if (StringUtils.isNotBlank(docSearchForm.getCriteria().getDocTypeFullName())) {
////            DocumentSearchGenerator generator = getValidDocumentType(docSearchForm.getCriteria().getDocTypeFullName()).getDocumentSearchGenerator();
////            DocSearchCriteriaDTO newCriteria = generator.clearSearch(processor.getDocSearchCriteriaDTO());
////            if (newCriteria == null) {
////                newCriteria = new DocSearchCriteriaDTO();
////            }
////            newCriteria.setIsAdvancedSearch(processor.getDocSearchCriteriaDTO().getIsAdvancedSearch());
////            newCriteria.setSuperUserSearch(processor.getDocSearchCriteriaDTO().getSuperUserSearch());
////            processor = buildCriteriaProcessor(processor, docSearchForm, newCriteria, getUserSession(request));
////            setAdjustedState(request, state);
////        }
//       docSearchForm.clearSearchableAttributeProperties();
//       return mapping.findForward("success");
//    }
//
//    public ActionForward doDocSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        LOG.info("started doDocSearch");
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        DocumentSearchResultComponents results = null;
//        SavedSearchResult result = null;
//        State currentState = getCurrentState(request);
//        if (docSearchForm.getNamedSearch() != null && !"".equals(docSearchForm.getNamedSearch()) && !"ignore".equals(docSearchForm.getNamedSearch())) {
//            result = getDocumentSearchService().getSavedSearchResults(getUserSession(request).getPerson().getPrincipalId(), docSearchForm.getNamedSearch());
//            if (result != null) {
//                // if using a saved search assume new state needs created by discarding old state
//                currentState = null;
//                docSearchForm.setCriteriaProcessor(buildCriteriaProcessor(null, docSearchForm, getUserSession(request)));
//                docSearchForm.getCriteriaProcessor().setDocSearchCriteriaDTO(result.getDocSearchCriteriaDTO());
//                docSearchForm.clearSearchableAttributeProperties();
//                docSearchForm.checkForAdditionalFields();
//                docSearchForm.setupPropertyFieldsUsingCriteria();
//                docSearchForm.setNamedSearch("");
//                setDropdowns(docSearchForm, request);
//                results = result.getSearchResult();
//            } else {
//                LOG.warn("Could not find saved search with name '" + docSearchForm.getNamedSearch() + "' for user " + getUserSession(request).getPrincipalName());
//                ActionErrors errors = new ActionErrors();
//                errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.savedSearches.notFound", docSearchForm.getNamedSearch()));
//                saveErrors(request, errors);
//                return mapping.findForward("success");
//            }
//        } else {
//            docSearchForm.addSearchableAttributesToCriteria();
//            DocSearchCriteriaDTO criteria = docSearchForm.getCriteria();
//            if (docSearchForm.isInitiatorUser()) {
//                criteria.setInitiator(getUserSession(request).getPrincipalName());
//            }
//            results = getDocumentSearchService().getList(getUserSession(request).getPerson().getPrincipalId(), criteria);
//            result = new SavedSearchResult(criteria, results);
//        }
//
//        List columns = results.getColumns();
//        MessageResources mr = getResources(request);
//        mr.setReturnNull(true);
//        Locale locale = (Locale) request.getAttribute(Globals.LOCALE_KEY);
//        for (Iterator iter = columns.iterator(); iter.hasNext();) {
//            DocumentSearchColumn column = (DocumentSearchColumn) iter.next();
//            if ((column.getColumnTitle() == null) || (column.getColumnTitle().trim().length() == 0)) {
//                String title = mr.getMessage(locale, SEARCH_RESULT_LABEL_KEYS_BY_COLUMN_KEY.get(column.getKey()));
//                if (StringUtils.isBlank(title)) {
//                    title = "** No Title Available **";
//                }
//                column.setColumnTitle(title);
//            }
//        }
//
//        // adjust results and result objects
//        result = new SavedSearchResult(docSearchForm.getCriteria(), new DocumentSearchResultComponents(columns, results.getSearchResults()));
//        request.setAttribute("reqSearchResultColumns", result.getSearchResult().getColumns());
//        request.setAttribute("reqSearchResults", result.getSearchResult().getSearchResults());
//        State state = adjustStateAndForm(currentState, docSearchForm, getUserSession(request));
//        state.setResult(result);
//        setAdjustedState(request, state);
//        if (docSearchForm.getCriteria().isOverThreshold() && docSearchForm.getCriteria().getSecurityFilteredRows() > 0) {
//            ActionErrors errors = new ActionErrors();
//            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.exceededThresholdAndSecurityFiltered", String.valueOf(results.getSearchResults().size()), docSearchForm.getCriteria().getSecurityFilteredRows()));
//            saveErrors(request, errors);
//        } else if (docSearchForm.getCriteria().getSecurityFilteredRows() > 0) {
//            ActionErrors errors = new ActionErrors();
//            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.securityFiltered", docSearchForm.getCriteria().getSecurityFilteredRows()));
//            saveErrors(request, errors);
//        } else if (docSearchForm.getCriteria().isOverThreshold()) {
//            ActionErrors errors = new ActionErrors();
//            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("docsearch.DocumentSearchService.exceededThreshold", String.valueOf(results.getSearchResults().size())));
//            saveErrors(request, errors);
//        }
//
//        LOG.info("end doDocSearch");
//        return mapping.findForward("success");
//    }
//
//    private static DocumentType getValidDocumentType(String docTypeName) {
//        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(docTypeName);
//        if (documentType == null) {
//            throw new RuntimeException("Document Type invalid : " + docTypeName);
//        }
//        return documentType;
//    }
//
//    private State getCurrentState(HttpServletRequest request) {
//        return (State)request.getAttribute("currentSearchState");
//    }
//
//    private void setAdjustedState(HttpServletRequest request, State state) {
//        request.removeAttribute("currentSearchState");
//        request.setAttribute("currentSearchState", state);
//    }
//
//    private State adjustStateAndForm(State currentState, DocumentSearchForm docSearchForm, UserSession userSession) {
//        State state = new State(docSearchForm);
//        if (currentState != null) {
//            state = new State(currentState);
//        }
//        state.setCriteriaProcessor(buildCriteriaProcessor(state.getCriteriaProcessor(), docSearchForm, userSession));
//        state.updateForm(docSearchForm);
//        return state;
//    }
//
//    private State adjustStateAndForm(State currentState, DocumentSearchForm docSearchForm, DocSearchCriteriaDTO criteria, UserSession userSession) {
//        State state = new State(docSearchForm);
//        if (currentState != null) {
//            state = new State(currentState);
//        }
//        state.setCriteriaProcessor(buildCriteriaProcessor(state.getCriteriaProcessor(), docSearchForm, criteria, userSession));
//        state.updateForm(docSearchForm);
//        return state;
//    }
//
//    private DocumentSearchCriteriaProcessor buildCriteriaProcessor(DocumentSearchCriteriaProcessor processor, DocumentSearchForm docSearchForm, UserSession userSession) {
//        return buildCriteriaProcessor(processor, docSearchForm, docSearchForm.getCriteria(), userSession);
//    }
//
//    private DocumentSearchCriteriaProcessor buildCriteriaProcessor(DocumentSearchCriteriaProcessor processor, DocumentSearchForm docSearchForm, DocSearchCriteriaDTO criteria, UserSession userSession) {
//        if (processor == null) {
//            // no criteria processor... set one up
//            if ( (criteria != null) && (StringUtils.isNotBlank(criteria.getDocTypeFullName())) ) {
//                processor = getValidDocumentType(criteria.getDocTypeFullName()).getDocumentSearchCriteriaProcessor();
//            } else {
//                processor = new StandardDocumentSearchCriteriaProcessor();
//            }
//        } else {
//            // criteria processor exists in state... check for match against form criteria doc type name
//            if (Utilities.isEmpty(criteria.getDocTypeFullName())) {
//                // document type name was cleared out... clear the criteria processor
//                processor = new StandardDocumentSearchCriteriaProcessor();
//            } else if (!StringUtils.equals(criteria.getDocTypeFullName(), processor.getDocSearchCriteriaDTO().getDocTypeFullName())) {
//                // document type name is not the same as previous state's criteria... update criteria processor
//                processor = getValidDocumentType(criteria.getDocTypeFullName()).getDocumentSearchCriteriaProcessor();
//            }
//        }
//        docSearchForm.addSearchableAttributesToCriteria();
//        DocSearchCriteriaDTO newCriteria = criteria;
//        if (docSearchForm.isInitiatorUser()) {
//            newCriteria.setInitiator(userSession.getPrincipalName());
//        }
//        if (newCriteria != null) {
//            processor.setDocSearchCriteriaDTO(newCriteria);
//        }
////        processor.getDocSearchCriteriaDTO().setSuperUserSearch(formCriteria.getSuperUserSearch());
////        processor.getDocSearchCriteriaDTO().setIsAdvancedSearch(docSearchForm.getIsAdvancedSearch());
//        processor.setSearchingUser(userSession.getPrincipalId());
//        return processor;
//    }
//
//    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        Preferences preferences = getUserSession(request).getPreferences();
////        setupState(request, docSearchForm);
//        State currentState = getOriginalState(request);
//        State newState = adjustStateAndForm(currentState, docSearchForm, getUserSession(request));
////        if (currentState == null) {
//            newState.updateForm(docSearchForm);
////        }
//        setAdjustedState(request, newState);
//        request.setAttribute("preferences", preferences);
//        // request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
//        setDropdowns(docSearchForm, request);
//        docSearchForm.checkForAdditionalFields();
//        return null;
//    }
//
//    private State getOriginalState(HttpServletRequest request) {
//        String searchStateKeyValue = request.getParameter("searchStateKey");
//        if (Utilities.isEmpty(searchStateKeyValue)) {
//            searchStateKeyValue = (String) request.getAttribute("searchStateKey");
//        }
//        return (State) getUserSession(request).retrieveObject(searchStateKeyValue);
//    }
//
//    @Override
//    public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
////        request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getWorkflowUser()));
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//        State currentState = getCurrentState(request);
//        if (currentState == null) {
//            throw new RuntimeException("Search state is empty and search cannot proceed");
//        }
//        currentState.updateForm(docSearchForm);
//        request.setAttribute("searchStateKey", getUserSession(request).addObject(currentState));
//        request.removeAttribute("currentSearchState");
////        setupState(request, docSearchForm);
////        docSearchForm.checkForAdditionalFields();
//        updateNamedSearches(request, docSearchForm);
//        return null;
//    }
//
//    private void updateNamedSearches(HttpServletRequest request, DocumentSearchForm docSearchForm) {
//        request.setAttribute("namedSearches", getSavedSearches(getUserSession(request).getPrincipalId()));
//        docSearchForm.checkForAdditionalFields();
//        docSearchForm.setupPropertyFieldsUsingCriteria();
//    }
//
//    @Override
//    public void establishExceptionFinalState(HttpServletRequest request, ActionForm form) throws Exception {
//        try {
//            updateNamedSearches(request, (DocumentSearchForm)form);
//        } catch (Exception e) {
//            LOG.warn("Swallowing exception thrown by establishFinalState since we already had a source exception: " + e.getLocalizedMessage());
//        }
//    }
//
//    private List getSavedSearches(String principalId) {
//        List savedSearches = new ArrayList();
//        savedSearches.add(new KeyValue("", "Searches"));
//        savedSearches.add(new KeyValue("ignore", "-----"));
//        savedSearches.add(new KeyValue("ignore", "&nbsp;&nbsp;Named Searches"));
//        List namedSearches = getDocumentSearchService().getNamedSearches(principalId);
//        for (Iterator iter = namedSearches.iterator(); iter.hasNext();) {
//            KeyValue namedSearch = (KeyValue) iter.next();
//            savedSearches.add(new KeyValue(namedSearch.getKey(), "&nbsp;&nbsp;&nbsp;&nbsp;" + namedSearch.getValue()));
//        }
//        savedSearches.add(new KeyValue("ignore", "-----"));
//        savedSearches.add(new KeyValue("ignore", "&nbsp;&nbsp;Recent Searches"));
//        List mostRecentSearches = getDocumentSearchService().getMostRecentSearches(principalId);
//        for (Iterator iter = mostRecentSearches.iterator(); iter.hasNext();) {
//            KeyValue recentSearch = (KeyValue) iter.next();
//            savedSearches.add(new KeyValue(recentSearch.getKey(), "&nbsp;&nbsp;&nbsp;&nbsp;" + (recentSearch.getValue().length() > 100 ? recentSearch.getValue().substring(0, 100) + "..." : recentSearch.getValue())));
//        }
//        return savedSearches;
//    }
//
//    private DocumentSearchService getDocumentSearchService() {
//        return ((DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE));
//    }
//
//    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        DocumentSearchForm documentSearchForm = (DocumentSearchForm) form;
//
//        String docTypeFullName = request.getParameter("docTypeFullName");
//        if (docTypeFullName != null) {
//            documentSearchForm.setNamedSearch("");
//            documentSearchForm.getCriteria().setNamedSearch("");
//            documentSearchForm.clearSearchableAttributeProperties();
//        }
//        if (request.getParameter("workgroupId") != null) {
//            String groupId = request.getParameter("workgroupId");
//            KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
//            documentSearchForm.getCriteria().setWorkgroupViewerName(group.getGroupName());
//            documentSearchForm.getCriteria().setWorkgroupViewerNamespace(group.getNamespaceCode());
//        }
//        String documentTypeId = request.getParameter("documentTypeId");
//        if (documentTypeId != null) {
//            documentSearchForm.setNamedSearch("");
//            documentSearchForm.getCriteria().setNamedSearch("");
//            documentSearchForm.clearSearchableAttributeProperties();
//            DocumentType docType = KEWServiceLocator.getDocumentTypeService().findById(new Long(documentTypeId));
//            documentSearchForm.setDocTypeFullName(docType.getName());
//        }
//        return mapping.findForward("success");
//    }
//
//    private static void setDropdowns(DocumentSearchForm dsForm, HttpServletRequest request) {
//        LOG.debug("Entered setDropDowns");
//
//        List documentRouteStatus = new ArrayList();
//        documentRouteStatus.add(new KeyValue("", "All"));
//        documentRouteStatus.addAll(DOCUMENT_SEARCH_SEARCHABLE_DOCUMENT_STATUSES);
//        request.setAttribute("documentRouteStatus", documentRouteStatus);
//
//        if (!Utilities.isEmpty(dsForm.getCriteria().getDocTypeFullName())) {
//            List qualifierLogic = new ArrayList();
//            for (String key : KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.keySet()) {
//                qualifierLogic.add(new KeyValue(key, KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.get(key)));
//            }
//            request.setAttribute("qualifierLogic", qualifierLogic);
//
//            // people are going to be feeding us doctype names by url for inline doc search so check for a null doctype to
//            // give
//            // a sensible error
//            List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getValidDocumentType(dsForm.getCriteria().getDocTypeFullName()), true);
//            RouteNode blankNode = new RouteNode();// for a default no choice option
//            blankNode.setRouteNodeId(new Long(-1));
//            blankNode.setRouteNodeName("");
//            routeNodes.add(0, blankNode);
//            request.setAttribute("routeNodes", routeNodes);
//        }
//        LOG.debug("Leaving setDropDowns");
//    }
//
//    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        DocumentSearchForm docSearchForm = (DocumentSearchForm) form;
//
//        State state = getCurrentState(request);
//        state.updateForm(docSearchForm);
//        State originalState = getOriginalState(request);
//        docSearchForm.getCriteriaProcessor().setDocSearchCriteriaDTO(originalState.getCriteriaProcessor().getDocSearchCriteriaDTO());
//        DocumentSearchResultComponents searchComponents = state.getResult().getSearchResult();
//        List<DocumentSearchColumn> columns = searchComponents.getColumns();
//        List<DocumentSearchResult> displayResults = searchComponents.getSearchResults();
//
////        docSearchForm.setCriteriaProcessor(state.getCriteriaProcessor());
////        docSearchForm.setCriteria(state.getResult().getDocSearchCriteriaDTO());
//        setDropdowns(docSearchForm, request);
//        docSearchForm.setNamedSearch("");
//
//        boolean ascending = true;
//        String sortOrderParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_ORDER);
//        String sortOrder = request.getParameter(sortOrderParameter);
//        if (sortOrder == null) {
//            sortOrder = "1";
//        }
//        if (sortOrder.equals("2")) {
//            ascending = false;
//        }
//        String sortNameParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_SORT);
//        String sortName = request.getParameter(sortNameParameter);
//        DocumentSearchColumn sortColumn = getSortColumn(columns, sortName);
//        sortDisplayList(sortColumn, displayResults, ascending);
//        state.getResult().setSearchResult(new DocumentSearchResultComponents(columns, displayResults));
//        setAdjustedState(request, state);
////        setStateForUse(request, state);
//        request.setAttribute("reqSearchResultColumns", state.getResult().getSearchResult().getColumns());
//        request.setAttribute("reqSearchResults", state.getResult().getSearchResult().getSearchResults());
//        return mapping.findForward("success");
//    }
//
//    private DocumentSearchColumn getSortColumn(List<DocumentSearchColumn> columns, String sortName) {
//        if (StringUtils.isEmpty(sortName)) {
//            return (DocumentSearchColumn) columns.get(0);
//        }
//        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
//            DocumentSearchColumn column = (DocumentSearchColumn) iterator.next();
//            if (column.getSortName().equals(sortName)) {
//                return column;
//            }
//        }
//        throw new WorkflowRuntimeException("Could not sort based on the given sort name of " + sortName);
//    }
//
//    private void sortDisplayList(DocumentSearchColumn sortColumn, List<DocumentSearchResult> displayList, boolean ascending) {
//        Collections.sort(displayList, new ColumnComparator(sortColumn, ascending));
//    }
//
//    private class ColumnComparator implements Comparator<Object> {
//
//        private DocumentSearchColumn column;
//        private boolean ascending;
//
//        public ColumnComparator(DocumentSearchColumn column, boolean ascending) {
//            this.column = column;
//            this.ascending = ascending;
//        }
//
//        public int compare(Object row1, Object row2) {
//            try {
//                Object property1 = PropertyUtils.getProperty(row1, column.getSortName());
//                Object property2 = PropertyUtils.getProperty(row2, column.getSortName());
//                int compare = 0;
//                if ((property1 != null) || (property2 != null)) {
//                    if ((property1 != null) && (property2 != null) && (!(property1.getClass().equals(property2.getClass())))) {
//                        // two classes are unequal... throw exception
//                        String errorMessage = "Found classes in this comparator that are unequal (property class '" + property1.getClass().getName() + "' is not equal to property class '" + property2.getClass().getName() + "')";
//                        LOG.error("compare() " + errorMessage);
//                        throw new RuntimeException(errorMessage);
//                    }
//                    if ((property1 instanceof Integer) || (property2 instanceof Integer)) {
//                        Integer value1 = (property1 != null) ? (Integer) property1 : Integer.MIN_VALUE;
//                        Integer value2 = (property2 != null) ? (Integer) property2 : Integer.MIN_VALUE;
//                        compare = value1.compareTo(value2);
//                    } else if ((property1 instanceof Long) || (property2 instanceof Long)) {
//                        Long value1 = (property1 != null) ? (Long) property1 : Long.MIN_VALUE;
//                        Long value2 = (property2 != null) ? (Long) property2 : Long.MIN_VALUE;
//                        compare = value1.compareTo(value2);
//                    } else if ((property1 instanceof BigDecimal) || (property2 instanceof BigDecimal)) {
//                        BigDecimal value1 = (property1 != null) ? (BigDecimal) property1 : BigDecimal.ZERO;
//                        BigDecimal value2 = (property2 != null) ? (BigDecimal) property2 : BigDecimal.ZERO;
//                        compare = value1.compareTo(value2);
//                    } else if ((property1 instanceof Timestamp) || (property2 instanceof Timestamp)) {
//                        Timestamp value1 = (Timestamp) property1;
//                        Timestamp value2 = (Timestamp) property2;
//                        if ((value1 != null) && (value2 != null)) {
//                            compare = value1.compareTo(value2);
//                        } else if ((value1 == null) && (value2 != null)) {
//                            compare = -1;
//                        } else if ((value1 != null) && (value2 == null)) {
//                            compare = 1;
//                        }
//                    } else {
//                        // at this point... assume String
//                        String value1 = (property1 != null) ? (String) property1 : "";
//                        String value2 = (property2 != null) ? (String) property2 : "";
//                        compare = String.CASE_INSENSITIVE_ORDER.compare(value1, value2);
//                    }
//                }
//
//                // String property1Value = (property1 != null) ? property1.toString() : "";
//                // String property2Value = (property2 != null) ? property2.toString() : "";
//                // if (Column.INTEGER.equals(column.getType())) {
//                // Integer i1 = Integer.valueOf(property1Value);
//                // Integer i2 = Integer.valueOf(property2Value);
//                // compare = i1.compareTo(i2);
//                // } else if (Column.LONG.equals(column.getType())) {
//                // Long l1 = Long.valueOf(property1Value);
//                // Long l2 = Long.valueOf(property2Value);
//                // compare = l1.compareTo(l2);
//                // } else if (Column.FLOAT.equals(column.getType())) {
//                // Float f1 = Float.valueOf(property1Value);
//                // Float f2 = Float.valueOf(property2Value);
//                // compare = f1.compareTo(f2);
//                // } else if (Column.DATETIME.equals(column.getType())) {
//                // Timestamp t1 = Timestamp.valueOf(property1Value);
//                // Timestamp t2 = Timestamp.valueOf(property2Value);
//                // compare = t1.compareTo(t2);
//                // } else {
//                // compare = property1Value.compareTo(property2Value);
//                // }
//                if (!ascending) {
//                    compare *= -1;
//                }
//                return compare;
//            } catch (Exception e) {
//                throw new WorkflowRuntimeException(e);
//            }
//        }
//
//    }
//
//    private class State {
//        private boolean headerBarEnabled = true;
//        private boolean searchCriteriaEnabled = true;
//        private DocumentSearchCriteriaProcessor criteriaProcessor = null;
//        private SavedSearchResult result = null;
//
//        public State(DocumentSearchForm form) {
//            this.headerBarEnabled = form.isHeaderBarEnabled();
//            this.searchCriteriaEnabled = form.isSearchCriteriaEnabled();
//        }
//
//        public State(State state) {
//            this.headerBarEnabled = state.isHeaderBarEnabled();
//            this.searchCriteriaEnabled = state.isSearchCriteriaEnabled();
//            this.criteriaProcessor = state.getCriteriaProcessor();
//            this.result = state.getResult();
//        }
//
//        public void updateForm(DocumentSearchForm form) {
//            updateForm(form, null);
//        }
//
//        public void updateForm(DocumentSearchForm form, DocSearchCriteriaDTO criteria) {
//            form.setCriteriaProcessor(getCriteriaProcessor());
//            if (criteria != null) {
//                form.getCriteriaProcessor().setDocSearchCriteriaDTO(criteria);
//            }
//            form.setHeaderBarEnabled(isHeaderBarEnabled());
//            form.setSearchCriteriaEnabled(isSearchCriteriaEnabled());
//        }
//
//        public void setResult(SavedSearchResult result) {
//            this.result = result;
//        }
//
//        public SavedSearchResult getResult() {
//            return result;
//        }
//
//        public DocumentSearchCriteriaProcessor getCriteriaProcessor() {
//            return criteriaProcessor;
//        }
//
//        public void setCriteriaProcessor(DocumentSearchCriteriaProcessor processor) {
//            this.criteriaProcessor = processor;
//        }
//
//        public boolean isHeaderBarEnabled() {
//            return headerBarEnabled;
//        }
//
//        public boolean isSearchCriteriaEnabled() {
//            return searchCriteriaEnabled;
//        }
//
//    }

}