package org.kuali.rice.krad.uif.container;

import org.kuali.rice.core.util.Node;
import org.kuali.rice.core.util.Tree;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.core.BindingInfo;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.widget.TreeWidget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Group component that is backed by a <code>Tree</code> data structure and typically
 * rendered as a tree in the user interface
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TreeGroup extends Group {
    private static final long serialVersionUID = 5841343037089286740L;

    private String propertyName;
    private BindingInfo bindingInfo;

    private Map<Class<?>, NodePrototype> nodeProtypeMap;
    private NodePrototype defaultNodePrototype;

    private Tree<Group, MessageField> treeGroups;

    private TreeWidget treeWidget;

    public TreeGroup() {
        super();

        treeGroups = new Tree<Group, MessageField>();
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
     *
     * @see org.kuali.rice.kns.uif.core.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
     */
    @Override
    public void performInitialization(View view) {
        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(view);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        // TODO: set object path for prototypes equal to the tree group object path?

        initializeNodePrototypeComponents(view);
    }

    /**
     * This method initializes {@link org.kuali.rice.kns.uif.core.Component}s within the {@link NodePrototype}s
     *
     * @param view
     */
    private void initializeNodePrototypeComponents(View view) {
        view.getViewHelperService().performComponentInitialization(view, defaultNodePrototype.getLabelPrototype());
        view.getViewHelperService().performComponentInitialization(view, defaultNodePrototype.getDataGroupPrototype());

        if (nodeProtypeMap != null)
            for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodeProtypeMap.entrySet()) {
                NodePrototype prototype = prototypeEntry.getValue();
                if (prototype != null) {

                    if (prototype.getLabelPrototype() != null) {
                        view.getViewHelperService().performComponentInitialization(view, prototype.getLabelPrototype());
                    } else {
                        throw new IllegalStateException("encountered null NodePrototype.labelPrototype");
                    }

                    if (prototype.getDataGroupPrototype() != null) {
                        view.getViewHelperService()
                                .performComponentInitialization(view, prototype.getDataGroupPrototype());
                    } else {
                        throw new IllegalStateException("encountered null NodePrototype.dataGroupPrototype");
                    }
                } else {
                    throw new IllegalStateException("encountered null NodePrototype");
                }
            }
    }

    /**
     * @see org.kuali.rice.kns.uif.container.ContainerBase#performApplyModel(org.kuali.rice.kns.uif.container.View,
     *      java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model) {
        super.performApplyModel(view, model);

        buildTreeGroups(view, model);
    }

    /**
     * Builds the components that will be rendered as part of the tree group
     *
     * <p>
     * The component tree group mirrors the tree data structure on the model. For each node of
     * the data structure, a corresponding <code>MessageField</code>  will be created for the node
     * label, and a <code>Group</code> component for the node data. These are placed into a new
     * node for the component tree. After the tree is built it is set as a property on the tree group
     * to be read by the renderer
     * </p>
     *
     * @param view - view instance the tree group belongs to
     * @param model - object containing the view data from which the tree data will be retrieved
     */
    protected void buildTreeGroups(View view, Object model) {
        // get Tree data property
        Tree<Object, String> treeData = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());

        // build component tree that corresponds with tree data
        Tree<Group, MessageField> treeGroups = new Tree<Group, MessageField>();

        String bindingPrefix = getBindingInfo().getBindingPrefixForNested();
        Node<Group, MessageField> rootNode = buildTreeNode(treeData.getRootElement(), bindingPrefix, 0);
        treeGroups.setRootElement(rootNode);

        setTreeGroups(treeGroups);
    }

    protected Node<Group, MessageField> buildTreeNode(Node<Object, String> nodeData, String bindingPrefix,
            int nodeCounter) {
        Node<Group, MessageField> node = new Node<Group, MessageField>();
        node.setNodeType(nodeData.getNodeType());

        String idSuffix = "_n" + nodeCounter;

        NodePrototype prototype = getNodePrototype(nodeData);

        MessageField messageField = ComponentUtils.copy(prototype.getLabelPrototype(), idSuffix);
        ComponentUtils.pushObjectToContext(messageField, UifConstants.ContextVariableNames.NODE, nodeData);
        messageField.setMessageText(nodeData.getNodeLabel());
        node.setNodeLabel(messageField);

        Group nodeGroup =
                ComponentUtils.copyComponent(prototype.getDataGroupPrototype(), bindingPrefix + ".data", idSuffix);
        ComponentUtils.pushObjectToContext(nodeGroup, UifConstants.ContextVariableNames.NODE, nodeData);
        node.setData(nodeGroup);

        List<Node<Group, MessageField>> nodeChildren = new ArrayList<Node<Group, MessageField>>();

        int childIndex = -1;
        for (Node<Object, String> childDataNode : nodeData.getChildren()) {
            String nextBindingPrefix = bindingPrefix + ".children[" + childIndex + "]";
            Node<Group, MessageField> childNode = buildTreeNode(childDataNode, nextBindingPrefix, nodeCounter++);

            nodeChildren.add(childNode);
        }
        node.setChildren(nodeChildren);

        return node;
    }

    /**
     * This method gets the NodePrototype to use for the given Node
     */
    private NodePrototype getNodePrototype(Node<Object, String> nodeData) {
        NodePrototype result = null;
        if (nodeData != null && nodeData.getData() != null) {
            Class<?> dataClass = nodeData.getData().getClass();
            result = nodeProtypeMap.get(dataClass);

            // somewhat lame fallback - to do this right we'd find all entries that are assignable from the data class
            // and then figure out which one is the closest relative
            if (result == null)
                for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodeProtypeMap.entrySet()) {
                    if (prototypeEntry.getKey().isAssignableFrom(dataClass)) {
                        result = prototypeEntry.getValue();
                        break;
                    }
                }
        }
        if (result == null)
            result = defaultNodePrototype;
        return result;
    }

    /**
     * @see org.kuali.rice.kns.uif.container.ContainerBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(treeWidget);
        addNodeComponents(treeGroups.getRootElement(), components);

        return components;
    }

    /**
     * Retrieves the <code>Component</code> instances from the node for building the nested
     * components list
     *
     * @param node - node to pull components from
     * @param components - list to add components to
     */
    protected void addNodeComponents(Node<Group, MessageField> node, List<Component> components) {
        if (node != null) {
            components.add(node.getNodeLabel());
            components.add(node.getData());

            for (Node<Group, MessageField> nodeChild : node.getChildren()) {
                addNodeComponents(nodeChild, components);
            }
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public BindingInfo getBindingInfo() {
        return bindingInfo;
    }

    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * @return the defaultNodePrototype
     */
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
     * @return the nodeProtypeMap
     */
    public Map<Class<?>, NodePrototype> getNodeProtypeMap() {
        return this.nodeProtypeMap;
    }

    /**
     * @param nodeProtypeMap the nodeProtypeMap to set
     */
    public void setNodeProtypeMap(Map<Class<?>, NodePrototype> nodeProtypeMap) {
        this.nodeProtypeMap = nodeProtypeMap;
    }

    public Tree<Group, MessageField> getTreeGroups() {
        return treeGroups;
    }

    public void setTreeGroups(Tree<Group, MessageField> treeGroups) {
        this.treeGroups = treeGroups;
    }

    public TreeWidget getTreeWidget() {
        return treeWidget;
    }

    public void setTreeWidget(TreeWidget treeWidget) {
        this.treeWidget = treeWidget;
    }

    public static class NodePrototype implements Serializable {

        private static final long serialVersionUID = 1L;

        MessageField labelPrototype;
        Group dataGroupPrototype;

        /**
         * @param labelPrototype the labelPrototype to set
         */
        public void setLabelPrototype(MessageField labelPrototype) {
            this.labelPrototype = labelPrototype;
        }

        /**
         * @return the labelPrototype
         */
        public MessageField getLabelPrototype() {
            return this.labelPrototype;
        }

        /**
         * @param dataGroupPrototype the dataGroupPrototype to set
         */
        public void setDataGroupPrototype(Group dataGroupPrototype) {
            this.dataGroupPrototype = dataGroupPrototype;
        }

        /**
         * @return the dataGroupPrototype
         */
        public Group getDataGroupPrototype() {
            return this.dataGroupPrototype;
        }
    }
}