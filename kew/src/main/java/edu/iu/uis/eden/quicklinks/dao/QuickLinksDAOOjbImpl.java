/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.quicklinks.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ojb.broker.PersistenceBroker;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.docsearch.DocumentSearchService;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypePolicy;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.quicklinks.ActionListStats;
import edu.iu.uis.eden.quicklinks.InitiatedDocumentType;
import edu.iu.uis.eden.quicklinks.WatchedDocument;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

public class QuickLinksDAOOjbImpl extends PersistenceBrokerDaoSupport implements QuickLinksDAO {

    public List getActionListStats(final WorkflowUser workflowUser) {
        return (List) this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement selectActionItems = null;
                PreparedStatement selectDocTypeLabel = null;
                ResultSet selectedActionItems = null;
                ResultSet selectedDocTypeLabel = null;
                List docTypes = new ArrayList();
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    selectActionItems = connection.prepareStatement("select DOC_TYP_NM, COUNT(*) from EN_ACTN_ITM_T where ACTN_ITM_PRSN_EN_ID = ? " +
                            "and (dlgn_typ is null or dlgn_typ != '" + EdenConstants.DELEGATION_SECONDARY + "') group by DOC_TYP_NM");
                    selectDocTypeLabel = connection.prepareStatement("select DOC_TYP_LBL_TXT from EN_DOC_TYP_T WHERE DOC_TYP_NM = ? and DOC_TYP_CUR_IND = 1");
                    selectActionItems.setString(1, workflowUser.getWorkflowUserId().getWorkflowId());
                    selectedActionItems = selectActionItems.executeQuery();
                    while (selectedActionItems.next()) {
                        String docTypeName = selectedActionItems.getString(1);
                        int count = selectedActionItems.getInt(2);
                        selectDocTypeLabel.setString(1, docTypeName);
                        selectedDocTypeLabel = selectDocTypeLabel.executeQuery();
                        if (selectedDocTypeLabel.next()) {
                            docTypes.add(new ActionListStats(docTypeName, selectedDocTypeLabel.getString(1), count));
                        }
                    }
                    Collections.sort(docTypes);
                    return docTypes;
                } catch (Exception e) {
                    throw new WorkflowRuntimeException("Error getting action list stats for user: " + workflowUser.getAuthenticationUserId().getAuthenticationId(), e);
                } finally {
                    if (selectActionItems != null) {
                        try {
                            selectActionItems.close();
                        } catch (SQLException e) {
                        }
                    }

                    if (selectDocTypeLabel != null) {
                        try {
                            selectDocTypeLabel.close();
                        } catch (SQLException e) {
                        }
                    }

                    if (selectedActionItems != null) {
                        try {
                            selectedActionItems.close();
                        } catch (SQLException e) {
                        }
                    }

                    if (selectedDocTypeLabel != null) {
                        try {
                            selectedDocTypeLabel.close();
                        } catch (SQLException e) {
                        }
                    }

                }
            }
        });
    }

    public List getInitiatedDocumentTypesList(final WorkflowUser workflowUser) {
        return (List)  this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {

            public Object doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement selectDistinctDocumentTypes = null;
                ResultSet selectedDistinctDocumentTypes = null;
                List documentTypesByName = new ArrayList();
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
//                  select the doc type only if the SUPPORTS_QUICK_INITIATE policy is NULL or true
                    String sql = "select distinct B.DOC_TYP_NM, B.DOC_TYP_LBL_TXT from EN_DOC_HDR_T A, EN_DOC_TYP_T B "+
                    	"where A.DOC_INITR_PRSN_EN_ID = ? and A.DOC_TYP_ID = B.DOC_TYP_ID and " +
                    	"B.DOC_TYP_ACTV_IND = 1 and B.DOC_TYP_CUR_IND = 1 " +
                    	"order by upper(B.DOC_TYP_LBL_TXT)";
                    selectDistinctDocumentTypes = connection.prepareStatement(sql);
                    selectDistinctDocumentTypes.setString(1, workflowUser.getWorkflowUserId().getWorkflowId());
                    selectedDistinctDocumentTypes = selectDistinctDocumentTypes.executeQuery();
                    String documentNames = Utilities.getApplicationConstant(EdenConstants.QUICK_LINKS_RESTRICT_DOCUMENT_TYPES).trim();
                    if (documentNames == null || "none".equals(documentNames)) {
                    	documentNames = "";
                    }
                    List docTypesToRestrict = new ArrayList();
                    StringTokenizer st = new StringTokenizer(documentNames, ",");
                    while (st.hasMoreTokens()) {
                        docTypesToRestrict.add(st.nextToken());
                    }
                    while (selectedDistinctDocumentTypes.next()) {
                        String docTypeName = selectedDistinctDocumentTypes.getString(1);
                        String docTypeTopParent = "";
                        int firstPeriod = docTypeName.indexOf(".");
                        if (firstPeriod == -1) {
                            docTypeTopParent = docTypeName.substring(0);
                        } else {
                            docTypeTopParent = docTypeName.substring(0, firstPeriod);
                        }
                        if (!docTypesToRestrict.contains(docTypeTopParent)) {
                        	// the document types should be cached so this should be pretty quick
                        	DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(docTypeName);
                        	DocumentTypePolicy quickInitiatePolicy = docType.getSupportsQuickInitiatePolicy();
                            if (quickInitiatePolicy.getPolicyValue().booleanValue()) {
                            	documentTypesByName.add(new InitiatedDocumentType(docTypeName, selectedDistinctDocumentTypes.getString(2)));
                            }
                        }
                    }
                    return documentTypesByName;
                } catch (Exception e) {
                    throw new WorkflowRuntimeException("Error getting initiated document types for user: " + workflowUser.getAuthenticationUserId().getAuthenticationId(), e);
                } finally {
                    if (selectDistinctDocumentTypes != null) {
                        try {
                            selectDistinctDocumentTypes.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (selectedDistinctDocumentTypes != null) {
                        try {
                            selectedDistinctDocumentTypes.close();
                        } catch (SQLException e) {
                        }
                    }

                }

            }
        });
    }

    public List getNamedSearches(WorkflowUser workflowUser) {
        return getDocumentSearchService().getNamedSearches(workflowUser);
    }

    public List getRecentSearches(WorkflowUser workflowUser) {
        return getDocumentSearchService().getMostRecentSearches(workflowUser);
    }

    public List getWatchedDocuments(final WorkflowUser workflowUser) {
        return (List) this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
                List watchedDocuments = new ArrayList();
                PreparedStatement selectWatchedDocuments = null;
                ResultSet selectedWatchedDocuments = null;
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    selectWatchedDocuments = connection.prepareStatement("select DOC_HDR_ID, DOC_RTE_STAT_CD, DOC_TTL, DOC_CRTE_DT from EN_DOC_HDR_T where DOC_INITR_PRSN_EN_ID = ? and DOC_RTE_STAT_CD in ('"+ EdenConstants.ROUTE_HEADER_ENROUTE_CD +"','"+ EdenConstants.ROUTE_HEADER_EXCEPTION_CD +"') order by DOC_CRTE_DT desc");
                    selectWatchedDocuments.setString(1, workflowUser.getWorkflowUserId().getWorkflowId());
                    selectedWatchedDocuments = selectWatchedDocuments.executeQuery();
                    while (selectedWatchedDocuments.next()) {
                        watchedDocuments.add(new WatchedDocument(selectedWatchedDocuments.getString(1), (String)EdenConstants.DOCUMENT_STATUSES.get(selectedWatchedDocuments.getString(2)), selectedWatchedDocuments.getString(3)));
                    }           
                    return watchedDocuments;
                } catch (Exception e) {
                    throw new WorkflowRuntimeException("Error getting initiated document types for user: " + workflowUser.getAuthenticationUserId().getAuthenticationId(), e);
                } finally {
                    if (selectWatchedDocuments != null) {
                        try {
                            selectWatchedDocuments.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (selectedWatchedDocuments != null) {
                        try {
                            selectedWatchedDocuments.close();
                        } catch (SQLException e) {
                        }
                    }

                }
            }
        });
    }

    public DocumentTypeService getDocumentTypeService() {
        return ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE));
    }

    public DocumentSearchService getDocumentSearchService() {
        return ((DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE));
    }

}