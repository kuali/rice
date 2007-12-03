/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.xml;

import org.jdom.Namespace;

/**
 * Constants for various XML namespaces, elements and attributes for the various parsers.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface XmlConstants {

    // namespaces
    public static final Namespace WORKFLOW_NAMESPACE = Namespace.getNamespace("", "ns:workflow");
    public static final Namespace SCHEMA_NAMESPACE = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    public static final Namespace RULE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/Rule");
    public static final Namespace DOCUMENT_TYPE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/DocumentType");
    public static final Namespace WORKGROUP_NAMESPACE = Namespace.getNamespace("", "ns:workflow/Workgroup");
    public static final Namespace WORKGROUP_TYPE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/WorkgroupType");
    public static final Namespace RULE_TEMPLATE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/RuleTemplate");
    public static final Namespace RULE_ATTRIBUTE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/RuleAttribute");
    public static final Namespace HELP_NAMESPACE = Namespace.getNamespace("", "ns:workflow/Help");
    public static final Namespace EDL_NAMESPACE = Namespace.getNamespace("", "ns:workflow/EDocLite");
    public static final Namespace STYLE_NAMESPACE = Namespace.getNamespace("", "ns:workflow/Style");

    // schemas
    public static final String SCHEMA_LOCATION_ATTR = "schemaLocation";
    public static final String WORKFLOW_SCHEMA_LOCATION = "ns:workflow resource:WorkflowData";
    public static final String RULE_SCHEMA_LOCATION = "ns:workflow/Rule resource:Rule";
    public static final String DOCUMENT_TYPE_SCHEMA_LOCATION = "ns:workflow/DocumentType resource:DocumentType";
    public static final String WORKGROUP_SCHEMA_LOCATION = "ns:workflow/Workgroup resource:Workgroup";
    public static final String WORKGROUP_TYPE_SCHEMA_LOCATION = "ns:workflow/WorkgroupType resource:WorkgroupType";
    public static final String RULE_TEMPLATE_SCHEMA_LOCATION = "ns:workflow/RuleTemplate resource:RuleTemplate";
    public static final String RULE_ATTRIBUTE_SCHEMA_LOCATION = "ns:workflow/RuleAttribute resource:RuleAttribute";
    public static final String HELP_SCHEMA_LOCATION = "ns:workflow/Help resource:Help";
    public static final String EDL_SCHEMA_LOCATION = "ns:workflow/EDocLite resource:EDocLite";
    public static final String STYLE_SCHEMA_LOCATION = "ns:workflow/Style resource:Style";

    // data
    public static final String DATA_ELEMENT = "data";

    // general
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String LABEL = "label";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String ACTIVE= "active";
    public static final String INHERITED = "inherited";
    public static final String TYPE = "type";

    // document types
    public static final String DOCUMENT_TYPES = "documentTypes";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String PARENT = "parent";
    public static final String MESSAGE_ENTITY = "messageEntity";
    public static final String POST_PROCESSOR_NAME = "postProcessorName";
    public static final String SUPER_USER_WORKGROUP_NAME = "superUserWorkgroupName";
    public static final String BLANKET_APPROVE_WORKGROUP_NAME = "blanketApproveWorkgroupName";
    public static final String BLANKET_APPROVE_POLICY = "blanketApprovePolicy";
    public static final String DEFAULT_EXCEPTION_WORKGROUP_NAME = "defaultExceptionWorkgroupName";
    public static final String DOC_HANDLER = "docHandler";
    public static final String NOTIFICATION_FROM_ADDRESS = "notificationFromAddress";
    public static final String CUSTOM_EMAIL_STYLESHEET = "emailStylesheet";
    public static final String ROUTE_LEVELS_INHERITED = "routeLevelsInherited";
    public static final String ATTRIBUTES_INHERITED = "attributesInherited";
    public static final String POLICIES = "policies";
    public static final String POLICY = "policy";
    public static final String SECURITY = "security";
    public static final String ROUTING_VERSION = "routingVersion";
    public static final String ROUTE_PATHS = "routePaths";
    public static final String ROUTE_PATH = "routePath";
    public static final String INITIAL_NODE = "initialNode";
    public static final String PROCESS_NAME = "processName";
    public static final String ROUTE_NODES = "routeNodes";
    public static final String BRANCH = "branch";
    public static final String EXCEPTION_WORKGROUP_NAME = "exceptionWorkgroupName";
    public static final String ACTIVATION_TYPE = "activationType";
    public static final String FINAL_APPROVAL = "finalApproval";
    public static final String MANDATORY_ROUTE = "mandatoryRoute";
    public static final String ROUTE_MODULE = "routeModule";
    public static final String NEXT_NODE = "nextNode";

    // rules
    public static final String RULES = "rules";
    public static final String RULE = "rule";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String IGNORE_PREVIOUS = "ignorePrevious";
    public static final String RESPONSIBILITIES = "responsibilities";
    public static final String RESPONSIBILITY = "responsibility";
    public static final String ACTION_REQUESTED = "actionRequested";
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String APPROVE_POLICY = "approvePolicy";
    public static final String PRIORITY = "priority";
    public static final String DELEGATIONS = "delegations";
    public static final String DELEGATION_TYPE = "delegationType";
    public static final String RULE_EXTENSIONS = "ruleExtensions";
    public static final String RULE_EXTENSION = "ruleExtension";
    public static final String RULE_EXTENSION_VALUES = "ruleExtensionValues";
    public static final String RULE_EXTENSION_VALUE = "ruleExtensionValue";

    // workgroups, most of the elements are not known to the core and are dictated by the institutional plugin
    public static final String WORKGROUPS = "workgroups";
    public static final String WORKGROUP = "workgroup";
    public static final String EXTENSIONS = "extensions";
    public static final String EXTENSION = "extension";
    public static final String DATA = "data";

    // workgroup types
    public static final String WORKGROUP_TYPES = "workgroupTypes";
    public static final String WORKGROUP_TYPE = "workgroupType";

    // rule templates
    public static final String RULE_TEMPLATES = "ruleTemplates";
    public static final String RULE_TEMPLATE = "ruleTemplate";
    public static final String DELEGATION_TEMPLATE = "delegationTemplate";
    public static final String REQUIRED = "required";
    public static final String ATTRIBUTES = "attributes";
    public static final String ATTRIBUTE = "attribute";
    public static final String RULE_DEFAULTS = "ruleDefaults";
    public static final String RULE_INSTRUCTIONS = "ruleInstructions";
    public static final String DEFAULT_ACTION_REQUESTED = "defaultActionRequested";
    public static final String SUPPORTS_COMPLETE = "supportsComplete";
    public static final String SUPPORTS_APPROVE = "supportsApprove";
    public static final String SUPPORTS_ACKNOWLEDGE = "supportsAcknowledge";
    public static final String SUPPORTS_FYI = "supportsFYI";

    // rule attributes
    public static final String RULE_ATTRIBUTES = "ruleAttributes";
    public static final String RULE_ATTRIBUTE = "ruleAttribute";
    public static final String CLASS_NAME = "className";
    public static final String ROUTING_CONFIG = "routingConfig";
    public static final String SEARCHING_CONFIG = "searchingConfig";

    // help
    public static final String HELP_ENTRIES = "helpEntries";
    public static final String HELP_ENTRY = "helpEntry";
    public static final String HELP_NAME = "helpName";
    public static final String HELP_KEY = "helpKey";
    public static final String HELP_TEXT = "helpText";

    //edoclite
    public static final String EDL_EDOCLITE = "edoclite";
    public static final String EDL_STYLE = "style";
    public static final String EDL_ASSOCIATION = "association";
    public static final String EDL_DOC_TYPE = "docType";
    public static final String EDL_DEFINITION = "definition";
    public static final String EDL_ACTIVE = "active";

    //style
    public static final String STYLE_STYLES = "styles";
    public static final String STYLE_STYLE = "style";
}
