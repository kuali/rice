package org.kuali.rice.krms.impl.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableAttributeLookupSettings;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>{@link ActionTypeService} implementation for PeopleFlow actions.  The loaded {@link Action}s will place or extend
 * an attribute in the {@link org.kuali.rice.krms.api.engine.EngineResults} whose key is "peopleFlowSelected" and value
 * is a String of the form (using EBNF-like notation):</p>
 *
 * <pre>    (notification|approval):&lt;peopleFlowId&gt;{,(notification|approval):&lt;peopleFlowId&gt;}</pre>
 *
 * <p>An example value with two people flow actions specified would be:</p>
 *
 * <pre>    "approval:1000,notification:10001"</pre>
 *
 */
public class PeopleFlowActionTypeService implements ActionTypeService {

    /**
     * enum used to specify the action type to be specified in the vended actions
     */
    public enum Type {

        NOTIFICATION, APPROVAL;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    // String constants
    private final String PEOPLE_FLOWS_SELECTED_ATTRIBUTE = "peopleFlowsSelected";
    private static final String ATTRIBUTE_FIELD_NAME = "peopleFlowId";

    private final Type type;

    /**
     * Factory method for getting a {@link PeopleFlowActionTypeService}
     * @param type indicates the type of action that the returned {@link PeopleFlowActionTypeService} will produce
     * @return a {@link PeopleFlowActionTypeService} corresponding to the given {@link Type}.
     */
    public static PeopleFlowActionTypeService getInstance(Type type) {
        return new PeopleFlowActionTypeService(type);
    }

    /**
     * private constructor to enforce use of static factory
     * @param type
     */
    private PeopleFlowActionTypeService(Type type) {
        if (type == null) { throw new IllegalArgumentException("type must not be null"); }
        this.type = type;
    }

    @Override
    public Action loadAction(ActionDefinition actionDefinition) {
        if (actionDefinition == null) throw new IllegalArgumentException("actionDefinition must not be null");

        if (actionDefinition.getAttributes() == null ||
                !actionDefinition.getAttributes().containsKey(ATTRIBUTE_FIELD_NAME)) {

            throw new IllegalStateException("actionDefinition does not contain an " +
                    ATTRIBUTE_FIELD_NAME + " attribute");
        }

        String peopleFlowId = actionDefinition.getAttributes().get(ATTRIBUTE_FIELD_NAME);

        if (StringUtils.isBlank(peopleFlowId)) {
            throw new IllegalArgumentException(ATTRIBUTE_FIELD_NAME + " attribute must not be null or blank");
        }

        // if the ActionDefinition is valid, constructing the PeopleFlowAction is cake

        return new PeopleFlowAction(type, peopleFlowId);
    }

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) {

        // TODO: real params here.  At the time this was written, lookups didn't exist for PeopleFlows yet.
        RemotableQuickFinder.Builder quickFinderBuilder =
                RemotableQuickFinder.Builder.create("http://TODO.kuali.org/TODO/",
                        "org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo");
        // TODO: field conversions, etc
//        quickFinderBuilder.setFieldConversions();

        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(40);

        RemotableAttributeLookupSettings.Builder lookupSettingsBuilder = RemotableAttributeLookupSettings.Builder.create();
        lookupSettingsBuilder.setCaseSensitive(true);
        lookupSettingsBuilder.setInCriteria(true);
        lookupSettingsBuilder.setInResults(true);
        lookupSettingsBuilder.setRanged(false);

        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(ATTRIBUTE_FIELD_NAME);
        builder.setAttributeLookupSettings(lookupSettingsBuilder);
        builder.setRequired(true);
        builder.setDataType(DataType.STRING);
        builder.setControl(controlBuilder);
        builder.setLongLabel("PeopleFlow ID");
        builder.setShortLabel("PeopleFlow ID");
        builder.setMinLength(1);
        builder.setMaxLength(40);
        builder.setWidgets(Collections.<RemotableAbstractWidget.Builder>singletonList(quickFinderBuilder));

        return Collections.singletonList(builder.build());
    }

    @Override
    public List<RemotableAttributeError> validateAttributes(

            @WebParam(name = "krmsTypeId") String krmsTypeId,

            @WebParam(name = "attributes")
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            Map<String, String> attributes

    ) throws RiceIllegalArgumentException {

        RemotableAttributeError.Builder errorBuilder =
                RemotableAttributeError.Builder.create(ATTRIBUTE_FIELD_NAME);

        if (attributes != null && attributes.containsKey(ATTRIBUTE_FIELD_NAME)) {
            PeopleFlowDefinition peopleFlowDefinition =
                    KewApiServiceLocator.getPeopleFlowService().getPeopleFlow(attributes.get(ATTRIBUTE_FIELD_NAME));
            if (peopleFlowDefinition == null) {

                errorBuilder.addErrors("The " + ATTRIBUTE_FIELD_NAME +
                        " must be a valid ID for an existing PeopleFlow");

            }
        } else {
            errorBuilder.addErrors(ATTRIBUTE_FIELD_NAME + " is required");
        }

        return Collections.singletonList(errorBuilder.build());
    }


    @Override
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "krmsTypeId") String krmsTypeId, @WebParam(name = "newAttributes") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> oldAttributes) throws RiceIllegalArgumentException {
        return validateAttributes(krmsTypeId, newAttributes);
    }

    private class PeopleFlowAction implements Action {

        private final Type type;
        private final String peopleFlowId;

        private PeopleFlowAction(Type type, String peopleFlowId) {

            if (type == null) throw new IllegalArgumentException("type must not be null");
            if (StringUtils.isBlank(peopleFlowId)) throw new IllegalArgumentException("peopleFlowId must not be null");

            this.type = type;
            this.peopleFlowId = peopleFlowId;
        }

        @Override
        public void execute(ExecutionEnvironment environment) {
            // create or extend an existing attribute on the EngineResults to communicate the selected PeopleFlow and
            // action

            Object value = environment.getEngineResults().getAttribute(PEOPLE_FLOWS_SELECTED_ATTRIBUTE);
            StringBuilder selectedAttributesStringBuilder = new StringBuilder();

            if (value != null) {
                // assume the value is what we think it is
                selectedAttributesStringBuilder.append(value.toString());
                // we need a comma after the initial value
                selectedAttributesStringBuilder.append(",");
            }

            // add our people flow action to the string using our convention
            selectedAttributesStringBuilder.append(type.toString());
            selectedAttributesStringBuilder.append(":");
            selectedAttributesStringBuilder.append(peopleFlowId);

            // set our attribute on the engine results
            environment.getEngineResults().setAttribute(
                    PEOPLE_FLOWS_SELECTED_ATTRIBUTE, selectedAttributesStringBuilder.toString()
            );
        }

        @Override
        public void executeSimulation(ExecutionEnvironment environment) {
            // our action doesn't need special handling during simulations
            execute(environment);
        }
    }
}
