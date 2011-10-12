package org.kuali.rice.krms.impl.peopleflow;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.BasicExecutionEnvironment;
import org.kuali.rice.krms.framework.engine.TermResolutionEngineImpl;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.springframework.orm.ObjectRetrievalFailureException;

import javax.jws.WebParam;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit test for the {@link PeopleFlowActionTypeService}
 */
public class PeopleFlowActionTypeServiceTest {

    private final PeopleFlowActionTypeService notificationPFATS = PeopleFlowActionTypeService.getInstance(
            PeopleFlowActionTypeService.Type.NOTIFICATION);

    private final PeopleFlowActionTypeService approvalPFATS = PeopleFlowActionTypeService.getInstance(
            PeopleFlowActionTypeService.Type.APPROVAL);

    private static final String VALID_PEOPLEFLOW_ID_1 = "myBogusPeopleFlowId1";
    private static final String VALID_PEOPLEFLOW_ID_2 = "myBogusPeopleFlowId2";
    private static final String INVALID_PEOPLEFLOW_ID = "invalidPeopleFlowId";

    @Test(expected = RiceIllegalArgumentException.class) public void testNullActionDefinition() {
        // should throw exception, NOT return null
        notificationPFATS.loadAction(null);
    }

    @Test public void testActionExecution() {

        // dummy up an ActionDefinition

        ActionDefinition.Builder actionDefinitionBuilder =
                ActionDefinition.Builder.create("myId", "myName", "myNamespace", "myTypeId", "myRuleId", 0);

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(PeopleFlowActionTypeService.ATTRIBUTE_FIELD_NAME, VALID_PEOPLEFLOW_ID_1);

        actionDefinitionBuilder.setAttributes(attributes);

        // create an ExecutionEnvironment

        SelectionCriteria sc1 =
                SelectionCriteria.createCriteria("foo", new DateTime(), Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap());

        ExecutionEnvironment ee = new BasicExecutionEnvironment(sc1, Collections.<Term, Object>emptyMap(), new ExecutionOptions(), new TermResolutionEngineImpl());

        // load a notification action
        Action notificationAction = notificationPFATS.loadAction(actionDefinitionBuilder.build());

        notificationAction.execute(ee);

        // change the peopleFlow id
        attributes.clear();
        attributes.put(PeopleFlowActionTypeService.ATTRIBUTE_FIELD_NAME, VALID_PEOPLEFLOW_ID_2);
        actionDefinitionBuilder.setAttributes(attributes);

        // load an approval action
        Action approvalAction = approvalPFATS.loadAction(actionDefinitionBuilder.build());

        approvalAction.execute(ee);

        // get our attribute for comparison
        String selectedPeopleFlows =
                (String)ee.getEngineResults().getAttribute(PeopleFlowActionTypeService.PEOPLE_FLOWS_SELECTED_ATTRIBUTE);

        // compare against our expected output:
        assertEquals("F:myBogusPeopleFlowId1,A:myBogusPeopleFlowId2", selectedPeopleFlows);

        // TODO: test ActionDefinition w/o the attribute we need

        actionDefinitionBuilder.setAttributes(Collections.<String, String>emptyMap());

        try {
            approvalPFATS.loadAction(actionDefinitionBuilder.build());
            fail("should have blown up since the attribute we need isn't in the ActionDefinition");
        } catch (RiceIllegalArgumentException e) {
            // good
        }

    }

    @Test public void testValidateAttributes() {

        ActionTypeService peopleFlowActionTypeService =
                PeopleFlowActionTypeService.getInstance(PeopleFlowActionTypeService.Type.NOTIFICATION);

        // inject our mock PeopleFlowService:
        ((PeopleFlowActionTypeService)peopleFlowActionTypeService).setPeopleFlowService(mockPeopleFlowService);

        // set up attributes with an "invalid" peopleflow ID (according to our mock PeopleFlowService)
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(PeopleFlowActionTypeService.ATTRIBUTE_FIELD_NAME, INVALID_PEOPLEFLOW_ID);

        // test null ID parameter
        try {
            peopleFlowActionTypeService.validateAttributes(null, attributes);
            fail("null type id should throw an exception");
        } catch (RiceIllegalArgumentException e) {
            // good
        }

        // test null attributes parameter
        try {
            peopleFlowActionTypeService.validateAttributes("bogusTypeId", null);
            fail("null type id should throw an exception");
        } catch (RiceIllegalArgumentException e) {
            // good
        }

        // this should create errors
        List<RemotableAttributeError> errors = peopleFlowActionTypeService.validateAttributes("bogusTypeId", attributes);

        assertFalse(CollectionUtils.isEmpty(errors));

        // this should create errors
        errors = peopleFlowActionTypeService.validateAttributesAgainstExisting("bogusTypeId", attributes,
                Collections.<String, String>emptyMap());

        assertFalse(CollectionUtils.isEmpty(errors));

        // reset attribute to have a "valid" peopleFlow ID (according to our mock PeopleFlowService)

        attributes.clear();
        attributes.put(PeopleFlowActionTypeService.ATTRIBUTE_FIELD_NAME, VALID_PEOPLEFLOW_ID_1);

        // this should not create any errors
        errors = peopleFlowActionTypeService.validateAttributes("bogusTypeId", attributes);

        assertTrue(CollectionUtils.isEmpty(errors));

        // this should not create any errors
        errors = peopleFlowActionTypeService.validateAttributesAgainstExisting("bogusTypeId", attributes,
                Collections.<String, String>emptyMap());

        assertTrue(CollectionUtils.isEmpty(errors));

    }

    private final PeopleFlowService mockPeopleFlowService = new PeopleFlowService() {

        private Set<String> validPeopleFlowIds = new HashSet<String>();
        {
            validPeopleFlowIds.add(VALID_PEOPLEFLOW_ID_1);
            validPeopleFlowIds.add(VALID_PEOPLEFLOW_ID_2);
        }

        @Override
        public PeopleFlowDefinition getPeopleFlow(@WebParam(name = "peopleFlowId") String peopleFlowId) throws RiceIllegalArgumentException {
            if (validPeopleFlowIds.contains(peopleFlowId)) {
                return PeopleFlowDefinition.Builder.create("myNamespace", "myPeopleFlowName").build();
            } else {
                // simulate what our PeopleFlowServiceImpl would do
                throw new ObjectRetrievalFailureException("", new RuntimeException());
            }
        }

        @Override
        public PeopleFlowDefinition getPeopleFlowByName(@WebParam(name = "namespaceCode") String namespaceCode,
                @WebParam(name = "name") String name) throws RiceIllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PeopleFlowDefinition createPeopleFlow(@WebParam(
                name = "peopleFlow") PeopleFlowDefinition peopleFlow) throws RiceIllegalArgumentException, RiceIllegalStateException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PeopleFlowDefinition updatePeopleFlow(@WebParam(
                name = "peopleFlow") PeopleFlowDefinition peopleFlow) throws RiceIllegalArgumentException, RiceIllegalStateException {
            throw new UnsupportedOperationException();
        }
    };

}
