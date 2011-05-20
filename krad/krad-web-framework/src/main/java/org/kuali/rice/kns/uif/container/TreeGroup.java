package org.kuali.rice.kns.uif.container;

import org.kuali.rice.core.util.Node;
import org.kuali.rice.core.util.Tree;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.MessageField;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.uif.widget.TreeWidget;

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

    private Group dataGroupPrototype;

    private MessageField nodeLabelPrototype;
    private Map<String, String> nodeTypeStyleClasses;

    private Tree<Group, MessageField> treeGroups;

    private TreeWidget treeWidget;

    public TreeGroup() {
        super();

        nodeTypeStyleClasses = new HashMap<String, String>();
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

        view.getViewHelperService().performComponentInitialization(view, nodeLabelPrototype);
        view.getViewHelperService().performComponentInitialization(view, dataGroupPrototype);
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
     *  The component tree group mirrors the tree data structure on the model. For each node of
     *  the data structure, a corresponding <code>MessageField</code>  will be created for the node
     *  label, and a <code>Group</code> component for the node data. These are placed into a new
     *  node for the component tree. After the tree is built it is set as a property on the tree group
     *  to be read by the renderer
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

    protected Node<Group, MessageField> buildTreeNode(Node<Object, String> nodeData, String bindingPrefix, int nodeCounter) {
        Node<Group, MessageField> node = new Node<Group, MessageField>();

        String idSuffix = "_n" + nodeCounter;

        MessageField messageField = ComponentUtils.copy(this.nodeLabelPrototype, idSuffix);
        ComponentUtils.pushObjectToContext(messageField, UifConstants.ContextVariableNames.NODE, nodeData);
        messageField.setMessageText(nodeData.getNodeLabel());
        node.setNodeLabel(messageField);

        Group nodeGroup = ComponentUtils.copyComponent(this.dataGroupPrototype, bindingPrefix + ".data", idSuffix);
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

    public Group getDataGroupPrototype() {
        return dataGroupPrototype;
    }

    public void setDataGroupPrototype(Group dataGroupPrototype) {
        this.dataGroupPrototype = dataGroupPrototype;
    }

    public MessageField getNodeLabelPrototype() {
        return nodeLabelPrototype;
    }

    public void setNodeLabelPrototype(MessageField nodeLabelPrototype) {
        this.nodeLabelPrototype = nodeLabelPrototype;
    }

    public Map<String, String> getNodeTypeStyleClasses() {
        return nodeTypeStyleClasses;
    }

    public void setNodeTypeStyleClasses(Map<String, String> nodeTypeStyleClasses) {
        this.nodeTypeStyleClasses = nodeTypeStyleClasses;
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
}
