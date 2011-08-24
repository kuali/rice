package org.kuali.rice.krms.impl.util;

/**
 * Created by IntelliJ IDEA.
 * User: gilesp
 * Date: 8/24/11
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class KrmsImplConstants {


    public final static class WebPaths {
        public static final String AGENDA_EDITOR_PATH = "krmsAgendaEditor";
    }

    /**
     * This class contains constants associated with the KRMS Repository
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     *
     */
    public static final class PropertyNames {

        public static final class Action {
            public static final String ACTION_ID ="actionId";
        }

        public static final class Agenda {
            public static final String ID = "id";
            public static final String AGENDA_ID ="agendaId";
        }

        public static final class Context {
            public static final String CONTEXT_ID ="contextId";
            public static final String NAME = "name";
            public static final String NAMESPACE = "namespace";
            public static final String ATTRIBUTE_BOS = "attributeBos";
        }

        public static final class Rule {
            public static final String RULE_ID ="ruleId";
        }

        public static final class KrmsAttributeDefinition {
            public static final String NAME = "name";
            public static final String NAMESPACE = "namespace";
        }

        public static final class BaseAttribute {
            public static final String ATTRIBUTE_DEFINITION_ID = "attributeDefinitionId";
            public static final String VALUE = "value";
            public static final String ATTRIBUTE_DEFINITION = "attributeDefinition";
        }

    }

}
