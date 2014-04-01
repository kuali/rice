/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.engine.node.dao.impl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.dao.RouteNodeDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

public class RouteNodeDAOJpa implements RouteNodeDAO {

	private EntityManager entityManager;
    private DataObjectService dataObjectService;

    public static final String FIND_INITIAL_NODE_INSTANCES_NAME = "RouteNodeInstance.FindInitialNodeInstances";
    public static final String FIND_INITIAL_NODE_INSTANCES_QUERY = "select d.initialRouteNodeInstances from "
            + "DocumentRouteHeaderValue d where d.documentId = :documentId";

    /**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

    public RouteNodeInstance findRouteNodeInstanceById(String nodeInstanceId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.ROUTE_NODE_INSTANCE_ID,nodeInstanceId)
        );

        List<RouteNodeInstance> routeNodeInstances = getDataObjectService().findMatching(
                RouteNodeInstance.class,queryByCriteria.build()).getResults();
        if(routeNodeInstances != null && routeNodeInstances.size() > 0){
            return routeNodeInstances.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<RouteNodeInstance> getActiveNodeInstances(String documentId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.DOCUMENT_ID,documentId),
                equal(KEWPropertyConstants.ACTIVE,true)
        );
        return getDataObjectService().findMatching(RouteNodeInstance.class,
                    queryByCriteria.build()).getResults();
    }

    private static final String CURRENT_ROUTE_NODE_NAMES_SQL = "SELECT rn.nm" +
                " FROM krew_rte_node_t rn," +
                "      krew_rte_node_instn_t rni" +
                " LEFT JOIN krew_rte_node_instn_lnk_t rnl" +
                "   ON rnl.from_rte_node_instn_id = rni.rte_node_instn_id" +
                " WHERE rn.rte_node_id = rni.rte_node_id AND" +
                "       rni.doc_hdr_id = ? AND" +
                "       rnl.from_rte_node_instn_id IS NULL";

        @Override
        public List<String> getCurrentRouteNodeNames(final String documentId) {
            final DataSource dataSource = KEWServiceLocator.getDataSource();
            JdbcTemplate template = new JdbcTemplate(dataSource);
            List<String> names = template.execute(new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            return connection.prepareStatement(CURRENT_ROUTE_NODE_NAMES_SQL);
                        }
                    }, new PreparedStatementCallback<List<String>>() {
                        public List<String> doInPreparedStatement(
                                PreparedStatement statement) throws SQLException, DataAccessException {
                            List<String> routeNodeNames = new ArrayList<String>();
                            statement.setString(1, documentId);
                            ResultSet rs = statement.executeQuery();
                            try {
                                while (rs.next()) {
                                    String name = rs.getString("nm");
                                    routeNodeNames.add(name);
                                }
                            } finally {
                                if (rs != null) {
                                    rs.close();
                                }
                            }
                            return routeNodeNames;
                        }
                    }
            );
            return names;
        }
    
    @Override
	public List<String> getActiveRouteNodeNames(final String documentId) {
    	final DataSource dataSource = KEWServiceLocator.getDataSource();
    	JdbcTemplate template = new JdbcTemplate(dataSource);
    	List<String> names = template.execute(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement statement = connection.prepareStatement(
								"SELECT rn.nm FROM krew_rte_node_t rn, krew_rte_node_instn_t rni WHERE rn.rte_node_id "
                                        + "= rni.rte_node_id AND rni.doc_hdr_id = ? AND rni.actv_ind = ?");
						return statement;
					}
				},
				new PreparedStatementCallback<List<String>>() {
					public List<String> doInPreparedStatement(PreparedStatement statement) throws SQLException, DataAccessException {
						List<String> routeNodeNames = new ArrayList<String>();
						statement.setString(1, documentId);
						statement.setBoolean(2, Boolean.TRUE);
						ResultSet rs = statement.executeQuery();
						try {
							while(rs.next()) {
								String name = rs.getString("nm");
								routeNodeNames.add(name);
							}
						} finally {
							if(rs != null) {
								rs.close();
							}
						}
						return routeNodeNames;
					}
				});
    	return names;
	}

    @SuppressWarnings("unchecked")
    public List<RouteNodeInstance> getTerminalNodeInstances(String documentId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.DOCUMENT_ID,documentId),
                equal(KEWPropertyConstants.ACTIVE,false),
                equal(KEWPropertyConstants.COMPLETE,true)
        );

        //FIXME: Can we do this better using just the JPQL query?
        List<RouteNodeInstance> terminalNodes = new ArrayList<RouteNodeInstance>();
        List<RouteNodeInstance> routeNodeInstances = getDataObjectService().
                findMatching(RouteNodeInstance.class,queryByCriteria.build()).getResults();
		for (RouteNodeInstance routeNodeInstance : routeNodeInstances) {
		    if (routeNodeInstance.getNextNodeInstances().isEmpty()) {
		    	terminalNodes.add(routeNodeInstance);
		    }
		}
		return terminalNodes;
    }

    @Override
    public List<String> getTerminalRouteNodeNames(final String documentId) {
        final DataSource dataSource = KEWServiceLocator.getDataSource();
        JdbcTemplate template = new JdbcTemplate(dataSource);
        List<String> names = template.execute(new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement statement = connection.prepareStatement("SELECT rn.nm" +
                                "  FROM krew_rte_node_t rn," +
                                "       krew_rte_node_instn_t rni" +
                                "  LEFT JOIN krew_rte_node_instn_lnk_t rnl" +
                                "    ON rnl.from_rte_node_instn_id = rni.rte_node_instn_id" +
                                "  WHERE rn.rte_node_id = rni.rte_node_id AND" +
                                "        rni.doc_hdr_id = ? AND" +
                                "        rni.actv_ind = ? AND" +
                                "        rni.cmplt_ind = ? AND" +
                                "        rnl.from_rte_node_instn_id IS NULL");
                        return statement;
                    }
                }, new PreparedStatementCallback<List<String>>() {
                    public List<String> doInPreparedStatement(
                            PreparedStatement statement) throws SQLException, DataAccessException {
                        List<String> routeNodeNames = new ArrayList<String>();
                        statement.setString(1, documentId);
                        statement.setBoolean(2, Boolean.FALSE);
                        statement.setBoolean(3, Boolean.TRUE);
                        ResultSet rs = statement.executeQuery();
                        try {
                            while (rs.next()) {
                                String name = rs.getString("nm");
                                routeNodeNames.add(name);
                            }
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        return routeNodeNames;
                    }
                }
        );
        return names;
    }

    public List getInitialNodeInstances(String documentId) {
    	//FIXME: Not sure this query is returning what it needs to     	                                              
    	Query query = entityManager.createNamedQuery(FIND_INITIAL_NODE_INSTANCES_NAME);
    	query.setParameter(KEWPropertyConstants.DOCUMENT_ID, documentId);
		return (List)query.getResultList();
    }

    public NodeState findNodeState(Long nodeInstanceId, String key) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.ROUTE_NODE_INSTANCE_ID,nodeInstanceId.toString()),
                equal(KEWPropertyConstants.KEY,key)
        );

        List<NodeState> nodeStates = getDataObjectService().findMatching(
                        NodeState.class,queryByCriteria.build()).getResults();
        if(nodeStates != null && nodeStates.size() > 0){
            return nodeStates.get(0);
        }
        return null;
    }

    public RouteNode findRouteNodeByName(String documentTypeId, String name) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.DOCUMENT_TYPE_ID,documentTypeId),
                equal(KEWPropertyConstants.ROUTE_NODE_NAME,name)
        );
        List<RouteNode> routeNodes = getDataObjectService().findMatching(
                        RouteNode.class,queryByCriteria.build()).getResults();
        if(routeNodes != null && routeNodes.size() > 0){
            return routeNodes.get(0);
        }
        return null;
    }

    public List<RouteNode> findFinalApprovalRouteNodes(String documentTypeId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.DOCUMENT_TYPE_ID,documentTypeId),
                equal(KEWPropertyConstants.FINAL_APPROVAL,Boolean.TRUE)
        );
    	return getDataObjectService().findMatching(RouteNode.class,queryByCriteria.build()).getResults();
    }

    public List findProcessNodeInstances(RouteNodeInstance process) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.PROCESS_ID,process.getRouteNodeInstanceId())
        );
        return getDataObjectService().findMatching(RouteNodeInstance.class,queryByCriteria.build()).getResults();
    }

    public List findRouteNodeInstances(String documentId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.DOCUMENT_ID,documentId)
        );
        return getDataObjectService().findMatching(RouteNodeInstance.class,queryByCriteria.build()).getResults();
    }

    public void deleteLinksToPreNodeInstances(RouteNodeInstance routeNodeInstance) {
		List<RouteNodeInstance> preNodeInstances = routeNodeInstance.getPreviousNodeInstances();
		for (Iterator<RouteNodeInstance> preNodeInstanceIter = preNodeInstances.iterator(); preNodeInstanceIter.hasNext();) {
		    RouteNodeInstance preNodeInstance = (RouteNodeInstance) preNodeInstanceIter.next();
		    List<RouteNodeInstance> nextInstances = preNodeInstance.getNextNodeInstances();
		    nextInstances.remove(routeNodeInstance);
		    getEntityManager().merge(preNodeInstance);
		}
    }

    public void deleteRouteNodeInstancesHereAfter(RouteNodeInstance routeNodeInstance) {
    	RouteNodeInstance rnInstance = findRouteNodeInstanceById(routeNodeInstance.getRouteNodeInstanceId());
    	entityManager.remove(rnInstance);
    }

    public void deleteNodeStateById(Long nodeStateId) {
        QueryByCriteria.Builder queryByCriteria = QueryByCriteria.Builder.create().setPredicates(
                equal(KEWPropertyConstants.ROUTE_NODE_STATE_ID,nodeStateId)
        );
        List<NodeState> nodeStates = getDataObjectService().findMatching(
                                NodeState.class,queryByCriteria.build()).getResults();
        NodeState nodeState = null;
        if(nodeStates != null && nodeStates.size() > 0){
            nodeState = nodeStates.get(0);
        }
    	getDataObjectService().delete(nodeState);
    }

    public void deleteNodeStates(List statesToBeDeleted) {
		for (Iterator stateToBeDeletedIter = statesToBeDeleted.iterator(); stateToBeDeletedIter.hasNext();) {
		    Long stateId = (Long) stateToBeDeletedIter.next();
		    deleteNodeStateById(stateId);
		}
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


}
