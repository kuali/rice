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
package edu.iu.uis.eden.docsearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.util.RiceConstants;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceError;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.doctype.SecuritySession;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserUtils;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.KeyValueSort;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandardDocumentSearchGenerator implements DocumentSearchGenerator {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardDocumentSearchGenerator.class);

    private static final String DATABASE_WILDCARD_CHARACTER_STRING = "%";
    private static final char DATABASE_WILDCARD_CHARACTER = DATABASE_WILDCARD_CHARACTER_STRING.toCharArray()[0];

    private static final String CREATE_DATE_FIELD_STRING = " DOC_HDR.DOC_CRTE_DT ";
    private static final String APPROVE_DATE_FIELD_STRING = " DOC_HDR.DOC_APRV_DT ";
    private static final String FINALIZATION_DATE_FIELD_STRING = " DOC_HDR.DOC_FNL_DT ";
    private static final String LAST_STATUS_UPDATE_DATE = " DOC_HDR.DOC_RTE_STAT_MDFN_DT ";

    private static List searchableAttributes;
    private static DocSearchCriteriaVO criteria;
    private static WorkflowUser searchingUser;

    private boolean usingAtLeastOneSearchAttribute = false;

	public StandardDocumentSearchGenerator() {
		super();
		this.searchableAttributes = new ArrayList();
	}

	/**
	 * @param searchableAttributes
	 */
	public StandardDocumentSearchGenerator(List searchableAttributes) {
		this();
		this.searchableAttributes = searchableAttributes;
	}

	public DocSearchCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(DocSearchCriteriaVO criteria) {
		StandardDocumentSearchGenerator.criteria = criteria;
	}

	public List getSearchableAttributes() {
		return searchableAttributes;
	}

	public void setSearchableAttributes(List searchableAttributes) {
		this.searchableAttributes = searchableAttributes;
	}

	public WorkflowUser getSearchingUser() {
		return searchingUser;
	}

	public void setSearchingUser(WorkflowUser searchingUser) {
		StandardDocumentSearchGenerator.searchingUser = searchingUser;
	}

	/* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.DocumentSearchGenerator#clearSearch(edu.iu.uis.eden.docsearch.DocSearchCriteriaVO)
     */
    public DocSearchCriteriaVO clearSearch(DocSearchCriteriaVO searchCriteria) {
        return new DocSearchCriteriaVO();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.DocumentSearchGenerator#performPreSearchConditions(edu.iu.uis.eden.user.WorkflowUser, edu.iu.uis.eden.docsearch.DocSearchCriteriaVO)
     */
    public List<WorkflowServiceError> performPreSearchConditions(WorkflowUser user, DocSearchCriteriaVO searchCriteria) {
    	setCriteria(searchCriteria);
    	return new ArrayList<WorkflowServiceError>();
    }

    protected SearchAttributeCriteriaComponent getSearchableAttributeByFieldName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Attempted to find Searchable Attribute with blank Field name '" + name + "'");
        }
        for (Iterator iter = getCriteria().getSearchableAttributes().iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) iter.next();
            if (name.equals(critComponent.getFormKey())) {
                return critComponent;
            }
        }
        return null;
    }

    protected void addErrorMessageToList(List<WorkflowServiceError> errors, String message) {
        errors.add(new WorkflowServiceErrorImpl(message,"general.message",message));
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.DocumentSearchGenerator#executeSearch(edu.iu.uis.eden.docsearch.DocSearchCriteriaVO, org.kuali.rice.database.platform.Platform)
     */
    public String generateSearchSql(DocSearchCriteriaVO searchCriteria) throws EdenUserNotFoundException {
    	setCriteria(searchCriteria);
        return getDocSearchSQL();
    }
    
    public DocumentType getValidDocumentType(String documentTypeFullName) {
    	if (!Utilities.isEmpty(documentTypeFullName)) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeFullName);
            if (documentType == null) {
    			throw new RuntimeException("No Valid Document Type Found for document type name '" + documentTypeFullName + "'");
            }
            return documentType;
    	}
    	return null;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.DocumentSearchGenerator#validateSearchableAttributes()
     */
    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaVO searchCriteria) {
    	setCriteria(searchCriteria);
        List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();
        List searchableAttributes = criteria.getSearchableAttributes();
        if (searchableAttributes != null && !searchableAttributes.isEmpty()) {
            Map paramMap = new HashMap();
            for (Iterator iter = searchableAttributes.iterator(); iter.hasNext();) {
            	SearchAttributeCriteriaComponent component = (SearchAttributeCriteriaComponent) iter.next();
                if (!Utilities.isEmpty(component.getValues())) {
                    paramMap.put(component.getFormKey(),component.getValues());
                } else {
                    paramMap.put(component.getFormKey(),component.getValue());
                }
            }
            DocumentType documentType = getValidDocumentType(criteria.getDocTypeFullName());
            try {
	            for (Iterator iter = documentType.getSearchableAttributes().iterator(); iter.hasNext();) {
	            	SearchableAttribute searchableAttribute = (SearchableAttribute) iter.next();
	                List searchableErrors = validateSearchableAttribute(searchableAttribute, paramMap);
	                if(!Utilities.isEmpty(searchableAttributes)){
	                    for (Iterator iterator = searchableErrors.iterator(); iterator.hasNext();) {
	                        WorkflowAttributeValidationError error = (WorkflowAttributeValidationError) iterator.next();
	                        errors.add(new WorkflowServiceErrorImpl(error.getKey(), "routetemplate.xmlattribute.error", error.getMessage()));
	                    }
	                }
	            }
            } catch (Exception e) {
                LOG.error("error finding searchable attribute in when validating document search criteria.", e);
            }
        }
        return errors;
    }

    public List<WorkflowAttributeValidationError> validateSearchableAttribute(SearchableAttribute searchableAttribute, Map searchAttributesParameterMap) {
        return searchableAttribute.validateUserSearchInputs(searchAttributesParameterMap);
    }

    protected QueryComponent getSearchableAttributeSql(List searchableAttributes, String whereClausePredicatePrefix) {
        StringBuffer fromSql = new StringBuffer();
        StringBuffer whereSql = new StringBuffer();

        int tableIndex = 1;

        Map<String, List> searchableAttributeRangeComponents = new HashMap<String,List>();
        for (Iterator iterator = searchableAttributes.iterator(); iterator.hasNext(); tableIndex++) {
            SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iterator.next();
            if (!criteriaComponent.isSearchable()) {
                continue;
            }

            SearchableAttributeValue searchAttribute = criteriaComponent.getSearchableAttributeValue();
            if (searchAttribute == null) {
            	// key given for propertyField must not be on document
            	String errorMsg = "The search attribute value associated with key '" + criteriaComponent.getSavedKey() + "' cannot be found";
            	LOG.error("getSearchableAttributeSql() " + errorMsg);
            	throw new RuntimeException(errorMsg);
            }
            if (criteriaComponent.isRangeSearch()) {
            	if (searchableAttributeRangeComponents.containsKey(criteriaComponent.getSavedKey())) {
					List<SearchAttributeCriteriaComponent> criteriaComponents = (List)searchableAttributeRangeComponents.get(criteriaComponent.getSavedKey());
					List<SearchAttributeCriteriaComponent> newCriteriaComponents = new ArrayList<SearchAttributeCriteriaComponent>();
					newCriteriaComponents.addAll(criteriaComponents);
            		newCriteriaComponents.add(criteriaComponent);
            		searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(), newCriteriaComponents);
            	} else {
            		searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(), Arrays.asList(new SearchAttributeCriteriaComponent[]{criteriaComponent}));
            	}
            	continue;
            }
            // if where clause is empty then use passed in prefix... otherwise generate one
            String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());
            QueryComponent qc = generateSearchableAttributeSql(criteriaComponent, whereClausePrefix, tableIndex);
            fromSql.append(qc.getFromSql());
            whereSql.append(qc.getWhereSql());

        }

        for (Iterator iter = searchableAttributeRangeComponents.keySet().iterator(); iter.hasNext();) {
        	String keyName = (String) iter.next();
			List criteriaComponents = searchableAttributeRangeComponents.get(keyName);
            // if where clause is empty then use passed in prefix... otherwise generate one
            String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());
			QueryComponent qc = generateSearchableAttributeRangeSql(keyName, criteriaComponents, whereClausePrefix, tableIndex);
            fromSql.append(qc.getFromSql());
            whereSql.append(qc.getWhereSql());
		}

        QueryComponent qc = new QueryComponent("",fromSql.toString(),whereSql.toString());
        return qc;
    }

    protected QueryComponent generateSearchableAttributeSql(SearchAttributeCriteriaComponent criteriaComponent,String whereSqlStarter,int tableIndex) {
        String tableIdentifier = "EXT" + tableIndex;
        String queryTableColumnName = tableIdentifier + ".DOC_HDR_EXT_VAL";
        QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(criteriaComponent.getSearchableAttributeValue(), tableIdentifier, whereSqlStarter, criteriaComponent.getSavedKey());
        StringBuffer fromSql = new StringBuffer(joinSqlComponent.getFromSql());
        StringBuffer whereSql = new StringBuffer(joinSqlComponent.getWhereSql());

        whereSql.append(generateSearchableAttributeDefaultWhereSql(criteriaComponent, queryTableColumnName));

        return new QueryComponent("",fromSql.toString(),whereSql.toString());
    }

    protected QueryComponent generateSearchableAttributeRangeSql(String searchAttributeKeyName, List criteriaComponents,String whereSqlStarter,int tableIndex) {
        StringBuffer fromSql = new StringBuffer();
        StringBuffer whereSql = new StringBuffer();
    	boolean joinAlreadyPerformed = false;
        String tableIdentifier = "EXT" + tableIndex;
        String queryTableColumnName = tableIdentifier + ".DOC_HDR_EXT_VAL";

        for (Iterator iter = criteriaComponents.iterator(); iter.hasNext();) {
			SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iter.next();
			if (!searchAttributeKeyName.equals(criteriaComponent.getSavedKey())) {
				String errorMsg = "Key value of searchable attribute component with savedKey '" + criteriaComponent.getSavedKey() + "' does not match specified savedKey value '" + searchAttributeKeyName + "'";
				LOG.error("generateSearchableAttributeRangeSql() " + errorMsg);
				throw new RuntimeException(errorMsg);
			}
			if (!joinAlreadyPerformed) {
		        QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(criteriaComponent.getSearchableAttributeValue(), tableIdentifier, whereSqlStarter, searchAttributeKeyName);
		        fromSql.append(joinSqlComponent.getFromSql());
		        whereSql.append(joinSqlComponent.getWhereSql());
		        joinAlreadyPerformed = true;
			}
	        whereSql.append(generateSearchableAttributeDefaultWhereSql(criteriaComponent, queryTableColumnName));
		}

        return new QueryComponent("",fromSql.toString(),whereSql.toString());
    }

    protected StringBuffer generateSearchableAttributeDefaultWhereSql(SearchAttributeCriteriaComponent criteriaComponent,String queryTableColumnName) {
        StringBuffer whereSql = new StringBuffer();
        String initialClauseStarter = "and";
//        whereSql.append(" " + initialClauseStarter + " ");

        boolean valueIsDate = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeDateTimeValue);
        boolean valueIsString = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeStringValue);
        boolean addCaseInsensitivityForValue = (!criteriaComponent.isCaseSensitive()) && criteriaComponent.getSearchableAttributeValue().allowsCaseInsensitivity();
        String attributeValueSearched = criteriaComponent.getValue();
        List<String> attributeValuesSearched = criteriaComponent.getValues();

        StringBuffer whereSqlTemp = new StringBuffer();
        if (valueIsDate) {
        	if (criteriaComponent.isRangeSearch()) {
                // for a range search just add the criteria
	        	whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), criteriaComponent.isComponentLowerBoundValue(), attributeValueSearched));
        	} else {
                if (!Utilities.isEmpty(attributeValuesSearched)) {
                    // for a multivalue date search we need multiple ranges entered
                    whereSqlTemp.append(initialClauseStarter).append(" (");
                    boolean firstValue = true;
                    for (Iterator iter = attributeValuesSearched.iterator(); iter.hasNext();) {
                        String attributeValueEntered = (String) iter.next();
                        whereSqlTemp.append(" ( ");
                        whereSqlTemp.append(constructWhereClauseDateElement("", queryTableColumnName, criteriaComponent.isSearchInclusive(), true, attributeValueEntered));
                        whereSqlTemp.append(constructWhereClauseDateElement("and", queryTableColumnName, criteriaComponent.isSearchInclusive(), false, attributeValueEntered));
                        whereSqlTemp.append(" ) ");
                        String separator = " or ";
                        if (firstValue) {
                            firstValue = false;
                            separator = "";
                        }
                        whereSqlTemp.append(separator);
                    }
                    whereSqlTemp.append(") ");
                } else {
    	        	// below is a search for a single date field.... must do a range of 'time' so we can find any value regardless of the time associated with it
    	        	whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), true, attributeValueSearched));
    	        	whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), false, attributeValueSearched));
                }
        	}
        } else {
            boolean usingWildcards = false;
        	StringBuffer prefix = new StringBuffer("");
        	StringBuffer suffix = new StringBuffer("");
        	if (valueIsString) {
        		prefix.append("'");
        		suffix.insert(0,"'");
        	}
        	if (criteriaComponent.isAllowWildcards() && criteriaComponent.getSearchableAttributeValue().allowsWildcards()) {
                if (!Utilities.isEmpty(attributeValuesSearched)) {
                    List<String> newList = new ArrayList<String>();
                    for (Iterator iter = attributeValuesSearched.iterator(); iter.hasNext();) {
                        String attributeValueEntered = (String) iter.next();
                        newList.add(attributeValueEntered.trim().replace('*', DATABASE_WILDCARD_CHARACTER));
                        usingWildcards |= (attributeValueEntered.indexOf(DATABASE_WILDCARD_CHARACTER_STRING) != -1);
                    }
                    attributeValuesSearched = newList;
                } else {
                    attributeValueSearched = attributeValueSearched.trim().replace('*', DATABASE_WILDCARD_CHARACTER);
                    usingWildcards |= (attributeValueSearched.indexOf(DATABASE_WILDCARD_CHARACTER_STRING) != -1);
                }
                if (criteriaComponent.isAutoWildcardBeginning()) {
                    usingWildcards |= true;
                	if (prefix.length() == 0) {
                    	prefix.append("'" + DATABASE_WILDCARD_CHARACTER_STRING);
                	} else {
                    	prefix.append(DATABASE_WILDCARD_CHARACTER_STRING);
                	}
                }
                if (criteriaComponent.isAutoWildcardEnd()) {
                    usingWildcards |= true;
                	if (suffix.length() == 0) {
                		suffix.insert(0,DATABASE_WILDCARD_CHARACTER_STRING + "'");
                	} else {
                		suffix.insert(0,DATABASE_WILDCARD_CHARACTER_STRING);
                	}
                }
        	}
        	String prefixToUse = prefix.toString();
        	String suffixToUse = suffix.toString();
        	if (addCaseInsensitivityForValue) {
            	queryTableColumnName = "upper(" + queryTableColumnName + ")";
            	prefixToUse = "upper(" + prefix.toString();
            	suffixToUse = suffix.toString() + ")";
        	}
            if (!Utilities.isEmpty(attributeValuesSearched)) {
                // for a multivalue search we need multiple 'or' clause statements entered
                whereSqlTemp.append(initialClauseStarter).append(" (");
                boolean firstValue = true;
                for (Iterator iter = attributeValuesSearched.iterator(); iter.hasNext();) {
                    String attributeValueEntered = (String) iter.next();
                    String separator = " or ";
                    if (firstValue) {
                        firstValue = false;
                        separator = "";
                    }
                    String sqlOperand = getSqlOperand(criteriaComponent.isRangeSearch(), criteriaComponent.isSearchInclusive(), (criteriaComponent.isRangeSearch() && criteriaComponent.isComponentLowerBoundValue()), usingWildcards);
                    whereSqlTemp.append(constructWhereClauseElement(separator, queryTableColumnName, sqlOperand, attributeValueEntered, prefixToUse, suffixToUse));
                }
                whereSqlTemp.append(") ");
            } else {
                String sqlOperand = getSqlOperand(criteriaComponent.isRangeSearch(), criteriaComponent.isSearchInclusive(), (criteriaComponent.isRangeSearch() && criteriaComponent.isComponentLowerBoundValue()), usingWildcards);
                whereSqlTemp.append(constructWhereClauseElement(initialClauseStarter, queryTableColumnName, sqlOperand, attributeValueSearched, prefixToUse, suffixToUse));
            }
        }
        whereSqlTemp.append(" ");
        return whereSql.append(whereSqlTemp);
    }

    private QueryComponent getSearchableAttributeJoinSql(SearchableAttributeValue attributeValue,String tableIdentifier,String whereSqlStarter,String attributeTableKeyColumnName) {
    	return new QueryComponent("",generateSearchableAttributeFromSql(attributeValue, tableIdentifier).toString(),generateSearchableAttributeWhereClauseJoin(whereSqlStarter, tableIdentifier, attributeTableKeyColumnName).toString());
    }

    private StringBuffer generateSearchableAttributeWhereClauseJoin(String whereSqlStarter,String tableIdentifier,String attributeTableKeyColumnName) {
    	StringBuffer whereSql = new StringBuffer(constructWhereClauseElement(whereSqlStarter, "DOC_HDR.DOC_HDR_ID", "=", tableIdentifier + ".DOC_HDR_ID", null, null));
    	whereSql.append(constructWhereClauseElement(" and ", tableIdentifier + ".DOC_HDR_EXT_VAL_KEY", "=", attributeTableKeyColumnName, "'", "'"));
        return whereSql;
    }

    private StringBuffer generateSearchableAttributeFromSql(SearchableAttributeValue attributeValue,String tableIdentifier) {
        StringBuffer fromSql = new StringBuffer();
        String tableName = attributeValue.getAttributeTableName();
        if (StringUtils.isBlank(tableName)) {
        	String errorMsg = "The table name associated with Searchable Attribute with class '" + attributeValue.getClass() + "' returns as '" + tableName + "'";
        	LOG.error("getSearchableAttributeSql() " + errorMsg);
        	throw new RuntimeException(errorMsg);
        }
        fromSql.append(" ," + tableName + " " + tableIdentifier + " ");
        return fromSql;
    }

    private StringBuffer constructWhereClauseDateElement(String clauseStarter,String queryTableColumnName,boolean inclusive,boolean valueIsLowerBound,String dateValueToSearch) {
    	StringBuffer sqlOperand = new StringBuffer(getSqlOperand(true, inclusive, valueIsLowerBound, false));
    	String timeValueToSearch = null;
    	if (valueIsLowerBound) {
    		timeValueToSearch = "00:00:00";
    	} else {
    		timeValueToSearch = "23:59:59";
    	}
    	return new StringBuffer().append(constructWhereClauseElement(clauseStarter, queryTableColumnName, sqlOperand.toString(), DocSearchUtils.getDateSQL(DocSearchUtils.getSqlFormattedDate(dateValueToSearch.trim()), timeValueToSearch.trim()), "", ""));
    }

    private StringBuffer constructWhereClauseElement(String clauseStarter,String queryTableColumnName,String operand,String valueToSearch,String valuePrefix,String valueSuffix) {
    	StringBuffer whereSql = new StringBuffer();
    	valuePrefix = (valuePrefix != null) ? valuePrefix : "";
    	valueSuffix = (valueSuffix != null) ? valueSuffix : "";
    	whereSql.append(" " + clauseStarter + " ").append(queryTableColumnName).append(" " + operand + " ").append(valuePrefix).append(valueToSearch).append(valueSuffix).append(" ");
    	return whereSql;
    }

	/**
	 * For the following we first check for a ranged search because a ranged search
	 * does not allow for wildcards
	 */
    private String getSqlOperand(boolean rangeSearch, boolean inclusive, boolean valueIsLowerBound, boolean usingWildcards) {
    	StringBuffer sqlOperand = new StringBuffer("=");
    	if (rangeSearch) {
    		if (valueIsLowerBound) {
    			sqlOperand = new StringBuffer(">");
    		} else {
    			sqlOperand = new StringBuffer("<");
    		}
    		if (inclusive) {
    			sqlOperand.append("=");
    		}

    	} else if (usingWildcards) {
            sqlOperand = new StringBuffer("like");
    	}
    	return sqlOperand.toString();
    }

    /**
     * @deprecated Removed as of version 0.9.3.  Use {@link #processResultSet(Statement, ResultSet, DocSearchCriteriaVO, WorkflowUser)} instead.
     */
    public List<DocSearchVO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaVO searchCriteria) throws EdenUserNotFoundException, SQLException {
        return processResultSet(searchAttributeStatement, resultSet, searchCriteria, null);
    }
    
    /**
     * @param resultSet
     * @param criteria
     * @return
     * @throws EdenUserNotFoundException
     * @throws SQLException
     */
    public List<DocSearchVO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaVO searchCriteria, WorkflowUser user) throws EdenUserNotFoundException, SQLException {
    	setCriteria(searchCriteria);
        int size = 0;
        List docList = new ArrayList();
        Map resultMap = new HashMap();
        PerformanceLogger perfLog = new PerformanceLogger();
        int iteration = 0;
        boolean resultSetHasNext = resultSet.next();
        while ( resultSetHasNext &&
                ( (searchCriteria.getThreshold() == null) || (resultMap.size() < searchCriteria.getThreshold().intValue()) ) && 
                ( (searchCriteria.getFetchLimit() == null) || (iteration < searchCriteria.getFetchLimit().intValue()) ) ) {
        	iteration++;
            DocSearchVO docSearchVO = processRow(searchAttributeStatement, resultSet);
            docSearchVO.setSuperUserSearch(getCriteria().getSuperUserSearch());
            if (!resultMap.containsKey(docSearchVO.getRouteHeaderId())) {
                docList.add(docSearchVO);
                resultMap.put(docSearchVO.getRouteHeaderId(), docSearchVO);
                size++;
            } else {
                // handle duplicate rows with different search data
                DocSearchVO previousEntry = (DocSearchVO)resultMap.get(docSearchVO.getRouteHeaderId());
                handleMultipleDocumentRows(previousEntry, docSearchVO);
            }
            resultSetHasNext = resultSet.next();
        }
        perfLog.log("Time to read doc search results.", true);
        // if we have threshold+1 results, then we have more results than we are going to display
        criteria.setOverThreshold(resultSetHasNext);

        UserSession userSession = UserSession.getAuthenticatedUser();
        if ( (userSession == null) && (user != null) ) {
            LOG.info("Authenticated User Session is null... using parameter user: " + user);
            userSession = new UserSession(user);
        } else if (searchCriteria.isOverridingUserSession()) {
            if (user == null) {
                LOG.error("Search Criteria specified UserSession override but given user paramter is null");
                throw new WorkflowRuntimeException("Search criteria specified UserSession override but given user is null.");
            }
            LOG.info("Search Criteria specified UserSession override.  Using user: " + user);
            userSession = new UserSession(user);
        }
        if (userSession != null) {
        	// TODO do we really want to allow the document search if there is no User Session?
        	// This is mainly to allow for the unit tests to run but I wonder if we need to push
        	// the concept of the "executing user" into the doc search api in some way...
        	perfLog = new PerformanceLogger();
        	SecuritySession securitySession = new SecuritySession(userSession);
        	for (Iterator iterator = docList.iterator(); iterator.hasNext();) {
        		DocSearchVO docSearchVO = (DocSearchVO) iterator.next();
        		if (!KEWServiceLocator.getDocumentSecurityService().docSearchAuthorized(userSession, docSearchVO, securitySession)) {
        			iterator.remove();
        			criteria.setSecurityFilteredRows(criteria.getSecurityFilteredRows() + 1);
        		}
        	}
        	perfLog.log("Time to filter document search results for security.", true);
        }

        LOG.debug("Processed "+size+" document search result rows.");
        return docList;
    }

    /**
     * Handles multiple document rows by collapsing them and their data into the searchable attribute columns.
     *
     * TODO this is currently concatenating strings together with HTML elements, this seems bad in this location,
     * perhaps we should move this to the web layer (and perhaps enhance the searchable attributes
     * portion of the DocSearchVO data structure?)
     */
    private void handleMultipleDocumentRows(DocSearchVO existingRow, DocSearchVO newRow) {

    	for (Iterator iterator = newRow.getSearchableAttributes().iterator(); iterator.hasNext();) {
    		KeyValueSort newData = (KeyValueSort) iterator.next();
    		String newRowValue = newData.getValue();
            boolean foundMatch = false;
            for (Iterator dataIt = existingRow.getSearchableAttributes().iterator(); dataIt.hasNext();) {
            	KeyValueSort existingData = (KeyValueSort) dataIt.next();
                if (existingData.getKey().equals(newData.getKey())) {
                	String existingRowValue = existingData.getValue();
                	if (!Utilities.isEmpty(newRowValue)) {
                		String valueToSet = "";
                		if (Utilities.isEmpty(existingRowValue)) {
                			valueToSet = newRowValue;
                		} else {
                			valueToSet = existingRowValue + "<br>" + newRowValue;
                		}
                		existingData.setvalue(valueToSet);
                		if ( (existingData.getSortValue() == null) && (newData.getSortValue() != null) ) {
                    		existingData.setSortValue(newData.getSortValue());
                		}
                	}
                	foundMatch = true;
                }
            }
            if (!foundMatch) {
            	existingRow.addSearchableAttribute(new KeyValueSort(newData));
            }
    	}
    }

    public DocSearchVO processRow(Statement searchAttributeStatement, ResultSet rs) throws SQLException, EdenUserNotFoundException {
        DocSearchVO docSearchVO = new DocSearchVO();

        docSearchVO.setRouteHeaderId(new Long(rs.getLong("DOC_HDR_ID")));

        String docTypeLabel = rs.getString("DOC_TYP_LBL_TXT");
        String activeIndicatorCode = rs.getString("DOC_TYP_ACTV_IND");

        docSearchVO.setDocRouteStatusCode(rs.getString("DOC_RTE_STAT_CD"));
        docSearchVO.setDateCreated(rs.getTimestamp("DOC_CRTE_DT"));
        docSearchVO.setDocumentTitle(rs.getString("DOC_TTL"));
        docSearchVO.setDocTypeName(rs.getString("DOC_TYP_NM"));
        docSearchVO.setDocTypeLabel(docTypeLabel);

        if ((activeIndicatorCode == null) || (activeIndicatorCode.trim().length() == 0)) {
            docSearchVO.setActiveIndicatorCode(KEWConstants.ACTIVE_CD);
        } else {
            docSearchVO.setActiveIndicatorCode(activeIndicatorCode);
        }

        if ((docTypeLabel == null) || (docTypeLabel.trim().length() == 0)) {
            docSearchVO.setDocTypeHandlerUrl("");
        } else {
            docSearchVO.setDocTypeHandlerUrl(rs.getString("DOC_TYP_HDLR_URL_ADDR"));
        }

        docSearchVO.setInitiatorWorkflowId(rs.getString("DOC_INITR_PRSN_EN_ID"));

        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(docSearchVO.getInitiatorWorkflowId()));

        docSearchVO.setInitiatorNetworkId(user.getAuthenticationUserId().getAuthenticationId());
        docSearchVO.setInitiatorName(user.getDisplayName());
        docSearchVO.setInitiatorFirstName(user.getGivenName());
        docSearchVO.setInitiatorLastName(user.getLastName());
        docSearchVO.setInitiatorTransposedName(UserUtils.getTransposedName(UserSession.getAuthenticatedUser(), user));
        docSearchVO.setInitiatorEmailAddress(user.getEmailAddress());

        if (usingAtLeastOneSearchAttribute) {
            populateRowSearchableAttributes(docSearchVO,searchAttributeStatement);
        }
        return docSearchVO;
    }

    /**
     * This method performs searches against the search attribute value tables (see classes implementing
     * {@link SearchableAttributeValue}) to get data to fill in search attribute values on the given docSearchVO parameter
     * 
     * @param docSearchVO - document search result object getting search attributes added to it
     * @param searchAttributeStatement - statement being used to call the database for queries
     * @throws SQLException
     */
    public void populateRowSearchableAttributes(DocSearchVO docSearchVO, Statement searchAttributeStatement) throws SQLException {
        searchAttributeStatement.setFetchSize(50);
        Long documentId = docSearchVO.getRouteHeaderId();
        List<SearchableAttributeValue> attributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
        PerformanceLogger perfLog = new PerformanceLogger(documentId);
        for (Iterator iter = attributeValues.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            String attributeSql = "select DOC_HDR_EXT_VAL_KEY, DOC_HDR_EXT_VAL from " + searchAttValue.getAttributeTableName() + " where DOC_HDR_ID = " + documentId;
            ResultSet attributeResultSet = null;
            try {
                attributeResultSet = searchAttributeStatement.executeQuery(attributeSql);
                while (attributeResultSet.next()) {
                    searchAttValue.setSearchableAttributeKey(attributeResultSet.getString("DOC_HDR_EXT_VAL_KEY"));
                    searchAttValue.setupAttributeValue(attributeResultSet, "DOC_HDR_EXT_VAL");
                    if ( (!Utilities.isEmpty(searchAttValue.getSearchableAttributeKey())) && (searchAttValue.getSearchableAttributeValue() != null) ) {
                        docSearchVO.addSearchableAttribute(new KeyValueSort(searchAttValue.getSearchableAttributeKey(),searchAttValue.getSearchableAttributeDisplayValue(),searchAttValue.getSearchableAttributeValue(),searchAttValue));
                    }
                }
            } finally {
                if (attributeResultSet != null) {
                    try {
                        attributeResultSet.close();
                    } catch (Exception e) {
                        LOG.warn("Could not close searchable attribute result set for class " + searchAttValue.getClass().getName(),e);
                    }
                }
            }
        }
        perfLog.log("Time to execute doc search search attribute queries.", true);
    }
    
    /**
     * @deprecated As of version 0.9.3 this method is no longer used. Method
     *             {@link #populateRowSearchableAttributes(DocSearchVO, Statement)} is being used instead.
     */
    @Deprecated
    public void populateRowSearchableAttributes(DocSearchVO docSearchVO, Statement searchAttributeStatement, ResultSet rs) throws SQLException {
        List searchAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
        for (Iterator iter = searchAttributeValues.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            String prefixName = searchAttValue.getAttributeDataType().toUpperCase();
            searchAttValue.setSearchableAttributeKey(rs.getString(prefixName + "_KEY"));
            searchAttValue.setupAttributeValue(rs, prefixName + "_VALUE");
            if ( (!Utilities.isEmpty(searchAttValue.getSearchableAttributeKey())) && (searchAttValue.getSearchableAttributeValue() != null) ) {
        	docSearchVO.addSearchableAttribute(new KeyValueSort(searchAttValue.getSearchableAttributeKey(),searchAttValue.getSearchableAttributeDisplayValue(),searchAttValue.getSearchableAttributeValue(),searchAttValue));
            }
        }
    }

    protected String getDocSearchSQL() throws EdenUserNotFoundException {
    	String sqlPrefix = "Select * from (";
    	String sqlSuffix = ") FINAL_SEARCH order by FINAL_SEARCH.DOC_HDR_ID desc";
    	boolean possibleSearchableAttributesExist = false;
        // the DISTINCT here is important as it filters out duplicate rows which could occur as the result of doc search extension values...
        StringBuffer selectSQL = new StringBuffer("select DISTINCT(DOC_HDR.DOC_HDR_ID), DOC_HDR.DOC_INITR_PRSN_EN_ID, DOC_HDR.DOC_RTE_STAT_CD, DOC_HDR.DOC_CRTE_DT, DOC_HDR.DOC_TTL, DOC1.DOC_TYP_NM, DOC1.DOC_TYP_LBL_TXT, DOC1.DOC_TYP_HDLR_URL_ADDR, DOC1.DOC_TYP_ACTV_IND");
        StringBuffer fromSQL = new StringBuffer(" from EN_DOC_TYP_T DOC1 ");
        String docHeaderTableAlias = "DOC_HDR";
        StringBuffer fromSQLForDocHeaderTable = new StringBuffer(", EN_DOC_HDR_T " + docHeaderTableAlias + " ");
        StringBuffer whereSQL = new StringBuffer();
        whereSQL.append(getRouteHeaderIdSql(criteria.getRouteHeaderId(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getInitiatorSql(criteria.getInitiator(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getAppDocIdSql(criteria.getAppDocId(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateCreatedSql(criteria.getFromDateCreated(), criteria.getToDateCreated(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateLastModifiedSql(criteria.getFromDateLastModified(), criteria.getToDateLastModified(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateApprovedSql(criteria.getFromDateApproved(), criteria.getToDateApproved(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateFinalizedSql(criteria.getFromDateFinalized(), criteria.getToDateFinalized(), getGeneratedPredicatePrefix(whereSQL.length())));
        // flags for the table being added to the FROM class of the sql
        boolean actionTakenTable = false;
        if ((!"".equals(getViewerSql(criteria.getViewer(), getGeneratedPredicatePrefix(whereSQL.length())))) || (!"".equals(getWorkgroupViewerSql(criteria.getWorkgroupViewerName(), getGeneratedPredicatePrefix(whereSQL.length()))))) {
            whereSQL.append(getViewerSql(criteria.getViewer(), getGeneratedPredicatePrefix(whereSQL.length())));
            whereSQL.append(getWorkgroupViewerSql(criteria.getWorkgroupViewerName(), getGeneratedPredicatePrefix(whereSQL.length())));
            fromSQL.append(", EN_ACTN_RQST_T ");
            actionTakenTable = true;
        }

        if (!("".equals(getApproverSql(criteria.getApprover(), getGeneratedPredicatePrefix(whereSQL.length()))))) {
            whereSQL.append(getApproverSql(criteria.getApprover(), getGeneratedPredicatePrefix(whereSQL.length())));
            if (!actionTakenTable) {
                fromSQL.append(", EN_ACTN_TKN_T ");
            }
            actionTakenTable = true;
        }

        String docTypeFullNameSql = getDocTypeFullNameWhereSql(criteria.getDocTypeFullName(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (!("".equals(docTypeFullNameSql))) {
            possibleSearchableAttributesExist |= true;
            whereSQL.append(docTypeFullNameSql);
        }

        String docRouteNodeSql = getDocRouteNodeSql(criteria.getDocTypeFullName(), criteria.getDocRouteNodeId(), criteria.getDocRouteNodeLogic(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (!"".equals(docRouteNodeSql)) {
            whereSQL.append(docRouteNodeSql);
            fromSQL.append(", EN_RTE_NODE_INSTN_T ");
            fromSQL.append(", EN_RTE_NODE_T ");
        }

        filterOutNonQueryAttributes();
        if ((criteria.getSearchableAttributes() != null) && (criteria.getSearchableAttributes().size() > 0)) {
            possibleSearchableAttributesExist |= true;
            QueryComponent queryComponent = getSearchableAttributeSql(criteria.getSearchableAttributes(), getGeneratedPredicatePrefix(whereSQL.length()));
            selectSQL.append(queryComponent.getSelectSql());
            fromSQL.append(queryComponent.getFromSql());
            whereSQL.append(queryComponent.getWhereSql());
        }

        // at this point we haven't appended doc title to the query, if the document title is the only field
        // which was entered, we want to set the "from" date to be X days ago.  This will allow for a
        // more efficient query
        Integer defaultCreateDateDaysAgoValue = null;
//        whereSQL.append(getDocTitleSql(criteria.getDocTitle(), getGeneratedPredicatePrefix(whereSQL.length())));
        String tempWhereSql = getDocTitleSql(criteria.getDocTitle(), getGeneratedPredicatePrefix(whereSQL.length()));
        if ( ((whereSQL == null) || (StringUtils.isBlank(whereSQL.toString()))) && (StringUtils.isNotBlank(tempWhereSql)) ) {
        	// doc title is not blank
        	defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_DOC_TITLE_CREATE_DATE_DAYS_AGO;
        }
        whereSQL.append(tempWhereSql);
        if ( ((whereSQL == null) || (StringUtils.isBlank(whereSQL.toString()))) && (StringUtils.isBlank(criteria.getDocRouteStatus())) ) {
            // if they haven't set any criteria, default the from created date to today minus days from constant variable
        	defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO;
        }
        if (defaultCreateDateDaysAgoValue != null) {
        	// add a default create date
        	Calendar calendar = Calendar.getInstance();
        	calendar.add(Calendar.DATE, defaultCreateDateDaysAgoValue.intValue());
        	criteria.setFromDateCreated(RiceConstants.getDefaultDateFormat().format(calendar.getTime()));
            whereSQL.append(getDateCreatedSql(criteria.getFromDateCreated(), criteria.getToDateCreated(), getGeneratedPredicatePrefix(whereSQL.length())));
        }
        whereSQL.append(getDocRouteStatusSql(criteria.getDocRouteStatus(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getGeneratedPredicatePrefix(whereSQL.length())).append(" DOC_HDR.DOC_TYP_ID = DOC1.DOC_TYP_ID ");

        fromSQL.append(fromSQLForDocHeaderTable);
        String finalizedSql = sqlPrefix + " " + selectSQL.toString() + " " + fromSQL.toString() + " " + whereSQL.toString() + " " + sqlSuffix;
        usingAtLeastOneSearchAttribute = possibleSearchableAttributesExist;
//        usingAtLeastOneSearchAttribute = false;
//        if (possibleSearchableAttributesExist) {
//            usingAtLeastOneSearchAttribute = true;
//            finalizedSql = generateFinalSQL(new QueryComponent(selectSQL.toString(),fromSQL.toString(),whereSQL.toString()), docHeaderTableAlias, sqlPrefix, sqlSuffix);
//        }
        LOG.info("*********** SEARCH SQL ***************");
        LOG.info(finalizedSql);
        LOG.info("**************************************");
        return finalizedSql;
    }

    /**
     * @deprecated As of version 0.9.3 this method is no longer used. This method had been used to create multiple SQL queries if using searchable attributes
     *             and use the sql UNION function to join the queries. The replacement method
     *             {@link #generateFinalSQL(QueryComponent, String, String)} is now used instead.
     */
    @Deprecated
    protected String generateFinalSQL(QueryComponent searchSQL,String docHeaderTableAlias, String standardSqlPrefix, String standardSqlSuffix) {
    	StringBuffer finalSql = new StringBuffer();
    	List searchableAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
    	List tableAliasComponentNames = new ArrayList();
    	for (Iterator iterator = searchableAttributeValues.iterator(); iterator.hasNext();) {
			SearchableAttributeValue attValue = (SearchableAttributeValue) iterator.next();
			tableAliasComponentNames.add(attValue.getAttributeDataType().toUpperCase());
		}
    	for (Iterator iter = searchableAttributeValues.iterator(); iter.hasNext();) {
			SearchableAttributeValue attributeValue = (SearchableAttributeValue) iter.next();
			QueryComponent qc = generateSqlForSearchableAttributeValue(attributeValue, tableAliasComponentNames, docHeaderTableAlias);
			StringBuffer currentSql = new StringBuffer();
			currentSql.append(searchSQL.getSelectSql() + qc.getSelectSql() + searchSQL.getFromSql() + qc.getFromSql() + searchSQL.getWhereSql() + qc.getWhereSql());
			if (finalSql.length() == 0) {
				finalSql.append(standardSqlPrefix).append(" ( ").append(currentSql);
			} else {
				finalSql.append(" ) UNION ( " + currentSql.toString());
			}
		}
    	finalSql.append(" ) " + standardSqlSuffix);
    	return finalSql.toString();
    }
    
    /**
     * @deprecated As of version 0.9.3 this method is no longer used. This method had been used to generate SQL to return searchable attributes using left
     *             outer joins. The new mechanism to get search attributes from the database is to call each search attribute
     *             table individually in the {@link #populateRowSearchableAttributes(DocSearchVO, Statement, ResultSet)}
     *             method.
     */
    @Deprecated
    protected QueryComponent generateSqlForSearchableAttributeValue(SearchableAttributeValue attributeValue, List tableAliasComponentNames, String docHeaderTableAlias) {
    	StringBuffer selectSql = new StringBuffer();
    	StringBuffer fromSql = new StringBuffer();
    	String currentAttributeTableAlias = "SA_" + attributeValue.getAttributeDataType().toUpperCase();
    	fromSql.append(" LEFT OUTER JOIN " + attributeValue.getAttributeTableName() + " " + currentAttributeTableAlias + " ON (" + docHeaderTableAlias + ".DOC_HDR_ID = " + currentAttributeTableAlias + ".DOC_HDR_ID)");
    	for (Iterator iter = tableAliasComponentNames.iterator(); iter.hasNext();) {
			String aliasComponentName = (String) iter.next();
			if (aliasComponentName.equalsIgnoreCase(attributeValue.getAttributeDataType())) {
				selectSql.append(", " + currentAttributeTableAlias + ".DOC_HDR_EXT_VAL_KEY as " + aliasComponentName + "_KEY, " + currentAttributeTableAlias + ".DOC_HDR_EXT_VAL as " + aliasComponentName + "_VALUE");
			} else {
				selectSql.append(", NULL as " + aliasComponentName + "_KEY, NULL as " + aliasComponentName + "_VALUE");
			}
		}
    	return new QueryComponent(selectSql.toString(),fromSql.toString(),"");
    }

    protected String getRouteHeaderIdSql(String routeHeaderId, String whereClausePredicatePrefix) {
        if ((routeHeaderId == null) || "".equals(routeHeaderId.trim())) {
            return "";
        } else {
            Long rh = new Long(routeHeaderId.trim());
            return new StringBuffer(whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = ").append(rh.intValue()).toString();
        }
    }

    protected String getInitiatorSql(String initiator, String whereClausePredicatePrefix) throws EdenUserNotFoundException {
        if ((initiator == null) || "".equals(initiator.trim())) {
            return "";
        } else {
            String userWorkflowId = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(initiator.trim())).getWorkflowUserId().getWorkflowId();
            return new StringBuffer(whereClausePredicatePrefix + " DOC_HDR.DOC_INITR_PRSN_EN_ID = '").append(userWorkflowId).append("'").toString();
        }
    }

    protected String getDocTitleSql(String docTitle, String whereClausePredicatePrefix) {
        if (StringUtils.isBlank(docTitle)) {
            return "";
        } else {
            if (!docTitle.trim().endsWith("*")) {
                docTitle = docTitle.trim().concat("*").replace('*', '%');
            } else {
                docTitle = docTitle.trim().replace('*', '%');
            }
            // quick and dirty ' replacement that isn't the best but should work for all dbs
            docTitle = docTitle.trim().replace('\'', '%');
            return new StringBuffer(whereClausePredicatePrefix + " upper(DOC_HDR.DOC_TTL) like '%").append(docTitle.toUpperCase()).append("'").toString();
        }
    }

    // special methods that return the sql needed to complete the search
    // or nothing if the field was not filled in
    protected String getAppDocIdSql(String appDocId, String whereClausePredicatePrefix) {
        if ((appDocId == null) || "".equals(appDocId.trim())) {
            return "";
        } else {
            if (!appDocId.trim().endsWith("*")) {
                appDocId = appDocId.trim().concat("*").replace('*', '%');
            } else {
                appDocId = appDocId.trim().replace('*', '%');
            }
            return new StringBuffer(whereClausePredicatePrefix + " upper(DOC_HDR.DOC_APPL_DOC_ID) like '%").append(appDocId.toUpperCase()).append("'").toString();
        }
    }

    protected String getDateCreatedSql(String fromDateCreated, String toDateCreated, String whereClausePredicatePrefix) {
        return establishDateString(fromDateCreated, toDateCreated, CREATE_DATE_FIELD_STRING, whereClausePredicatePrefix);
    }

    protected String getDateApprovedSql(String fromDateApproved, String toDateApproved, String whereClausePredicatePrefix) {
        return establishDateString(fromDateApproved, toDateApproved, APPROVE_DATE_FIELD_STRING, whereClausePredicatePrefix);
    }

    protected String getDateFinalizedSql(String fromDateFinalized, String toDateFinalized, String whereClausePredicatePrefix) {
        return establishDateString(fromDateFinalized, toDateFinalized, FINALIZATION_DATE_FIELD_STRING, whereClausePredicatePrefix);
    }

    protected String getDateLastModifiedSql(String fromDateLastModified, String toDateLastModified, String whereClausePredicatePrefix) {
        return establishDateString(fromDateLastModified, toDateLastModified, LAST_STATUS_UPDATE_DATE, whereClausePredicatePrefix);
    }

    protected String getViewerSql(String viewer, String whereClausePredicatePrefix) throws EdenUserNotFoundException {
    	String returnSql = "";
        if ((viewer != null) && (!"".equals(viewer.trim()))) {
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(viewer.trim()));
            String userWorkflowId = user.getWorkflowId();
            returnSql = whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = EN_ACTN_RQST_T.DOC_HDR_ID and EN_ACTN_RQST_T.ACTN_RQST_PRSN_EN_ID = '" + userWorkflowId + "'";
        }
        return returnSql;
    }

    protected String getWorkgroupViewerSql(String workgroupName, String whereClausePredicatePrefix) {
        String sql = "";
        if (!Utilities.isEmpty(workgroupName)) {
            Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(workgroupName));
        	sql = whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = EN_ACTN_RQST_T.DOC_HDR_ID and EN_ACTN_RQST_T.WRKGRP_ID = " + workgroup.getWorkflowGroupId().getGroupId();
        }
        return sql;
    }

    protected String getApproverSql(String approver, String whereClausePredicatePrefix) throws EdenUserNotFoundException {
    	String returnSql = "";
        if ((approver != null) && (!"".equals(approver.trim()))) {
            String userWorkflowId = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(approver.trim())).getWorkflowUserId().getWorkflowId();
            returnSql = whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = EN_ACTN_TKN_T.DOC_HDR_ID and upper(EN_ACTN_TKN_T.ACTN_TKN_CD) = '" + KEWConstants.ACTION_TAKEN_APPROVED_CD + "' and EN_ACTN_TKN_T.ACTN_TKN_PRSN_EN_ID = '" + userWorkflowId + "'";
        }
        return returnSql;
    }

    protected String getDocTypeFullNameWhereSql(String docTypeFullName, String whereClausePredicatePrefix) {
    	StringBuffer returnSql = new StringBuffer("");
        if ((docTypeFullName != null) && (!"".equals(docTypeFullName.trim()))) {
            DocumentTypeService docSrv = (DocumentTypeService) KEWServiceLocator.getDocumentTypeService();
            DocumentType docType = docSrv.findByName(docTypeFullName.trim());
            returnSql.append(getDocTypeFullNameWhereSql(docType,whereClausePredicatePrefix));
        }
        return returnSql.toString();
    }

    protected String getDocTypeFullNameWhereSql(DocumentType docType, String whereClausePredicatePrefix) {
    	StringBuffer returnSql = new StringBuffer("");
        if (docType != null) {
        	returnSql.append(whereClausePredicatePrefix).append("(");
        	addDocumentTypeNameToSearchOn(returnSql,docType.getName(), "");
            if (docType.getChildrenDocTypes() != null) {
                addChildDocumentTypes(returnSql, docType.getChildrenDocTypes());
            }
            addExtraDocumentTypesToSearch(returnSql,docType);
            returnSql.append(")");
        }
        return returnSql.toString();
    }

    private void addChildDocumentTypes(StringBuffer whereSql, Collection childDocumentTypes) {
        for (Iterator iter = childDocumentTypes.iterator(); iter.hasNext();) {
            DocumentType child = (DocumentType) iter.next();
            addDocumentTypeNameToSearchOn(whereSql, child.getName());
            addChildDocumentTypes(whereSql, child.getChildrenDocTypes());
        }
    }

    protected void addExtraDocumentTypesToSearch(StringBuffer whereSql,DocumentType docType) {}

    protected void addDocumentTypeNameToSearchOn(StringBuffer whereSql,String documentTypeName) {
    	this.addDocumentTypeNameToSearchOn(whereSql, documentTypeName, " or ");
    }

    private void addDocumentTypeNameToSearchOn(StringBuffer whereSql,String documentTypeName, String clause) {
    	whereSql.append(clause).append(" DOC1.DOC_TYP_NM = '" + documentTypeName + "'");
    }

    protected String getDocRouteNodeSql(String documentTypeFullName, String docRouteLevel, String docRouteLevelLogic, String whereClausePredicatePrefix) {
        // -1 is the default 'blank' choice from the route node drop down a number is used because the ojb RouteNode object is used to
        // render the node choices on the form.
    	String returnSql = "";
        if ((docRouteLevel != null) && (!"".equals(docRouteLevel.trim())) && (!docRouteLevel.equals("-1"))) {
        	StringBuffer routeNodeCriteria = new StringBuffer("and EN_RTE_NODE_T.RTE_NODE_NM ");
        	if (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_EXACT.equalsIgnoreCase(docRouteLevelLogic.trim())) {
        		routeNodeCriteria.append("= '" + docRouteLevel + "' ");
        	} else {
        		routeNodeCriteria.append("in (");
        		// below buffer used to facilitate the addition of the string ", " to separate out route node names
        		StringBuffer routeNodeInCriteria = new StringBuffer();
        		boolean foundSpecifiedNode = false;
        		List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getValidDocumentType(documentTypeFullName), true);
    			for (Iterator iter = routeNodes.iterator(); iter.hasNext();) {
                    RouteNode routeNode = (RouteNode) iter.next();
                    if (docRouteLevel.equals(routeNode.getRouteNodeName())) {
                    	// current node is specified node so we ignore it outside of the boolean below
                    	foundSpecifiedNode = true;
                    	continue;
                    }
                    // below logic should be to add the current node to the criteria if we haven't found the specified node
					// and the logic qualifier is 'route nodes before specified'... or we have found the specified node and
					// the logic qualifier is 'route nodes after specified'
                    if ( (!foundSpecifiedNode && (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_BEFORE.equalsIgnoreCase(docRouteLevelLogic.trim()))) || 
                         (foundSpecifiedNode && (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_AFTER.equalsIgnoreCase(docRouteLevelLogic.trim()))) ) {
                    	if (routeNodeInCriteria.length() > 0) {
                    		routeNodeInCriteria.append(", ");
                    	}
                    	routeNodeInCriteria.append("'" + routeNode.getRouteNodeName() + "'");
                    }
    			}
    			if (routeNodeInCriteria.length() > 0) {
            		routeNodeCriteria.append(routeNodeInCriteria);
    			} else {
    				routeNodeCriteria.append("''");
    			}
        		routeNodeCriteria.append(") ");
        	}
            returnSql = whereClausePredicatePrefix + "DOC_HDR.DOC_HDR_ID = EN_RTE_NODE_INSTN_T.DOC_ID and EN_RTE_NODE_INSTN_T.RTE_NODE_ID = EN_RTE_NODE_T.RTE_NODE_ID and EN_RTE_NODE_INSTN_T.ACTV_IND = 1 " + routeNodeCriteria.toString() + " ";
        }
        return returnSql;
    }

    protected String getDocRouteStatusSql(String docRouteStatus, String whereClausePredicatePrefix) {
        if ((docRouteStatus == null) || "".equals(docRouteStatus.trim())) {
            return whereClausePredicatePrefix + "DOC_HDR.DOC_RTE_STAT_CD != '" + KEWConstants.ROUTE_HEADER_INITIATED_CD + "'";
        } else {
            return whereClausePredicatePrefix + " DOC_HDR.DOC_RTE_STAT_CD = '" + docRouteStatus.trim() + "'";
        }
    }


    // ---- utility methods

    /**
     * TODO we should probably clean this up some, but we are going to exclude those KeyLabelPairs
     * that have a null label.  This will happen in the case of Quickfinders which don't really
     * represent criteria anyway.  Note however, that it is legal for the label to be the empty string.
     * At some point we will probably need to do some more work to untangle this mess
     */
    private void filterOutNonQueryAttributes() {
        List newAttributes = new ArrayList();
        for (Iterator iterator = criteria.getSearchableAttributes().iterator(); iterator.hasNext();) {
            SearchAttributeCriteriaComponent component = (SearchAttributeCriteriaComponent) iterator.next();
            if (component != null) {
                if ( (StringUtils.isNotBlank(component.getValue())) || (!Utilities.isEmpty(component.getValues())) ) {
                    newAttributes.add(component);
                }
            }
        }
        criteria.setSearchableAttributes(newAttributes);
    }

    private String getGeneratedPredicatePrefix(int whereClauseSize) {
    	return (whereClauseSize > 0) ? " and " : " where ";
    }

    protected String establishDateString(String fromDate, String toDate, String columnDbName, String whereStatementClause) {
    	StringBuffer dateSqlString = new StringBuffer(whereStatementClause).append(" " + columnDbName + " ");
        if (fromDate != null && DocSearchUtils.getSqlFormattedDate(fromDate) != null && toDate != null && DocSearchUtils.getSqlFormattedDate(toDate) != null) {
            return dateSqlString.append(" >= " + DocSearchUtils.getDateSQL(DocSearchUtils.getSqlFormattedDate(fromDate.trim()), null) + " and " + columnDbName + " <= " + DocSearchUtils.getDateSQL(DocSearchUtils.getSqlFormattedDate(toDate.trim()), "23:59:59")).toString();
        } else {
            if (fromDate != null && DocSearchUtils.getSqlFormattedDate(fromDate) != null) {
                return dateSqlString.append(" >= " + DocSearchUtils.getDateSQL(DocSearchUtils.getSqlFormattedDate(fromDate.trim()), null)).toString();
            } else if (toDate != null && DocSearchUtils.getSqlFormattedDate(toDate) != null) {
                return dateSqlString.append(" <= " + DocSearchUtils.getDateSQL(DocSearchUtils.getSqlFormattedDate(toDate.trim()), "23:59:59")).toString();
            } else {
                return "";
            }
        }
    }
    
    public int getDocumentSearchResultSetLimit() {
        return DEFAULT_SEARCH_RESULT_CAP;
    }
}
