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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.inquiry.InquirableImpl;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.SpaceField;
import org.kuali.rice.krad.uif.layout.GridLayoutManager;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;

/**
 * This class is responsible for building the recursive components on the Rule Inquiry View.
 */
public class RuleStudentInquiryViewHelperServiceImpl extends InquirableImpl {

    private static int ID = 1;

    private static final String PROPOSITION_GROUP_ID = "propositionGroup";

    private static final String PROPOSITION_ID = "proposition";

    private enum Operator {
        OR("|"), AND("&");

        private String code;

        Operator(String code) {
            this.code = code;
        }

        private static Operator fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (Operator operator : values()) {
                if (operator.code.equals(code)) {
                    return operator;
                }
            }
            throw new IllegalArgumentException("Failed to locate the Operator with the given code: " + code);
        }
    }



    @Override
    public void addCustomContainerComponents(ViewModel model, Container container) {
        if (PROPOSITION_GROUP_ID.equals(container.getId())) {
            RuleBo ruleDataObj = (RuleBo)((InquiryForm)model).getDataObject();
            PropositionBo proposition = ruleDataObj.getProposition();
            if (proposition != null) {
                if (PropositionType.COMPOUND.getCode().equals(proposition.getPropositionTypeCode())) {
                    List<Component> groups = new ArrayList<Component>();
                    handleCompoundPropositions(groups, proposition);
                    container.setItems(groups);
                } else {
                    Message simplePropName = ComponentFactory.getMessage();
                    simplePropName.setId(PROPOSITION_ID + "_" + ID++);
                    simplePropName.setMessageText(proposition.getDescription());
                    List<Message> simpleProps = new ArrayList<Message>();
                    simpleProps.add(simplePropName);
                    container.setItems(simpleProps);
                }
            }
        }
    }

    private void handleCompoundPropositions(List<Component> components, PropositionBo proposition) {
        Group compoundGroup = getPropositionGroup();
        compoundGroup.setId(String.valueOf(PROPOSITION_GROUP_ID + "_" + ID++));
        ((GridLayoutManager)compoundGroup.getLayoutManager()).setNumberOfColumns(2);

        List<Component> componentItems = new ArrayList<Component>();

        //Heading
        Message propositionName = ComponentFactory.getMessage();
        propositionName.setId(PROPOSITION_ID + "_" + ID++);
        propositionName.setMessageText(proposition.getDescription());

        componentItems.add(propositionName);

        //Space (for layout purposes)
        SpaceField spaceField1 = ComponentFactory.getSpaceField();
        spaceField1.setId("space" + "_" + ID++);
        componentItems.add(spaceField1);

        //Space (for layout purposes)
        SpaceField spaceField2 = ComponentFactory.getSpaceField();
        spaceField2.setId("space" + "_" + ID++);
        componentItems.add(spaceField2);

        if (proposition.getCompoundComponents() != null) {
            int loopCounter = 0;
            for (PropositionBo nestedProposition : proposition.getCompoundComponents()) {
                if (loopCounter != 0) {

                    //Space (for layout purposes)
                    SpaceField spaceField3 = ComponentFactory.getSpaceField();
                    spaceField3.setId("space" + "_" + ID++);
                    componentItems.add(spaceField3);

                    Message operator = ComponentFactory.getMessage();
                    operator.setId(PROPOSITION_ID + "_" + ID++);
                    operator.setMessageText(Operator.fromCode(proposition.getCompoundOpCode()).toString());
                    componentItems.add(operator);

                    //Space (for layout purposes)
                    SpaceField spaceField4 = ComponentFactory.getSpaceField();
                    spaceField4.setId("space" + "_" + ID++);
                    componentItems.add(spaceField4);
                }
                if (PropositionType.COMPOUND.getCode().equals(nestedProposition.getPropositionTypeCode())) {
                    handleCompoundPropositions(components, nestedProposition);
                } else {
                    Message simplePropName = ComponentFactory.getMessage();
                    simplePropName.setId(PROPOSITION_ID + "_" + ID++);
                    simplePropName.setMessageText(nestedProposition.getDescription());

                    componentItems.add(simplePropName);
                }
                loopCounter++;
            }
        }
        compoundGroup.setItems(componentItems);

        components.add(compoundGroup);
    }

    private static Group getPropositionGroup() {
        Group group = (Group) ComponentFactory.getGroupGridBodyOnly();
        group.getDisclosure().setId(String.valueOf(ID++));
        return group;
    }

}
