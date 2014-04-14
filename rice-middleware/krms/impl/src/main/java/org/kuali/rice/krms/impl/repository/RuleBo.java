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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.impl.ui.CompoundOpCodeNode;
import org.kuali.rice.krms.impl.ui.CompoundPropositionEditNode;
import org.kuali.rice.krms.impl.ui.RuleTreeNode;
import org.kuali.rice.krms.impl.ui.SimplePropositionEditNode;
import org.kuali.rice.krms.impl.ui.SimplePropositionNode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "KRMS_RULE_T")
public class RuleBo implements RuleDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String RULE_SEQ_NAME = "KRMS_RULE_S";
    static final RepositoryBoIncrementer ruleIdIncrementer = new RepositoryBoIncrementer(RULE_SEQ_NAME);
    static final RepositoryBoIncrementer actionIdIncrementer = new RepositoryBoIncrementer("KRMS_ACTN_S");
    static final RepositoryBoIncrementer ruleAttributeIdIncrementer = new RepositoryBoIncrementer("KRMS_RULE_ATTR_S");
    static final RepositoryBoIncrementer actionAttributeIdIncrementer = new RepositoryBoIncrementer("KRMS_ACTN_ATTR_S");

    @PortableSequenceGenerator(name = RULE_SEQ_NAME)
    @GeneratedValue(generator = RULE_SEQ_NAME)
    @Id
    @Column(name = "RULE_ID")
    private String id;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "NM")
    private String name;

    @Column(name = "TYP_ID", nullable = true)
    private String typeId;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToOne(targetEntity = PropositionBo.class, fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "PROP_ID", referencedColumnName = "PROP_ID", insertable = true, updatable = true)
    private PropositionBo proposition;

    @OneToMany(mappedBy = "rule",
            cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST })
    private List<ActionBo> actions;

    @OneToMany(targetEntity = RuleAttributeBo.class, fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "RULE_ID", referencedColumnName = "RULE_ID", insertable = true, updatable = true)
    private List<RuleAttributeBo> attributeBos;

    @Transient
    private Tree<RuleTreeNode, String> propositionTree;

    @Transient
    private String propositionSummary;

    @Transient
    private StringBuffer propositionSummaryBuffer;

    @Transient
    private String selectedPropositionId;

    public RuleBo() {
        actions = new ArrayList<ActionBo>();
        attributeBos = new ArrayList<RuleAttributeBo>();
    }

    public PropositionBo getProposition() {
        return proposition;
    }

    public void setProposition(PropositionBo proposition) {
        this.proposition = proposition;
    }

    /**
     * set the typeId.  If the parameter is blank, then this RuleBo's
     * typeId will be set to null
     *
     * @param typeId
     */
    public void setTypeId(String typeId) {
        if (StringUtils.isBlank(typeId)) {
            this.typeId = null;
        } else {
            this.typeId = typeId;
        }
    }

    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>();

        for (RuleAttributeBo attr : attributeBos) {
            DataObjectService dataObjectService = KRADServiceLocator.getDataObjectService();
            dataObjectService.wrap(attr).fetchRelationship("attributeDefinition", false, true);
            attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());
        }

        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributeBos = new ArrayList<RuleAttributeBo>();

        if (!StringUtils.isBlank(this.typeId)) {
            List<KrmsAttributeDefinition> attributeDefinitions = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().findAttributeDefinitionsByType(this.getTypeId());
            Map<String, KrmsAttributeDefinition> attributeDefinitionsByName = new HashMap<String, KrmsAttributeDefinition>();

            if (attributeDefinitions != null) {
                for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
                    attributeDefinitionsByName.put(attributeDefinition.getName(), attributeDefinition);
                }
            }

            for (Map.Entry<String, String> attr : attributes.entrySet()) {
                KrmsAttributeDefinition attributeDefinition = attributeDefinitionsByName.get(attr.getKey());
                RuleAttributeBo attributeBo = new RuleAttributeBo();
                attributeBo.setRuleId(this.getId());
                attributeBo.setValue(attr.getValue());
                attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attributeDefinition));
                attributeBos.add(attributeBo);
            }
        }
    }

    public String getPropositionSummary() {
        if (this.propositionTree == null) {
            this.propositionTree = refreshPropositionTree(false);
        }

        return propositionSummaryBuffer.toString();
    }

    /**
     * This method is used by the RuleEditor to display the proposition in tree form.
     *
     * @return Tree representing a rule proposition.
     */
    public Tree getPropositionTree() {
        if (this.propositionTree == null) {
            this.propositionTree = refreshPropositionTree(false);
        }

        return this.propositionTree;
    }

    public void setPropositionTree(Tree<RuleTreeNode, String> tree) {
        this.propositionTree.equals(tree);
    }

    public Tree refreshPropositionTree(Boolean editMode) {
        Tree myTree = new Tree<RuleTreeNode, String>();

        Node<RuleTreeNode, String> rootNode = new Node<RuleTreeNode, String>();
        myTree.setRootElement(rootNode);

        propositionSummaryBuffer = new StringBuffer();
        PropositionBo prop = this.getProposition();
        if (prop!=null && StringUtils.isBlank(prop.getDescription())) {
            prop.setDescription("");
        }
        buildPropTree(rootNode, prop, editMode);
        this.propositionTree = myTree;
        return myTree;
    }

    /**
     * This method builds a propositionTree recursively walking through the children of the proposition.
     *
     * @param sprout - parent tree node
     * @param prop - PropositionBo for which to make the tree node
     * @param editMode - Boolean determines the node type used to represent the proposition
     * false: create a view only node text control
     * true: create an editable node with multiple controls
     * null:  use the proposition.editMode property to determine the node type
     */
    private void buildPropTree(Node sprout, PropositionBo prop, Boolean editMode) {
        // Depending on the type of proposition (simple/compound), and the editMode,  
        // Create a treeNode of the appropriate type for the node and attach it to the  
        // sprout parameter passed in.  
        // If the prop is a compound proposition, calls itself for each of the compoundComponents  
        if (prop != null) {
            if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(prop.getPropositionTypeCode())) {
                // Simple Proposition  
                // add a node for the description display with a child proposition node  
                Node<RuleTreeNode, String> child = new Node<RuleTreeNode, String>();
                child.setNodeLabel(prop.getDescription());

                if (prop.getEditMode()) {
                    child.setNodeLabel("");
                    child.setNodeType(SimplePropositionEditNode.NODE_TYPE);
                    SimplePropositionEditNode pNode = new SimplePropositionEditNode(prop);
                    child.setData(pNode);
                } else {
                    child.setNodeType(SimplePropositionNode.NODE_TYPE);
                    SimplePropositionNode pNode = new SimplePropositionNode(prop);
                    child.setData(pNode);
                }

                sprout.getChildren().add(child);
                propositionSummaryBuffer.append(prop.getParameterDisplayString());
            } else if (PropositionType.COMPOUND.getCode().equalsIgnoreCase(prop.getPropositionTypeCode())) {
                // Compound Proposition  
                propositionSummaryBuffer.append(" ( ");
                Node<RuleTreeNode, String> aNode = new Node<RuleTreeNode, String>();
                aNode.setNodeLabel(prop.getDescription());

                // editMode has description as an editable field  
                if (prop.getEditMode()) {
                    aNode.setNodeLabel("");
                    aNode.setNodeType("ruleTreeNode compoundNode editNode");
                    CompoundPropositionEditNode pNode = new CompoundPropositionEditNode(prop);
                    aNode.setData(pNode);
                } else {
                    aNode.setNodeType("ruleTreeNode compoundNode");
                    RuleTreeNode pNode = new RuleTreeNode(prop);
                    aNode.setData(pNode);
                }

                sprout.getChildren().add(aNode);
                boolean first = true;
                List<PropositionBo> allMyChildren = prop.getCompoundComponents();
                int compoundSequenceNumber = 0;

                for (PropositionBo child : allMyChildren) {
                    child.setCompoundSequenceNumber((compoundSequenceNumber = ++compoundSequenceNumber));

                    // start with 1
                    // add an opcode node in between each of the children.  
                    if (!first) {
                        addOpCodeNode(aNode, prop);
                    }

                    first = false;
                    // call to build the childs node  
                    buildPropTree(aNode, child, editMode);
                }

                propositionSummaryBuffer.append(" ) ");
            }
        }
    }

    /**
     * This method adds an opCode Node to separate components in a compound proposition.
     *
     * @param currentNode
     * @param prop
     * @return
     */
    private void addOpCodeNode(Node currentNode, PropositionBo prop) {
        String opCodeLabel = "";

        if (LogicalOperator.AND.getCode().equalsIgnoreCase(prop.getCompoundOpCode())) {
            opCodeLabel = "AND";
        } else if (LogicalOperator.OR.getCode().equalsIgnoreCase(prop.getCompoundOpCode())) {
            opCodeLabel = "OR";
        }

        propositionSummaryBuffer.append(" " + opCodeLabel + " ");
        Node<RuleTreeNode, String> aNode = new Node<RuleTreeNode, String>();
        aNode.setNodeLabel("");
        aNode.setNodeType("ruleTreeNode compoundOpCodeNode");
        aNode.setData(new CompoundOpCodeNode(prop));

        currentNode.getChildren().add(aNode);
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static RuleDefinition to(RuleBo bo) {
        if (bo == null) {
            return null;
        }

        return RuleDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static RuleBo from(RuleDefinition im) {
        if (im == null) {
            return null;
        }

        RuleBo bo = new RuleBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.description = im.getDescription();
        bo.typeId = im.getTypeId();
        bo.active = im.isActive();

        if (im.getProposition() != null) {
            PropositionBo propositionBo = PropositionBo.from(im.getProposition());
            bo.proposition = propositionBo;
            propositionBo.setRuleId(im.getId());
        }

        bo.setVersionNumber(im.getVersionNumber());
        bo.actions = new ArrayList<ActionBo>();

        for (ActionDefinition action : im.getActions()) {
            ActionBo actionBo = ActionBo.from(action);
            bo.actions.add(actionBo);
            actionBo.setRule(bo);
        }

        // build the set of agenda attribute BOs  
        List<RuleAttributeBo> attrs = new ArrayList<RuleAttributeBo>();
        // for each converted pair, build an RuleAttributeBo and add it to the set  
        RuleAttributeBo attributeBo;
        for (Map.Entry<String, String> entry : im.getAttributes().entrySet()) {
            KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().getKrmsAttributeBo(entry.getKey(), im.getNamespace());
            attributeBo = new RuleAttributeBo();
            attributeBo.setRuleId(im.getId());
            attributeBo.setAttributeDefinition(attrDefBo);
            attributeBo.setValue(entry.getValue());
            attributeBo.setAttributeDefinition(attrDefBo);
            attrs.add(attributeBo);
        }

        bo.setAttributeBos(attrs);

        return bo;
    }

    public static RuleBo copyRule(RuleBo existing) {
        // create a simple proposition Bo  
        RuleBo newRule = new RuleBo();
        // copy simple fields  
        newRule.setId(ruleIdIncrementer.getNewId());
        newRule.setNamespace(existing.getNamespace());
        newRule.setDescription(existing.getDescription());
        newRule.setTypeId(existing.getTypeId());
        newRule.setActive(true);

        PropositionBo newProp = PropositionBo.copyProposition(existing.getProposition());
        newProp.setRuleId(newRule.getId());
        newRule.setProposition(newProp);

        newRule.setAttributeBos(copyRuleAttributes(existing));
        newRule.setActions(copyRuleActions(existing, newRule.getId()));

        return newRule;
    }

    /**
     * Returns a new copy of this rule with new ids.
     *
     * @param newRuleName name of the copied rule
     * @return RuleBo a copy of the this rule, with new ids, and the given name
     */
    public RuleBo copyRule(String newRuleName) {
        RuleBo copiedRule = RuleBo.copyRule(this);

        // Rule names cannot be the same, the error for being the same name is not displayed to the user, and the document is
        // said to have been successfully submitted.  
        //        copiedRule.setName(rule.getName());  
        copiedRule.setName(newRuleName);

        return copiedRule;
    }

    public static List<RuleAttributeBo> copyRuleAttributes(RuleBo existing) {
        List<RuleAttributeBo> newAttributes = new ArrayList<RuleAttributeBo>();

        for (RuleAttributeBo attr : existing.getAttributeBos()) {
            RuleAttributeBo newAttr = new RuleAttributeBo();
            newAttr.setId(ruleAttributeIdIncrementer.getNewId());
            newAttr.setRuleId(attr.getRuleId());
            newAttr.setAttributeDefinition(attr.getAttributeDefinition());
            newAttr.setValue(attr.getValue());
            newAttributes.add(newAttr);
        }

        return newAttributes;
    }

    public static List<ActionAttributeBo> copyActionAttributes(ActionBo existing) {
        List<ActionAttributeBo> newAttributes = new ArrayList<ActionAttributeBo>();

        for (ActionAttributeBo attr : existing.getAttributeBos()) {
            ActionAttributeBo newAttr = new ActionAttributeBo();
            newAttr.setId(actionAttributeIdIncrementer.getNewId());
            newAttr.setAction(existing);
            newAttr.setAttributeDefinition(attr.getAttributeDefinition());
            newAttr.setValue(attr.getValue());
            newAttributes.add(newAttr);
        }

        return newAttributes;
    }

    public static List<ActionBo> copyRuleActions(RuleBo existing, String ruleId) {
        List<ActionBo> newActionList = new ArrayList<ActionBo>();

        for (ActionBo action : existing.getActions()) {
            ActionBo newAction = new ActionBo();
            newAction.setId(actionIdIncrementer.getNewId());
            newAction.setRule(existing);
            newAction.setDescription(action.getDescription());
            newAction.setName(action.getName());
            newAction.setNamespace(action.getNamespace());
            newAction.setTypeId(action.getTypeId());
            newAction.setSequenceNumber(action.getSequenceNumber());
            newAction.setAttributeBos(copyActionAttributes(action));
            newActionList.add(newAction);
        }

        return newActionList;
    }

    /*
     * This is being done because there is a  major issue with lazy relationships, in ensuring that the relationship is
 	 * still available after the object has been detached, or serialized. For most JPA providers, after serialization
 	 * any lazy relationship that was not instantiated will be broken, and either throw an error when accessed,
 	 * or return null.
 	 */
    private void writeObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        if (proposition != null) {
            proposition.getId();
        }
        stream.defaultWriteObject();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getPropId() {
        if (proposition != null) {
            return proposition.getId();
        }

        return null;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<ActionBo> getActions() {
        return actions;
    }

    public void setActions(List<ActionBo> actions) {
        this.actions = actions;
    }

    public List<RuleAttributeBo> getAttributeBos() {
        return attributeBos;
    }

    public void setAttributeBos(List<RuleAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }

    public void setPropositionSummary(String propositionSummary) {
        this.propositionSummary = propositionSummary;
    }

    public String getSelectedPropositionId() {
        return selectedPropositionId;
    }

    public void setSelectedPropositionId(String selectedPropositionId) {
        this.selectedPropositionId = selectedPropositionId;
    }

}
