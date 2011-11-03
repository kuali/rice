--
-- Copyright 2005-2011 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

UPDATE EN_RULE_RSP_T SET RULE_RSP_NM='org.kuali.rice.kew.rule.InitiatorRoleAttribute!INITIATOR' WHERE RULE_RSP_NM='edu.iu.uis.eden.routetemplate.InitiatorRoleAttribute!INITIATOR'
/
UPDATE EN_RULE_RSP_T SET RULE_RSP_NM='org.kuali.rice.kew.rule.RoutedByUserRoleAttribute!ROUTED_BY_USER' WHERE RULE_RSP_NM='edu.iu.uis.eden.routetemplate.RoutedByUserRoleAttribute!ROUTED_BY_USER'
/
UPDATE EN_RULE_RSP_T SET RULE_RSP_NM='org.kuali.rice.kew.rule.NetworkIdRoleAttribute!networkId' WHERE RULE_RSP_NM='edu.iu.uis.eden.routetemplate.NetworkIdRoleAttribute!networkId'
/
UPDATE EN_RULE_RSP_T SET RULE_RSP_NM='org.kuali.rice.kew.rule.UniversityIdRoleAttribute!universityId' WHERE RULE_RSP_NM='edu.iu.uis.eden.routetemplate.UniversityIdRoleAttribute!universityId'
/
UPDATE EN_RULE_RSP_T SET RULE_RSP_NM='org.kuali.rice.ken.kew.ChannelReviewerRoleAttribute!reviewers' WHERE RULE_RSP_NM='org.kuali.notification.kew.ChannelReviewerRoleAttribute!reviewers'
/

UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='org.kuali.workflow.postprocessor.KualiPostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.removereplace.RemoveReplacePostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.removereplace.RemoveReplacePostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.rule.RulePostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.routetemplate.RulePostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.postprocessor.DefaultPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.postprocessor.DefaultPostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.edl.EDocLitePostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.edl.EDocLitePostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.workgroup.WorkgroupPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.workgroup.WorkgroupPostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.kew.edl.EDLDatabasePostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='edu.iu.uis.eden.edl.EDLDatabasePostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.ken.postprocessor.kew.NotificationPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='org.kuali.notification.postprocessor.kew.NotificationPostProcessor'
/
UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM='org.kuali.rice.ken.postprocessor.kew.NotificationSenderFormPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM='org.kuali.notification.postprocessor.kew.NotificationSenderFormPostProcessor'
/

UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.InitialNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.InitialNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.NoOpNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.NoOpNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.RequestsNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.RequestsNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.SimpleJoinNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.SimpleJoinNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.SimpleSplitNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.SimpleSplitNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.FYIByNetworkId' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.FYIByNetworkId'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.FYIByUniversityId' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.FYIByUniversityId'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.DynamicNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.DynamicNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.IteratedRequestActivationNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.IteratedRequestActivationNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.KRAMetaRuleNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.KRAMetaRuleNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.LogNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.LogNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.RequestActivationNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.RequestActivationNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.SimpleSubProcessNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.SimpleSubProcessNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.HierarchyRoutingNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.HierarchyRoutingNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.EmailNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.EmailNode'
/
UPDATE EN_RTE_NODE_T SET RTE_NODE_TYP='org.kuali.rice.kew.engine.node.SetVarNode' WHERE RTE_NODE_TYP='edu.iu.uis.eden.engine.node.SetVarNode'
/

