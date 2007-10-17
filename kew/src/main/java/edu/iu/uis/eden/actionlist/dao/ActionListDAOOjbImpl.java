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
package edu.iu.uis.eden.actionlist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionitem.ActionItemActionListExtension;
import edu.iu.uis.eden.actionlist.ActionListFilter;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * OJB implementation of the {@link ActionListDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionListDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListDAOOjbImpl.class);

    public Collection getActionList(WorkflowUser workflowUser, ActionListFilter filter) {
        LOG.info("getting action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
        Criteria crit = new Criteria();
        crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
        if (filter != null) {
            setUpActionListCriteria(crit, filter);
        }
        LOG.info("running query to get action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
        Collection collection = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItemActionListExtension.class, crit));
        LOG.info("finished running query to get action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
        return createActionList(collection);
    }

    private void setUpActionListCriteria(Criteria crit, ActionListFilter filter) {
        LOG.info("setting up Action List criteria");
        boolean filterOn = false;
        String filteredByItems = "";
        if (filter.getActionRequestCd() != null && !"".equals(filter.getActionRequestCd().trim()) && !filter.getActionRequestCd().equals(EdenConstants.ALL_CODE)) {
            if (filter.isExcludeActionRequestCd()) {
                crit.addNotEqualTo("actionRequestCd", filter.getActionRequestCd());
            } else {
                crit.addEqualTo("actionRequestCd", filter.getActionRequestCd());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Requested";
        }

        if (filter.getCreateDateFrom() != null || filter.getCreateDateTo() != null) {
            if (filter.isExcludeCreateDate()) {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.addNotBetween("routeHeader.createDate", new Timestamp(filter.getCreateDateFrom().getTime()), new Timestamp(filter.getCreateDateTo().getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.addLessOrEqualThan("routeHeader.createDate", new Timestamp(filter.getCreateDateFrom().getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.addGreaterOrEqualThan("routeHeader.createDate", new Timestamp(filter.getCreateDateTo().getTime()));
                }
            } else {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.addBetween("routeHeader.createDate", new Timestamp(filter.getCreateDateFrom().getTime()), new Timestamp(filter.getCreateDateTo().getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.addGreaterOrEqualThan("routeHeader.createDate", new Timestamp(filter.getCreateDateFrom().getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.addLessOrEqualThan("routeHeader.createDate", new Timestamp(filter.getCreateDateTo().getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Created";
        }

        if (filter.getDocRouteStatus() != null && !"".equals(filter.getDocRouteStatus().trim()) && !filter.getDocRouteStatus().equals(EdenConstants.ALL_CODE)) {
            if (filter.isExcludeRouteStatus()) {
                crit.addNotEqualTo("routeHeader.docRouteStatus", filter.getDocRouteStatus());
            } else {
                crit.addEqualTo("routeHeader.docRouteStatus", filter.getDocRouteStatus());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Route Status";
        }

        if (filter.getDocumentTitle() != null && !"".equals(filter.getDocumentTitle().trim())) {
            String docTitle = filter.getDocumentTitle();
            if (docTitle.trim().endsWith("*")) {
                docTitle = docTitle.substring(0, docTitle.length() - 1);
            }

            if (filter.isExcludeDocumentTitle()) {
                crit.addNotLike("docTitle", "%" + docTitle + "%");
            } else {
                crit.addLike("docTitle", "%" + docTitle + "%");
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Title";
        }

        if (filter.getDocumentType() != null && !"".equals(filter.getDocumentType().trim())) {
            if (filter.isExcludeDocumentType()) {
                crit.addNotLike("docName", "%" + filter.getDocumentType() + "%");
            } else {
                crit.addLike("docName", "%" + filter.getDocumentType() + "%");
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Type";
        }

        if (filter.getLastAssignedDateFrom() != null || filter.getLastAssignedDateTo() != null) {
            if (filter.isExcludeLastAssignedDate()) {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.addNotBetween("dateAssigned", new Timestamp(filter.getLastAssignedDateFrom().getTime()), new Timestamp(filter.getLastAssignedDateTo().getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.addLessOrEqualThan("dateAssigned", new Timestamp(filter.getLastAssignedDateFrom().getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.addGreaterOrEqualThan("dateAssigned", new Timestamp(filter.getLastAssignedDateTo().getTime()));
                }
            } else {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.addBetween("dateAssigned", new Timestamp(filter.getLastAssignedDateFrom().getTime()), new Timestamp(filter.getLastAssignedDateTo().getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.addGreaterOrEqualThan("dateAssigned", new Timestamp(filter.getLastAssignedDateFrom().getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.addLessOrEqualThan("dateAssigned", new Timestamp(filter.getLastAssignedDateTo().getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Last Assigned";
        }

        filter.setWorkgroupId(null);
        if (filter.getWorkgroupIdString() != null && !"".equals(filter.getWorkgroupIdString().trim()) && !filter.getWorkgroupIdString().trim().equals(EdenConstants.NO_FILTERING)) {
            filter.setWorkgroupId(new Long (filter.getWorkgroupIdString().trim()));
            if (filter.isExcludeWorkgroupId()) {
                Criteria critNotEqual = new Criteria();
                critNotEqual.addNotEqualTo("workgroupId", filter.getWorkgroupId());
                Criteria critNull = new Criteria();
                critNull.addIsNull("workgroupId");
                critNotEqual.addOrCriteria(critNull);
                crit.addAndCriteria(critNotEqual);
            } else {
                crit.addEqualTo("workgroupId", filter.getWorkgroupId());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Request Workgroup";
        }

        if (filteredByItems.length() > 0) {
            filterOn = true;
        }

        if (filter.getDelegatorId() != null && !"".equals(filter.getDelegatorId().trim()) && !filter.getDelegatorId().trim().equals(EdenConstants.DELEGATION_DEFAULT)
                && !filter.getDelegatorId().trim().equals(EdenConstants.ALL_CODE)) {
            filter.setDelegationType(EdenConstants.DELEGATION_SECONDARY);
            filter.setExcludeDelegationType(false);
            Criteria userCrit = new Criteria();
            Criteria groupCrit = new Criteria();
            if (filter.isExcludeDelegatorId()) {
                Criteria userNull = new Criteria();
                userCrit.addNotEqualTo("delegatorWorkflowId", filter.getDelegatorId());
                userNull.addIsNull("delegatorWorkflowId");
                userCrit.addOrCriteria(userNull);
                Criteria groupNull = new Criteria();
                groupCrit.addNotEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
                groupNull.addIsNull("delegatorWorkgroupId");
                groupCrit.addOrCriteria(groupNull);
                crit.addAndCriteria(userCrit);
                crit.addAndCriteria(groupCrit);
            } else {
                Criteria orCrit = new Criteria();
                userCrit.addEqualTo("delegatorWorkflowId", filter.getDelegatorId());
                groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
                orCrit.addOrCriteria(userCrit);
                orCrit.addOrCriteria(groupCrit);
                crit.addAndCriteria(orCrit);
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Delegator Id";
            filterOn = true;
        } else if (filter.getDelegatorId().trim().equals(EdenConstants.DELEGATION_DEFAULT)) {
            filter.setDelegationType(EdenConstants.DELEGATION_SECONDARY);
            filter.setExcludeDelegationType(true);
        } else if (filter.getDelegatorId().trim().equals(EdenConstants.ALL_CODE)) {
            filter.setDelegationType(EdenConstants.DELEGATION_SECONDARY);
            filter.setExcludeDelegationType(false);
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Delegator Id";
            filterOn = true;
        }

        //must come after delegation id since the delegation choices are all secondary delegations
        if (filter.getDelegationType() != null && !"".equals(filter.getDelegationType().trim())) {
            if (filter.isExcludeDelegationType()) {
                Criteria critNotEqual = new Criteria();
                Criteria critNull = new Criteria();
                critNotEqual.addNotEqualTo("delegationType", filter.getDelegationType());
                critNull.addIsNull("delegationType");
                critNotEqual.addOrCriteria(critNull);
                crit.addAndCriteria(critNotEqual);
            } else {
                crit.addEqualTo("delegationType", filter.getDelegationType());
            }
        }

        if (! "".equals(filteredByItems)) {
            filteredByItems = "Filtered by " + filteredByItems;
        }
        filter.setFilterLegend(filteredByItems);
        filter.setFilterOn(filterOn);

        LOG.info("returning from Action List criteria");
    }

    private static final String ACTION_LIST_COUNT_QUERY = "select count(distinct(ai.doc_hdr_id)) from en.en_actn_itm_t ai where ai.actn_itm_prsn_en_id = ? and (ai.dlgn_typ is null or ai.dlgn_typ = 'P')";

    public int getCount(final String workflowId) {
    	return (Integer)getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    statement = connection.prepareStatement(ACTION_LIST_COUNT_QUERY);
                    statement.setString(1, workflowId);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        throw new WorkflowRuntimeException("Error determining Action List Count.");
                    }
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    throw new WorkflowRuntimeException("Error determining Action List Count.", e);
                } catch (LookupException e) {
                    throw new WorkflowRuntimeException("Error determining Action List Count.", e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {}
                    }
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {}
                    }
                }
            }
        });
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per document.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    private Collection createActionList(Collection actionItems) {
    	Map actionItemMap = new HashMap();
    	ActionListPriorityComparator comparator = new ActionListPriorityComparator();
    	for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem potentialActionItem = (ActionItem) iterator.next();
			ActionItem existingActionItem = (ActionItem)actionItemMap.get(potentialActionItem.getRouteHeaderId());
			if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
				actionItemMap.put(potentialActionItem.getRouteHeaderId(), potentialActionItem);
			}
		}
    	return actionItemMap.values();
    }
}