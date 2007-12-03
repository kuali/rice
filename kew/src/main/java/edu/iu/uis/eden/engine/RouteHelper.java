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
package edu.iu.uis.eden.engine;

import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.engine.node.BasicJoinEngine;
import edu.iu.uis.eden.engine.node.DynamicNode;
import edu.iu.uis.eden.engine.node.JoinEngine;
import edu.iu.uis.eden.engine.node.JoinNode;
import edu.iu.uis.eden.engine.node.Node;
import edu.iu.uis.eden.engine.node.RequestActivationNode;
import edu.iu.uis.eden.engine.node.RequestsNode;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.SimpleNode;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SubProcessNode;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A helper class which provides some useful utilities for examining and generating nodes.
 * Provides access to the {@link JoinEngine} and the {@link RoutingNodeFactory}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteHelper {

    private JoinEngine joinEngine = new BasicJoinEngine();
    private RoutingNodeFactory nodeFactory = new RoutingNodeFactory();

    public JoinEngine getJoinEngine() {
        return joinEngine;
    }

    public RoutingNodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public boolean isSimpleNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), SimpleNode.class);
    }

    public boolean isJoinNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), JoinNode.class);
    }

    public boolean isSplitNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), SplitNode.class);
    }

    public boolean isDynamicNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), DynamicNode.class);
    }

    public boolean isSubProcessNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), SubProcessNode.class);
    }

    public boolean isRequestActivationNode(RouteNode routeNode) {
        return ClassLoaderUtils.isInstanceOf(getNode(routeNode), RequestActivationNode.class);
    }

    public boolean isRequestsNode(RouteNode routeNode) {
        return getNode(routeNode) instanceof RequestsNode;
    }

    public Node getNode(RouteNode routeNode) {
    	return (Node)GlobalResourceLoader.getObject(new ObjectDefinition(routeNode.getNodeType(), routeNode.getDocumentType().getMessageEntity()));
    }
}
