package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.xml.XmlJotter;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNodeUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.List;

public class PeopleFlowRouteModule implements RouteModule {

    private static final String PEOPLE_FLOW_ITERATION_KEY = "peopleFlowIteration";
    private static final String PEOPLE_FLOW_PROPERTY = "peopleFlow";
    private static final JAXBContext jaxbContext;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(PeopleFlowConfig.class);
        } catch (JAXBException e) {
            throw new RiceRuntimeException("Failed to initialize JAXB!", e);
        }
    }

    private PeopleFlowService peopleFlowService;
    private PeopleFlowRequestGenerator peopleFlowRequestGenerator;

    @Override
    public List<ActionRequestValue> findActionRequests(RouteContext context) throws Exception {
        int iteration = incrementIteration(context);
        List<PeopleFlowConfig> configurations = parsePeopleFlowConfiguration(context);
        List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
        int index = 0;
        for (PeopleFlowConfig configuration : configurations) {
            if (index++ != iteration) {
                continue;
            }
            PeopleFlowDefinition peopleFlow = loadPeopleFlow(configuration);
            actionRequests.addAll(getPeopleFlowRequestGenerator().generateRequests(context, peopleFlow, configuration.actionRequestType));
        }
        return actionRequests;
    }

    @Override
    public boolean isMoreRequestsAvailable(RouteContext context) {
        Integer currentIteration = getCurrentIteration(context);
        if (currentIteration == null) {
            return false;
        }
        List<PeopleFlowConfig> configurations = parsePeopleFlowConfiguration(context);
        return currentIteration.intValue() < configurations.size();
    }

    @Override
    public ResponsibleParty resolveResponsibilityId(String responsibilityId) throws WorkflowException {
        return null;
    }

    protected List<PeopleFlowConfig> parsePeopleFlowConfiguration(RouteContext context) {
        List<Element> peopleFlowElements = RouteNodeUtils.getCustomRouteNodeElements(
                context.getNodeInstance().getRouteNode(), PEOPLE_FLOW_PROPERTY);
        List<PeopleFlowConfig> configs = new ArrayList<PeopleFlowConfig>();
        for (Element peopleFlowElement : peopleFlowElements) {
            try {
                PeopleFlowConfig config = (PeopleFlowConfig)jaxbContext.createUnmarshaller().unmarshal(peopleFlowElement);
                if (config.actionRequestType == null) {
                    // default action request type to approve
                    config.actionRequestType = ActionRequestType.APPROVE;
                }
                if (config == null) {
                    throw new IllegalStateException("People flow configuration element did not properly unmarshall from XML: " + XmlJotter.jotNode(peopleFlowElement));
                }
                configs.add(config);
            } catch (JAXBException e) {
                throw new RiceRuntimeException("Failed to unmarshall people flow configuration from route node.", e);
            }
        }
        return configs;
    }

    protected PeopleFlowDefinition loadPeopleFlow(PeopleFlowConfig configuration) {
        String namespaceCode = configuration.name.namespaceCode;
        String name = configuration.name.name;
        PeopleFlowDefinition peopleFlow = getPeopleFlowService().getPeopleFlowByName(configuration.name.namespaceCode, configuration.name.name);
        if (peopleFlow == null) {
            throw new ConfigurationException("Failed to locate a people flow with the given namespaceCode of '" + namespaceCode + "' and name of '" + name + "'");
        }
        return peopleFlow;
    }

    protected int incrementIteration(RouteContext context) {
        int nextIteration = 0;
        NodeState nodeState = context.getNodeInstance().getNodeState(PEOPLE_FLOW_ITERATION_KEY);
        if (nodeState == null) {
            nodeState = new NodeState();
            nodeState.setNodeInstance(context.getNodeInstance());
            nodeState.setKey(PEOPLE_FLOW_ITERATION_KEY);
            context.getNodeInstance().addNodeState(nodeState);
        } else {
            int currentIteration = Integer.parseInt(nodeState.getValue());
            nextIteration = currentIteration + 1;
        }
        nodeState.setValue(Integer.toString(nextIteration));
        KEWServiceLocator.getRouteNodeService().save(nodeState);
        return nextIteration;
    }

    protected Integer getCurrentIteration(RouteContext context) {
        NodeState nodeState = context.getNodeInstance().getNodeState(PEOPLE_FLOW_ITERATION_KEY);
        if (nodeState == null) {
            return null;
        }
        return Integer.valueOf(nodeState.getValue());
    }

    public PeopleFlowService getPeopleFlowService() {
        return peopleFlowService;
    }

    public void setPeopleFlowService(PeopleFlowService peopleFlowService) {
        this.peopleFlowService = peopleFlowService;
    }

    public PeopleFlowRequestGenerator getPeopleFlowRequestGenerator() {
        return peopleFlowRequestGenerator;
    }

    public void setPeopleFlowRequestGenerator(PeopleFlowRequestGenerator peopleFlowRequestGenerator) {
        this.peopleFlowRequestGenerator = peopleFlowRequestGenerator;
    }

    @XmlRootElement(name = "peopleFlow")
    private static class PeopleFlowConfig {

        @XmlElement(name = "actionRequestType")
        ActionRequestType actionRequestType;

        @XmlElement(name = "name") NameConfig name;
        
    }

    private static class NameConfig {

        @XmlAttribute(name = "namespace")
        String namespaceCode;

        @XmlValue
        String name;

    }

}
