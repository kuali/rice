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
package org.kuali.rice.krad.uif.lifecycle;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For components that can be refreshed, builds out the various paths the lifecycle needs to be run on when
 * the component refresh process is run.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleRefreshPathBuilder {

    /**
     * Iterates through each {@link org.kuali.rice.krad.uif.util.LifecycleElement} that has been collected in
     * the view index and invokes refresh path processing.
     */
    public static void processLifecycleElements() {
        ViewIndex viewIndex = ViewLifecycle.getView().getViewIndex();

        Map<String, LifecycleElement> lifecycleElements = viewIndex.getLifecycleElementsByPath();
        for (LifecycleElement lifecycleElement : lifecycleElements.values()) {
            processLifecycleElement(lifecycleElement);
        }
    }

    /**
     * Determines whether the given lifecycle element is capable of being refreshed, and if so invokes
     * {@link LifecycleRefreshPathBuilder#buildRefreshPathMappings} to build the refresh paths.
     *
     * @param element lifecycle element to build refresh paths for (if necessary)
     */
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

            storePhasePathMapping(component, componentPostMetadata);
        }
    }

    /**
     * Builds the refresh paths for the given lifecycle element and sets onto the post metadata for storage.
     *
     * <p>For each lifecycle phase, a list of paths that the refresh lifecycle for the given should process
     * is built. These are then stored on the component post metadata for retrieval on the refresh call</p>
     *
     * @param lifecycleElement lifecycle element to build paths for
     * @param componentPostMetadata post metadata instance to store the paths
     */
    protected static void buildRefreshPathMappings(LifecycleElement lifecycleElement,
            ComponentPostMetadata componentPostMetadata) {
        List<String> initializePaths = new ArrayList<String>();
        List<String> applyModelPaths = new ArrayList<String>();
        List<String> finalizePaths = new ArrayList<String>();

        String refreshElementPath = lifecycleElement.getViewPath();
        processElementPath(refreshElementPath, initializePaths, applyModelPaths, finalizePaths, new HashSet<String>());

        Map<String, List<String>> refreshPathMappings = new HashMap<String, List<String>>();

        refreshPathMappings.put(UifConstants.ViewPhases.INITIALIZE, initializePaths);
        refreshPathMappings.put(UifConstants.ViewPhases.APPLY_MODEL, applyModelPaths);
        refreshPathMappings.put(UifConstants.ViewPhases.FINALIZE, finalizePaths);

        componentPostMetadata.setRefreshPathMappings(refreshPathMappings);
    }

    /**
     * Store phase path mapping for component if there are any paths different from the final path.
     *
     * @param lifecycleElement lifecycle element to store phase mapping for
     * @param componentPostMetadata post metadata instance to hold mapping
     */
    protected static void storePhasePathMapping(LifecycleElement lifecycleElement,
            ComponentPostMetadata componentPostMetadata) {
        Map<String, String> storedPhasePathMapping = new HashMap<String, String>();

        String refreshElementPath = lifecycleElement.getViewPath();

        Map<String, String> phasePathMapping = lifecycleElement.getPhasePathMapping();
        for (Map.Entry<String, String> phasePathEntry : phasePathMapping.entrySet()) {
            if (!StringUtils.equals(phasePathEntry.getValue(), refreshElementPath)) {
                storedPhasePathMapping.put(phasePathEntry.getKey(), phasePathEntry.getValue());
            }
        }

        if (!storedPhasePathMapping.isEmpty()) {
            componentPostMetadata.setPhasePathMapping(storedPhasePathMapping);
        }
    }

    /**
     * Processes the element at the given path, and all its parents (given by properties within the path) adding
     * their refresh paths to the lists being built.
     *
     * @param path path of the element to process
     * @param initializePaths list of refresh paths for the intialize phase
     * @param applyModelPaths list of refresh paths for the apply model phase
     * @param finalizePaths list of refresh paths for the finalize phase
     * @param visitedPaths list of paths that have already been processed
     */
    protected static void processElementPath(String path, List<String> initializePaths, List<String> applyModelPaths,
            List<String> finalizePaths, Set<String> visitedPaths) {
        String[] parentProperties = ObjectPropertyUtils.splitPropertyPath(path);

        String previousPath = null;
        for (int i = 0; i < parentProperties.length; i++) {
            String parentPath = parentProperties[i];
            if (previousPath != null) {
                parentPath = previousPath + "." + parentPath;
            }

            if (!visitedPaths.contains(parentPath)) {
                visitedPaths.add(parentPath);

                addElementRefreshPaths(parentPath, initializePaths, applyModelPaths, finalizePaths, visitedPaths);
            }

            previousPath = parentPath;
        }
    }

    /**
     * Retrieves the lifecycle element from the view index that has the given path, then adds its refresh
     * paths to the given lists.
     *
     * <p>If the lifecycle element had a different path in one the phases, a call is made back to
     * {@link #processElementPath} to process any new parents.</p>
     *
     * @param elementPath path of the element to add paths for
     * @param initializePaths list of refresh paths for the intialize phase
     * @param applyModelPaths list of refresh paths for the apply model phase
     * @param finalizePaths list of refresh paths for the finalize phase
     * @param visitedPaths list of paths that have already been processed
     */
    protected static void addElementRefreshPaths(String elementPath, List<String> initializePaths,
            List<String> applyModelPaths, List<String> finalizePaths, Set<String> visitedPaths) {
        LifecycleElement lifecycleElement = ViewLifecycle.getView().getViewIndex().getLifecycleElementByPath(
                elementPath);
        if (lifecycleElement == null) {
            return;
        }

        Map<String, String> phasePathMapping = lifecycleElement.getPhasePathMapping();
        for (Map.Entry<String, String> phasePathEntry : phasePathMapping.entrySet()) {
            String refreshPath = phasePathEntry.getValue();

            addElementRefreshPath(refreshPath, phasePathEntry.getKey(), initializePaths, applyModelPaths,
                    finalizePaths);

            // if the path is different from the element path we are processing, process its parents
            if (StringUtils.equals(elementPath, refreshPath)) {
                processElementPath(refreshPath, initializePaths, applyModelPaths, finalizePaths, visitedPaths);
            }
        }
    }

    /**
     * Adds the given path to the refresh path list for the given phase.
     *
     * @param path path to add
     * @param phase phase for the list the path should be added to
     * @param initializePaths list of refresh paths for the intialize phase
     * @param applyModelPaths list of refresh paths for the apply model phase
     * @param finalizePaths list of refresh paths for the finalize phase
     */
    protected static void addElementRefreshPath(String path, String phase, List<String> initializePaths,
            List<String> applyModelPaths, List<String> finalizePaths) {
        if (UifConstants.ViewPhases.INITIALIZE.equals(phase)) {
            initializePaths.add(path);
        } else if (UifConstants.ViewPhases.APPLY_MODEL.equals(phase)) {
            applyModelPaths.add(path);
        } else if (UifConstants.ViewPhases.FINALIZE.equals(phase)) {
            finalizePaths.add(path);
        }
    }

}
