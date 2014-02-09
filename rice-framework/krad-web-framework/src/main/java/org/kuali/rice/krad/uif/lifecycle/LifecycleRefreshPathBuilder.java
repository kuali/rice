/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle;

import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleRefreshPathBuilder {

    public static void processLifecycleElements() {
        ViewIndex viewIndex = ViewLifecycle.getView().getViewIndex();

        Map<String, LifecycleElement> lifecycleElements = viewIndex.getLifecycleElements();
        for (LifecycleElement lifecycleElement : lifecycleElements.values()) {
            processLifecycleElement(lifecycleElement);
        }
    }

    protected static void processLifecycleElement(LifecycleElement element) {
        if ((element == null) || !(element instanceof Component)) {
            return;
        }

        Component component = (Component) element;
        if (ComponentUtils.canBeRefreshed(component) || (component instanceof CollectionGroup) ||
                component.isForceSessionPersistence()) {
            ViewPostMetadata viewPostMetadata = ViewLifecycle.getViewPostMetadata();

            ComponentPostMetadata componentPostMetadata = viewPostMetadata.getComponentPostMetadata(component.getId());
            if (componentPostMetadata == null) {
                componentPostMetadata = viewPostMetadata.initializeComponentPostMetadata(component);
            }

            componentPostMetadata.setPath(component.getViewPath());

            buildRefreshPathMappings(component, componentPostMetadata);
        }
    }

    protected static void buildRefreshPathMappings(LifecycleElement lifecycleElement,
            ComponentPostMetadata componentPostMetadata) {
        ViewIndex viewIndex = ViewLifecycle.getView().getViewIndex();

        Map<String, String> elementPhasePathMapping = lifecycleElement.getPhasePathMapping();

        Tree<String, String> initializePhasePaths = initializeNewViewPathTree(elementPhasePathMapping.get(
                UifConstants.ViewPhases.INITIALIZE));
        Tree<String, String> applyModelPhasePaths = initializeNewViewPathTree(elementPhasePathMapping.get(
                UifConstants.ViewPhases.APPLY_MODEL));
        Tree<String, String> finalizePhasePaths = initializeNewViewPathTree(elementPhasePathMapping.get(
                UifConstants.ViewPhases.FINALIZE));

        String finalPath = lifecycleElement.getViewPath();

        String[] parentProperties = ObjectPropertyUtils.splitPropertyPath(finalPath);

        String previousPath = null;
        for (int i = 0; i < parentProperties.length - 1; i++) {
            String path = parentProperties[i];
            if (previousPath != null) {
                path = previousPath + "." + path;
            }

            LifecycleElement parentLifecycleElement = viewIndex.getLifecycleElementByPath(path);
            addElementPaths(parentLifecycleElement, initializePhasePaths, applyModelPhasePaths, finalizePhasePaths);

            previousPath = path;
        }

        Map<String, Tree<String, String>> refreshPathMappings = new HashMap<String, Tree<String, String>>();

        refreshPathMappings.put(UifConstants.ViewPhases.INITIALIZE, initializePhasePaths);
        refreshPathMappings.put(UifConstants.ViewPhases.APPLY_MODEL, applyModelPhasePaths);
        refreshPathMappings.put(UifConstants.ViewPhases.FINALIZE, finalizePhasePaths);

        componentPostMetadata.setRefreshPathMappings(refreshPathMappings);
    }

    public static Tree<String, String> initializeNewViewPathTree(String elementPath) {
        Tree<String, String> viewPathTree = new Tree<String, String>();

        Node<String, String> viewNode = new Node<String, String>("view");
        viewPathTree.setRootElement(viewNode);

        if (elementPath != null) {
            Node<String, String> nextNode = viewNode;

            String[] parentProperties = ObjectPropertyUtils.splitPropertyPath(elementPath);
            for (int i = 0; i < parentProperties.length; i++) {
                String path = parentProperties[i];

                nextNode = addNodeForPath(path, nextNode);
            }

            nextNode.setNodeLabel(UifConstants.REFRESH_ELEMENT_NODE_LABEL);
        }

        return viewPathTree;
    }

    protected static void addElementPaths(LifecycleElement lifecycleElement, Tree<String, String> initializePhasePaths,
            Tree<String, String> applyModelPhasePaths, Tree<String, String> finalizePhasePaths) {
        Map<String, String> phasePathMapping = lifecycleElement.getPhasePathMapping();
        for (Map.Entry<String, String> phasePathEntry : phasePathMapping.entrySet()) {
            if (UifConstants.ViewPhases.INITIALIZE.equals(phasePathEntry.getKey())) {
                addElementPathForPhase(phasePathEntry.getValue(), initializePhasePaths);
            } else if (UifConstants.ViewPhases.APPLY_MODEL.equals(phasePathEntry.getKey())) {
                addElementPathForPhase(phasePathEntry.getValue(), applyModelPhasePaths);
            } else if (UifConstants.ViewPhases.FINALIZE.equals(phasePathEntry.getKey())) {
                addElementPathForPhase(phasePathEntry.getValue(), finalizePhasePaths);
            }
        }
    }

    protected static void addElementPathForPhase(String path, Tree<String, String> phasePaths) {
        String[] pathParents = ObjectPropertyUtils.splitPropertyPath(path);

        Node<String, String> nextNode = phasePaths.getRootElement();

        for (int i = 0; i < pathParents.length; i++) {
            String parent = pathParents[i];

            nextNode = addNodeForPath(parent, nextNode);
        }
    }

    protected static Node<String, String> addNodeForPath(String path, Node<String, String> parentNode) {
        Node<String, String> pathNode = null;

        List<Node<String, String>> nodes = parentNode.getChildren();
        for (Node<String, String> node : nodes) {
            if (node.getData().equals(path)) {
                pathNode = node;
            }
        }

        if (pathNode == null) {
            pathNode = new Node<String, String>(path);
            parentNode.addChild(pathNode);
        }

        return pathNode;
    }

}
