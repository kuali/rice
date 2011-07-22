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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;
import org.kuali.rice.krms.impl.ui.RuleTreeNode;
import org.kuali.rice.krms.impl.ui.RuleTreeSimplePropositionParameterNode;


public class RuleBo extends PersistableBusinessObjectBase implements RuleDefinitionContract {
   
   def String id;
   def String namespace;
   def String description;
   def String name;
   def String typeId;
   def String propId;

   def PropositionBo proposition;
   def List<ActionBo> actions;
   def List<RuleAttributeBo> attributeBos;
   //def List<PropositionBo> allChildPropositions
   def Tree<RuleTreeNode, String> propositionTree;
   
   public PropositionBo getProposition(){
       return proposition;
   }
   public void setProposition(PropositionBo proposition){
       this.proposition = proposition;
   }
   
   public Map<String, String> getAttributes() {
       HashMap<String, String> attributes = new HashMap<String, String>();
       for (RuleAttributeBo attr : attributeBos) {
           attributes.put( attr.getAttributeDefinition().getName(), attr.getValue() );
       }
       return attributes;
   }
   
   /**
    * This method is used by the RuleEditor to display the proposition in tree form.
    *
    * @return Tree representing a rule proposition.
    */
   public Tree<? extends RuleTreeNode, String> getPropositionTree() {
       Tree<RuleTreeNode, String> propositionTree = new Tree<RuleTreeNode, String>();

       Node<RuleTreeNode, String> rootNode = new Node<RuleTreeNode, String>();
       propositionTree.setRootElement(rootNode);
       
       // This is a work in progress
       // will need a recursive function to walk the tree in the compound proposition
       if (proposition != null) {
           if (proposition.getPropositionTypeCode().equalsIgnoreCase(PropositionType.SIMPLE.getCode())){
               // Simple Proposition
               // add a node for the description display with a child proposition node
               Node<RuleTreeNode, String> child = new Node<RuleTreeNode, String>();
               child.setNodeLabel(proposition.getDescription());
               child.setNodeType("ruleNode");
               child.setData(new RuleTreeNode(proposition));
               rootNode.getChildren().add(child);
               
               Node<RuleTreeNode, String> grandChild = new Node<RuleTreeNode, String>();
               RuleTreeSimplePropositionParameterNode pNode = new RuleTreeSimplePropositionParameterNode(proposition);
               grandChild.setNodeLabel(pNode.getParameterDisplayString());
               grandChild.setNodeType("simplePropositionParameterNode");
               grandChild.setData(pNode);
               child.getChildren().add(grandChild);
           }
           else if (proposition.getPropositionTypeCode().equalsIgnoreCase(PropositionType.COMPOUND.getCode())){
               // Compound Proposition
               // TODO: implement this!!
           }
       }
       
       return propositionTree;
   }
   
   /**
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