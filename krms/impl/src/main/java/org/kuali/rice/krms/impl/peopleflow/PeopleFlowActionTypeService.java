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
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.springframework.orm.ObjectRetrievalFailureException;

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
 * <pre>    "approval:1000,notification:1001"</pre>
 *
 */
public class PeopleFlowActionTypeService implements ActionTypeService {

    /**
     * enum used to specify the action type to be specified in the vended actions.
     */
    public enum Type {

        /**
         * use this flag with the static factory to get a {@link PeopleFlowActionTypeService} that creates
         * notification actions.
         */
        NOTIFICATION,

        /**
         * use this flag with the static factory to get a {@link PeopleFlowActionTypeService} that creates
         * approval actions.
         */
        APPROVAL;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * for each type, check the input with the lowercase version of the type name, and returns any match.
         * @param s the type to retrieve
         * @return the type, or null if a match is not found.
         */
        public static Type fromString(String s) {
            for (Type type : Type.values()) {
                if (type.toString().equals(s.toLowerCase())) {
                    return type;
                }
            }
            return null;
        }
    }

    // String constants
    static final String PEOPLE_FLOWS_SELECTED_ATTRIBUTE = "peopleFlowsSelected";
    static final String ATTRIBUTE_FIELD_NAME = "peopleFlowId";

    private final Type type;

    private PeopleFlowService peopleFlowService;

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
        if (actionDefinition == null) { throw new RiceIllegalArgumentException("actionDefinition must not be null"); }

        if (actionDefinition.getAttributes() == null ||
                !actionDefinition.getAttributes().containsKey(ATTRIBUTE_FIELD_NAME)) {

            throw new RiceIllegalArgumentException("actionDefinition does not contain an " +
                    ATTRIBUTE_FIELD_NAME + " attribute");
        }

        String peopleFlowId = actionDefinition.getAttributes().get(ATTRIBUTE_FIELD_NAME);

        if (StringUtils.isBlank(peopleFlowId)) {
            throw new RiceIllegalArgumentException(ATTRIBUTE_FIELD_NAME + " attribute must not be null or blank");
        }

        // if the ActionDefinition is valid, constructing the PeopleFlowAction is cake

        return new PeopleFlowAction(type, peopleFlowId);
    }

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) {

        validateNonBlankKrmsTypeId(krmsTypeId);

        // TODO: real params here.  At the time this was written, lookups didn't exist for PeopleFlows yet.
        RemotableQuickFinder.Builder quickFinderBuilder =
                RemotableQuickFinder.Builder.create("http://TODO.kuali.org/TODO/",
                        "org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo");
        // TODO: field conversions, etc
//        quickFinderBuilder.setFieldConversions();

        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(Integer.valueOf(40));

        RemotableAttributeLookupSettings.Builder lookupSettingsBuilder = RemotableAttributeLookupSettings.Builder.create();
        lookupSettingsBuilder.setCaseSensitive(Boolean.TRUE);
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
        builder.setMinLength(Integer.valueOf(1));
        builder.setMaxLength(Integer.valueOf(40));
        builder.setWidgets(Collections.<RemotableAbstractWidget.Builder>singletonList(quickFinderBuilder));

        return Collections.singletonList(builder.build());
    }

    private void validateNonBlankKrmsTypeId(String krmsTypeId) {
        if (StringUtils.isEmpty(krmsTypeId)) {
            throw new RiceIllegalArgumentException("krmsTypeId may not be null or blank");
        }
    }

    @Override
    public List<RemotableAttributeError> validateAttributes(

            @WebParam(name = "krmsTypeId") String krmsTypeId,

            @WebParam(name = "attributes")
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            Map<String, String> attributes

    ) throws RiceIllegalArgumentException {

        List<RemotableAttributeError> results = null;

        validateNonBlankKrmsTypeId(krmsTypeId);
        if (attributes == null) { throw new RiceIllegalArgumentException("attributes must not be null"); }

        RemotableAttributeError.Builder errorBuilder =
                RemotableAttributeError.Builder.create(ATTRIBUTE_FIELD_NAME);

        if (attributes != null && attributes.containsKey(ATTRIBUTE_FIELD_NAME)) {
            PeopleFlowDefinition peopleFlowDefinition = null;

            try {
                peopleFlowDefinition = getPeopleFlowService().getPeopleFlow(attributes.get(ATTRIBUTE_FIELD_NAME));
            } catch (ObjectRetrievalFailureException e) {
                // that means the key was invalid to OJB/Spring.
                // That's not cause for general panic, so we'll swallow it.
            } catch (IllegalArgumentException e) {
                // that means the key was invalid to our JPA provider.
                // That's not cause for general panic, so we'll swallow it.
            }

            if (peopleFlowDefinition == null) {

                errorBuilder.addErrors("The " + ATTRIBUTE_FIELD_NAME +
                        " must be a valid ID for an existing PeopleFlow");

            }
        } else {
            errorBuilder.addErrors(ATTRIBUTE_FIELD_NAME + " is required");
        }

        if (errorBuilder.getErrors().size() > 0) {
            results = Collections.singletonList(errorBuilder.build());
        } else {
            results = Collections.emptyList();
        }

        return results;
    }


    @Override
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "krmsTypeId") String krmsTypeId, @WebParam(name = "newAttributes") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> oldAttributes) throws RiceIllegalArgumentException {

        if (oldAttributes == null) { throw new RiceIllegalArgumentException("oldAttributes must not be null"); }

        return validateAttributes(krmsTypeId, newAttributes);
    }

    /**
     * @return the configured {@link PeopleFlowService}      */
    public PeopleFlowService getPeopleFlowService() {
        if (peopleFlowService == null) {
            peopleFlowService = KewApiServiceLocator.getPeopleFlowService();
        }

        return peopleFlowService;
    }

    /**
     * inject the {@link PeopleFlowService} to use internally.
     * @param peopleFlowService
     */
    public void setPeopleFlowService(PeopleFlowService peopleFlowService) {
        this.peopleFlowService = peopleFlowService;
    }

    private static class PeopleFlowAction implements Action {

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
