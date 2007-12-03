/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.routemodule;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.workflow.routemodule.BaseRouteModule;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.routetemplate.FlexRM;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.ResponsibleParty;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.RuleXmlParser;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * A RouteModule that generates requests for responsibilities statically defined
 * in the config block of the node.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class InlineRequestsRouteModule extends BaseRouteModule {
    private static final Logger LOG = Logger.getLogger(InlineRequestsRouteModule.class);

    /* @see edu.iu.uis.eden.routemodule.RouteModule#findActionRequests(edu.iu.uis.eden.engine.RouteContext)
     */
    public List findActionRequests(RouteContext context) throws Exception {
        String contentFragment = context.getNodeInstance().getRouteNode().getContentFragment();
        // parse with JDOM to reuse RuleXmlParser
        Document doc = XmlHelper.trimSAXXml(new ByteArrayInputStream(contentFragment.getBytes()));
        Element root = doc.getRootElement();
        String xpathExpression = root.getChildText("match");
        if (StringUtils.isBlank(xpathExpression)) {
            throw new RuntimeException("Match xpath expression not specified (should be parse-time exception...)");
        }

        XPath xpath = XPathHelper.newXPath();
        Boolean match = (Boolean) xpath.evaluate(xpathExpression, context.getDocumentContent().getDocument(), XPathConstants.BOOLEAN);
        
        List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
        if (match.booleanValue()) {
            LOG.debug("Expression '" + xpathExpression + "' matched document '" + context.getDocumentContent().getDocContent() + "'");
        } else {
            LOG.debug("Expression '" + xpathExpression + "' did NOT match document '" + context.getDocumentContent().getDocContent() + "'");
            return actionRequests;
        }

        List<RuleResponsibility> responsibilities = new ArrayList<RuleResponsibility>();
        RuleXmlParser parser = new RuleXmlParser();
        FlexRM flexRM = new FlexRM();
        flexRM.setActionRequestFactory(new ActionRequestFactory(context.getDocument(), context.getNodeInstance()));
        // this rule is only used to obtain description, ignoreprevious flag, and the rulebasevalues id, which may be null
        RuleBaseValues fakeRule = new RuleBaseValues();
        fakeRule.setActiveInd(Boolean.TRUE);
        fakeRule.setCurrentInd(Boolean.TRUE);
        fakeRule.setDescription("a fake rule");
        fakeRule.setIgnorePrevious(Boolean.TRUE);
        fakeRule.setRuleBaseValuesId(null);

        for (Object o: root.getChildren("responsibility", XmlConstants.RULE_NAMESPACE)) {
            Element e = (Element) o;
            RuleResponsibility responsibility = parser.parseResponsibility(e, fakeRule, null);
            System.err.println("Responsibility id: " + responsibility.getResponsibilityId());
            responsibility.setResponsibilityId(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID);
            responsibilities.add(responsibility);
        }
        if (responsibilities.size() == 0) {
            throw new RuntimeException("No responsibilities found on node " + context.getNodeInstance().getName());
        }

        flexRM.makeActionRequests(responsibilities, context, fakeRule, context.getDocument(), null, null); 
        actionRequests.addAll(flexRM.getActionRequestFactory().getRequestGraphs());

        return actionRequests;
    }
    
    public ResponsibleParty resolveResponsibilityId(Long responsibilityId) {
        return new FlexRM().resolveResponsibilityId(responsibilityId);
    }
}