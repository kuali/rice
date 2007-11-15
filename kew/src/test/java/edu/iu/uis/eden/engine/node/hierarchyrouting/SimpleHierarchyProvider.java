/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.engine.node.hierarchyrouting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.NodeState;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeConfigParam;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.NamedRuleSelector;
import edu.iu.uis.eden.util.Utilities;

/**
 * A simple hierarchy provider that provides hierarchy based on doc content
 * <pre>
 * stop id="..." recipient="..." type="..."
 *     stop ...
 *     stop ...
 *       stop ...
 * </pre>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimpleHierarchyProvider implements HierarchyProvider {
    private static final Logger LOG = Logger.getLogger(SimpleHierarchyProvider.class);

    private static class SimpleStop implements Stop {
        private static enum RecipientType { USER, WORKGROUP };
        public SimpleStop parent;
        public List<SimpleStop> children = new ArrayList<SimpleStop>();
        public RecipientType type;
        public String recipient;
        public String id;
        
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof SimpleStop)) return false;
            return id.equals(((SimpleStop) o).id);
        }
        
        public int hashCode() {
            return ObjectUtils.hashCode(id);
        }
    }

    private SimpleStop root;
    private Map<String, SimpleStop> stops = new HashMap<String, SimpleStop>();

    public SimpleHierarchyProvider(RouteContext context) {
        this(context.getDocumentContent().getDocument().getDocumentElement());
    }

    public SimpleHierarchyProvider(Element element) {
        Element rootStop = findRootStop(element);
        root = parseStops(rootStop, null);
    }

    protected Element findRootStop(Element e) {
        if ("stop".equals(e.getNodeName())) return e;
        NodeList nl = e.getElementsByTagName("stop");
        if (nl == null || nl.getLength() == 0) {
            throw new RuntimeException("No stops found");
        }
        return (Element) nl.item(0);
    }

    protected SimpleStop parseStops(Element e, SimpleStop parent) {
        LOG.error("parsing element: " + e + " parent: " + parent);
        SimpleStop stop = parseStop(e);
        LOG.error("parsed stop: "+ stop);
        stop.parent = parent;
        if (parent != null) {
            parent.children.add(stop);
        }
        stops.put(e.getAttribute("id"), stop);
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);
            if (!(n instanceof Element)) continue;
            parseStops((Element) n, stop);
        }
        return stop;
    }

    protected SimpleStop parseStop(Element e) {
        SimpleStop ss = new SimpleStop();
        String recipient = e.getAttribute("recipient");
        String type = e.getAttribute("type");
        String id = e.getAttribute("id");
        if (recipient == null) {
            throw new RuntimeException("malformed document content, missing recipient attribute: " + e);
        }
        if (type == null) {
            throw new RuntimeException("malformed document content, missing type attribute: " + e);
        }
        if (id == null) {
            throw new RuntimeException("malformed document content, missing id attribute: " + e);
        }
        ss.id = id;
        ss.recipient = recipient;
        SimpleStop.RecipientType rtype = SimpleStop.RecipientType.valueOf(type.toUpperCase());
        if (type == null) {
            throw new RuntimeException("Invalid type: " + type);
        }
        ss.type = rtype;
        return ss;
    }

    public List<Stop> getLeafStops(DocumentContent docContent) {
        List<Stop> leafStops = new ArrayList<Stop>();
        for (SimpleStop stop: stops.values()) {
            if (stop.children.size() == 0) {
                leafStops.add(stop);
            }
        }
        return leafStops;
    }

    public Stop getStopByIdentifier(String stopId) {
        return stops.get(stopId);
    }

    public String getStopIdentifier(Stop stop) {
        return ((SimpleStop) stop).id;
    }

    public boolean requestNodeHasStopState(RouteNodeInstance nodeInstance) {
        return nodeInstance.getNodeState("id") != null;
    }
    
    public boolean requestNodeIsAtStop(RouteNodeInstance nodeInstance) {
        return requestNodeHasStopState(nodeInstance);
    }

    public void setRequestNodeInstanceState(RouteNodeInstance requestNodeInstance, Stop stop) {
        requestNodeInstance.addNodeState(new NodeState("id", getStopIdentifier(stop)));
        //requestNodeInstance.addNodeState(new NodeState(EdenConstants.RULE_SELECTOR_NODE_STATE_KEY, "named"));
        //requestNodeInstance.addNodeState(new NodeState(EdenConstants.RULE_NAME_NODE_STATE_KEY, "NodeInstanceRecipientRule"));
        requestNodeInstance.addNodeState(new NodeState("recipient", ((SimpleStop) stop).recipient));
        requestNodeInstance.addNodeState(new NodeState("type", ((SimpleStop) stop).type.name().toLowerCase()));
    }
    
    public boolean equals(Stop a, Stop b) {
        return ObjectUtils.equals(a, b);
    }

    public Stop getParent(Stop stop) {
        return ((SimpleStop) stop).parent;
    }

    public boolean isRoot(Stop stop) {
        return equals(stop, root);
    }

    public void configureRequestNodeInstance(RouteNodeInstance nodeInstance) {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        
    }

    public Stop getStopAtRouteNode(RouteNodeInstance nodeInstance) {
        NodeState state = nodeInstance.getNodeState("id");
        if (state == null) {
            return null;
        } else {
            return stops.get(state.getValue());
        }
    }

    public void configureRequestNode(RouteNodeInstance hierarchyNodeInstance, RouteNode node) {
        Map<String, String> cfgMap = Utilities.getKeyValueCollectionAsMap(node.getConfigParams());
        // propagate rule selector and name from hierarchy node
        if (!cfgMap.containsKey(RouteNode.RULE_SELECTOR_CFG_KEY)) {
            Map<String, String> hierarchyCfgMap = Utilities.getKeyValueCollectionAsMap(hierarchyNodeInstance.getRouteNode().getConfigParams());
            node.getConfigParams().add(new RouteNodeConfigParam(node, RouteNode.RULE_SELECTOR_CFG_KEY, hierarchyCfgMap.get(RouteNode.RULE_SELECTOR_CFG_KEY)));
        }
        if (!cfgMap.containsKey(NamedRuleSelector.RULE_NAME_CFG_KEY)) {
            Map<String, String> hierarchyCfgMap = Utilities.getKeyValueCollectionAsMap(hierarchyNodeInstance.getRouteNode().getConfigParams());
            node.getConfigParams().add(new RouteNodeConfigParam(node, NamedRuleSelector.RULE_NAME_CFG_KEY, hierarchyCfgMap.get(NamedRuleSelector.RULE_NAME_CFG_KEY)));
        }
    }
}