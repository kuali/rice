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
package org.kuali.rice.krad.uif.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

/**
 * Group component that is backed by a <code>Tree</code> data structure and typically
 * rendered as a tree in the user interface
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "treeGroup", parent = "Uif-TreeGroup"),
        @BeanTag(name = "treeSection", parent = "Uif-TreeSection"),
        @BeanTag(name = "treeSubSection", parent = "Uif-TreeSubSection")})
public class TreeGroup extends GroupBase implements DataBinding {
    private static final long serialVersionUID = 5841343037089286740L;

    private String propertyName;
    private BindingInfo bindingInfo;

    private Map<Class<?>, NodePrototype> nodePrototypeMap;
    private NodePrototype defaultNodePrototype;

    private Tree<Group, Message> treeGroups;

    private org.kuali.rice.krad.uif.widget.Tree tree;

    public TreeGroup() {
        super();

        treeGroups = new Tree<Group, Message>();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Set fieldBindModelPath to the collection model path (since the fields
     * have to belong to the same model as the collection)</li>
     * <li>Set defaults for binding</li>
     * <li>Calls view helper service to initialize prototypes</li>
     * </ul>
     */
    @Override
    public void performInitialization(Object model) {
        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(ViewLifecycle.getActiveLifecycle().getView(), getPropertyName());
        }

        // TODO: set object path for prototypes equal to the tree group object path?
    }

    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        buildTreeGroups(model);
    }

    /**
     * Builds the components that will be rendered as part of the tree group
     *
     * <p>
     * The component tree group mirrors the tree data structure on the model. For each node of
     * the data structure, a corresponding <code>Message</code>  will be created for the node
     * label, and a <code>Group</code> component for the node data. These are placed into a new
     * node for the component tree. After the tree is built it is set as a property on the tree group
     * to be read by the renderer
     * </p>
     *
     * @param model object containing the view data from which the tree data will be retrieved
     */
    protected void buildTreeGroups(Object model) {
        // get Tree data property
        Tree<Object, String> treeData = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());

        // build component tree that corresponds with tree data
        Tree<Group, Message> treeGroups = new Tree<Group, Message>();

        String bindingPrefix = getBindingInfo().getBindingPrefixForNested();
        Node<Group, Message> rootNode = buildTreeNode(treeData.getRootElement(),
                bindingPrefix + /* TODO: hack */ ".rootElement", "root");
        treeGroups.setRootElement(rootNode);

        setTreeGroups(treeGroups);
    }

    protected Node<Group, Message> buildTreeNode(Node<Object, String> nodeData, String bindingPrefix,
            String parentNode) {
        if (nodeData == null) {
            return null;
        }

        Node<Group, Message> node = new Node<Group, Message>();
        node.setNodeType(nodeData.getNodeType());

        NodePrototype prototype = getNodePrototype(nodeData);

        Message message = ComponentUtils.copy(prototype.getLabelPrototype(), parentNode);
        ContextUtils.pushObjectToContextDeep(message, UifConstants.ContextVariableNames.NODE, nodeData);
        message.setMessageText(nodeData.getNodeLabel());
        node.setNodeLabel(message);

        Group nodeGroup = ComponentUtils.copyComponent(prototype.getDataGroupPrototype(), bindingPrefix + ".data",
                parentNode);
        ContextUtils.pushObjectToContextDeep(nodeGroup, UifConstants.ContextVariableNames.NODE, nodeData);

        String nodePath = bindingPrefix + ".data";
        if (StringUtils.isNotBlank(getBindingInfo().getBindingObjectPath())) {
            nodePath = getBindingInfo().getBindingObjectPath() + "." + nodePath;
        }
        ContextUtils.pushObjectToContextDeep(nodeGroup, UifConstants.ContextVariableNames.NODE_PATH, nodePath);
        node.setData(nodeGroup);

        List<Node<Group, Message>> nodeChildren = new ArrayList<Node<Group, Message>>();

        int childIndex = 0;
        for (Node<Object, String> childDataNode : nodeData.getChildren()) {
            String nextBindingPrefix = bindingPrefix + ".children[" + childIndex + "]";
            Node<Group, Message> childNode = buildTreeNode(childDataNode, nextBindingPrefix,
                    "_node_" + childIndex + ("root".equals(parentNode) ? "_parent_" : "_parent") + parentNode);

            nodeChildren.add(childNode);

            // Don't forget about me:
            ++childIndex;
        }
        node.setChildren(nodeChildren);

        return node;
    }

    /**
     * Gets the NodePrototype to use for the given Node
     */
    private NodePrototype getNodePrototype(Node<Object, String> nodeData) {
        NodePrototype result = null;
        if (nodeData != null && nodeData.getData() != null) {
            Class<?> dataClass = nodeData.getData().getClass();
            result = nodePrototypeMap.get(dataClass);

            // somewhat lame fallback - to do this right we'd find all entries that are assignable from the data class
            // and then figure out which one is the closest relative
            if (result == null) {
                for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodePrototypeMap.entrySet()) {
                    if (prototypeEntry.getKey().isAssignableFrom(dataClass)) {
                        result = prototypeEntry.getValue();
                        break;
                    }
                }
            }
        }

        if (result == null) {
            result = defaultNodePrototype;
        }

        return result;
    }

    /**
     * Gets all node components within the tree.
     * 
     * @return list of node components
     */
    public List<Component> getNodeComponents() {
        List<Component> components = new ArrayList<Component>();
        addNodeComponents(treeGroups.getRootElement(), components);
        return components;
    }

    /**
     * Gets all node components prototypes within the tree.
     * 
     * @return list of node component prototypes
     */
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        if (defaultNodePrototype != null) {
            components.add(defaultNodePrototype.getLabelPrototype());
            components.add(defaultNodePrototype.getDataGroupPrototype());
        }

        if (nodePrototypeMap != null) {
            for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodePrototypeMap.entrySet()) {
                NodePrototype prototype = prototypeEntry.getValue();
                if (prototype != null) {
                    components.add(prototype.getLabelPrototype());
                    components.add(prototype.getDataGroupPrototype());
                }
            }
        }

        return components;
    }
    
    /**
     * Retrieves the <code>Component</code> instances from the node for building the nested
     * components list
     *
     * @param node node to pull components from
     * @param components list to add components to
     */
    protected void addNodeComponents(Node<Group, Message> node, List<Component> components) {
        if (node != null) {
            components.add(node.getNodeLabel());
            components.add(node.getData());

            for (Node<Group, Message> nodeChild : node.getChildren()) {
                addNodeComponents(nodeChild, components);
            }
        }
    }

    @BeanTagAttribute
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @BeanTagAttribute
    public BindingInfo getBindingInfo() {
        return bindingInfo;
    }

    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * @return the defaultNodePrototype
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public NodePrototype getDefaultNodePrototype() {
        return this.defaultNodePrototype;
    }

    /**
     * @param defaultNodePrototype the defaultNodePrototype to set
     */
    public void setDefaultNodePrototype(NodePrototype defaultNodePrototype) {
        this.defaultNodePrototype = defaultNodePrototype;
    }

    /**
     * @return the nodePrototypeMap
     */
    @BeanTagAttribute
    public Map<Class<?>, NodePrototype> getNodePrototypeMap() {
        return this.nodePrototypeMap;
    }

    /**
     * @param nodePrototypeMap the nodePrototypeMap to set
     */
    public void setNodePrototypeMap(Map<Class<?>, NodePrototype> nodePrototypeMap) {
        this.nodePrototypeMap = nodePrototypeMap;
    }

    @BeanTagAttribute
    public Tree<Group, Message> getTreeGroups() {
        return treeGroups;
    }

    public void setTreeGroups(Tree<Group, Message> treeGroups) {
        this.treeGroups = treeGroups;
    }

    @BeanTagAttribute
    public org.kuali.rice.krad.uif.widget.Tree getTree() {
        return tree;
    }

    public void setTree(org.kuali.rice.krad.uif.widget.Tree tree) {
        this.tree = tree;
    }

    /**
     * Copies a {@link Node} instance and then recursively copies each of its child nodes
     *
     * @param node node instance to copy
     * @return new node instance copied from given node
     */
    protected Node<Group, Message> copyNode(Node<Group, Message> node) {
        Node<Group, Message> nodeCopy = new Node<Group, Message>();

        if (node == null) {
            return null;
        }

        nodeCopy.setNodeType(node.getNodeType());

        if (node.getData() != null) {
            nodeCopy.setData((Group) CopyUtils.copy(node.getData()));
        }

        if (node.getNodeLabel() != null) {
            nodeCopy.setNodeLabel((Message) CopyUtils.copy(node.getNodeLabel()));
        }

        if (node.getChildren() != null) {
            List<Node<Group, Message>> childrenCopy = new ArrayList<Node<Group, Message>>();
            for (Node<Group, Message> childNode : node.getChildren()) {
                childrenCopy.add(copyNode(childNode));
            }

            nodeCopy.setChildren(childrenCopy);
        }

        return nodeCopy;
    }
}