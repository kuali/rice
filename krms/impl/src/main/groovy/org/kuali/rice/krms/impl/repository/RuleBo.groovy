/*
* Copyright 2011 The Kuali Foundation
*
* Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krms.impl.repository;


import java.util.Map.Entry
import org.kuali.rice.core.api.util.tree.Node
import org.kuali.rice.core.api.util.tree.Tree
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.LogicalOperator
import org.kuali.rice.krms.api.repository.action.ActionDefinition
import org.kuali.rice.krms.api.repository.proposition.PropositionType
import org.kuali.rice.krms.api.repository.rule.RuleDefinition
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract
import org.kuali.rice.krms.impl.ui.CompoundOpCodeNode
import org.kuali.rice.krms.impl.ui.RuleTreeNode
import org.kuali.rice.krms.impl.ui.SimplePropositionNode
import org.kuali.rice.krms.impl.ui.SimplePropositionEditNode
import org.kuali.rice.krms.impl.ui.CompoundPropositionEditNode

public class RuleBo extends PersistableBusinessObjectBase implements RuleDefinitionContract {
   
   String id;
   String namespace;
   String description;
   String name;
   String typeId;
   String propId;

   PropositionBo proposition;
   List<ActionBo> actions;
   List<RuleAttributeBo> attributeBos;
   //def List<PropositionBo> allChildPropositions
   
   // for Rule editor display
   Tree<RuleTreeNode, String> propositionTree;
   
   // for rule editor display
   String propositionSummary;
   private StringBuffer propositionSummaryBuffer;
   String selectedPropositionId;

   public RuleBo() {
       actions = new ArrayList<ActionBo>();
       attributeBos = new ArrayList<RuleAttributeBo>();
   }

   public PropositionBo getProposition(){
       return proposition;
   }
   public void setProposition(PropositionBo proposition){
       if (proposition != null) {
           propId = proposition.getId();
       } else {
           propId = null;
       }
       this.proposition = proposition;
   }
   
   public Map<String, String> getAttributes() {
       HashMap<String, String> attributes = new HashMap<String, String>();
       for (RuleAttributeBo attr : attributeBos) {
           attributes.put( attr.getAttributeDefinition().getName(), attr.getValue() );
       }
       return attributes;
   }
   
   public String getPropositionSummary(){
       return propositionSummaryBuffer.toString();
   }
   
   /**
    * This method is used by the RuleEditor to display the proposition in tree form.
    *
    * @return Tree representing a rule proposition.
    */
    public Tree getPropositionTree() {
        if (this.propositionTree == null){
            this.propositionTree = refreshPropositionTree(false);
        }

        return this.propositionTree;
    }
    public void setPropositionTree(Tree<RuleTreeNode, String> tree) {
        this.propositionTree == tree;
       }

   public Tree refreshPropositionTree(boolean editMode){
       Tree myTree = new Tree<RuleTreeNode, String>();

       Node<RuleTreeNode, String> rootNode = new Node<RuleTreeNode, String>();
       myTree.setRootElement(rootNode);

       propositionSummaryBuffer = new StringBuffer();
       PropositionBo prop = this.getProposition();
       buildPropTree( rootNode, prop, editMode );
       this.propositionTree = myTree;
       return myTree;
   }

   private void buildPropTree( Node sprout, PropositionBo prop, boolean editMode){
       // This is a work in progress
       // will need a recursive function to walk the tree in the compound proposition
       if (prop != null) {
           if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(prop.getPropositionTypeCode())){
               // Simple Proposition
               // add a node for the description display with a child proposition node
               Node<RuleTreeNode, String> child = new Node<RuleTreeNode, String>();
               child.setNodeLabel(prop.getDescription());
               if (prop.getEditMode()){
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
               
//               Node<RuleTreeNode, String> grandChild = new Node<RuleTreeNode, String>();
//               SimplePropositionNode pNode = new SimplePropositionNode(prop);
//               grandChild.setNodeLabel(pNode.getParameterDisplayString());
//               grandChild.setNodeType("ruleTreeNode simplePropositionParameterNode");
//               grandChild.setData(pNode);
//               child.getChildren().add(grandChild);
               
               propositionSummaryBuffer.append(prop.getParameterDisplayString())
           }
           else if (PropositionType.COMPOUND.getCode().equalsIgnoreCase(prop.getPropositionTypeCode())){
               // Compound Proposition
               propositionSummaryBuffer.append(" ( ");
               Node<RuleTreeNode, String> aNode = new Node<RuleTreeNode, String>();
               aNode.setNodeLabel(prop.getDescription());
               if (prop.getEditMode()){
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
               List <PropositionBo> allMyChildren = prop.getCompoundComponents();
               for (PropositionBo child : allMyChildren){
                   if (!first){
                       addOpCodeNode(aNode, prop);
                   }
                   first = false;
                   buildPropTree(aNode, child, editMode);
               }
               propositionSummaryBuffer.append(" ) ");
           }
       }
   }

   /**
    * 
    * This method adds an opCode Node to separate components in a compound proposition.
    * 
    * @param currentNode
    * @param prop
    * @return
    */
   private void addOpCodeNode(Node currentNode, PropositionBo prop){
       String opCodeLabel = "";
       
       if (LogicalOperator.AND.getCode().equalsIgnoreCase(prop.getCompoundOpCode())){
           opCodeLabel = "AND";
       } else if (LogicalOperator.OR.getCode().equalsIgnoreCase(prop.getCompoundOpCode())){
           opCodeLabel = "OR";
       }
       propositionSummaryBuffer.append(" "+opCodeLabel+" ");
       Node<RuleTreeNode, String> aNode = new Node<RuleTreeNode, String>();
       aNode.setNodeLabel("");
       aNode.setNodeType("ruleTreeNode compoundOpCodeNode");
       aNode.setData(new CompoundOpCodeNode(prop));
       currentNode.getChildren().add(aNode);
   }
   
   
   /**
    * 
   * Converts a mutable bo to it's immutable counterpart
   * @param bo the mutable business object
   * @return the immutable object
   */
  static RuleDefinition to(RuleBo bo) {
      if (bo == null) { return null; }
      return org.kuali.rice.krms.api.repository.rule.RuleDefinition.Builder.create(bo).build();
  }

  /**
   * Converts a immutable object to it's mutable bo counterpart
   * @param im immutable object
   * @return the mutable bo
   */
  static RuleBo from(RuleDefinition im) {
      if (im == null) { return null; }

      RuleBo bo = new RuleBo();
      bo.id = im.getId();
      bo.namespace = im.getNamespace();
      bo.name = im.getName();
      bo.description = im.getDescription();
      bo.typeId = im.getTypeId();
      bo.propId = im.getPropId();
      bo.proposition = PropositionBo.from(im.getProposition());
      bo.versionNumber = im.getVersionNumber();
      
      bo.actions = new ArrayList<ActionBo>();
      for (ActionDefinition action : im.getActions()){
          bo.actions.add( ActionBo.from(action) );
      }

      // build the set of agenda attribute BOs
      List<RuleAttributeBo> attrs = new ArrayList<RuleAttributeBo>();

      // for each converted pair, build an RuleAttributeBo and add it to the set
      RuleAttributeBo attributeBo;
      for (Entry<String,String> entry  : im.getAttributes().entrySet()){
          KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator
                  .getKrmsAttributeDefinitionService()
                  .getKrmsAttributeBo(entry.getKey(), im.getNamespace());
          attributeBo = new RuleAttributeBo();
          attributeBo.setRuleId( im.getId() );
          attributeBo.setAttributeDefinitionId( attrDefBo.getId() );
          attributeBo.setValue( entry.getValue() );
          attributeBo.setAttributeDefinition( attrDefBo );
          attrs.add( attributeBo );
      }
      bo.setAttributeBos(attrs);

      return bo;
  }


}